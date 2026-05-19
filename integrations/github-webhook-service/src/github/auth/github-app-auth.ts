import crypto from "node:crypto";
import { env } from "../../config/env";
import { logger } from "../../logging/logger";
import {
  installationAccessTokenResponseSchema,
  type InstallationAccessTokenResponse
} from "../types/github-auth";

interface CachedInstallationToken {
  token: string;
  expiresAtMs: number;
}

const tokenCache = new Map<number, CachedInstallationToken>();
const CACHE_SAFETY_WINDOW_MS = 60_000;

function base64UrlEncode(input: string): string {
  return Buffer.from(input)
    .toString("base64")
    .replace(/=/g, "")
    .replace(/\+/g, "-")
    .replace(/\//g, "_");
}

export function buildGitHubAppJwt(nowEpochSeconds = Math.floor(Date.now() / 1000)): string {
  const header = { alg: "RS256", typ: "JWT" };
  const payload = {
    iat: nowEpochSeconds - 60,
    exp: nowEpochSeconds + 9 * 60,
    iss: env.GITHUB_APP_ID
  };

  const encodedHeader = base64UrlEncode(JSON.stringify(header));
  const encodedPayload = base64UrlEncode(JSON.stringify(payload));
  const body = `${encodedHeader}.${encodedPayload}`;

  const privateKey = env.GITHUB_APP_PRIVATE_KEY.replace(/\\n/g, "\n");

  const signature = crypto
    .createSign("RSA-SHA256")
    .update(body)
    .sign(privateKey, "base64")
    .replace(/=/g, "")
    .replace(/\+/g, "-")
    .replace(/\//g, "_");

  return `${body}.${signature}`;
}

function parseExpiresAtMs(expiresAtIso: string): number {
  const expiresAtMs = Date.parse(expiresAtIso);
  if (!Number.isFinite(expiresAtMs)) {
    throw new Error(`Invalid installation token expires_at: ${expiresAtIso}`);
  }
  return expiresAtMs;
}

function getCachedToken(installationId: number): string | null {
  const cached = tokenCache.get(installationId);
  if (!cached) {
    return null;
  }

  if (Date.now() + CACHE_SAFETY_WINDOW_MS >= cached.expiresAtMs) {
    tokenCache.delete(installationId);
    return null;
  }

  return cached.token;
}

async function requestInstallationToken(installationId: number, appJwt: string): Promise<InstallationAccessTokenResponse> {
  const response = await fetch(`https://api.github.com/app/installations/${installationId}/access_tokens`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${appJwt}`,
      Accept: "application/vnd.github+json",
      "User-Agent": "prflow-github-webhook-service"
    }
  });

  const text = await response.text();

  if (!response.ok) {
    const retryAfter = response.headers.get("retry-after");
    logger.error("GitHub installation token request failed", {
      installationId,
      status: response.status,
      retryAfter,
      responseBody: text
    });
    throw new Error(`Failed to fetch installation token (${response.status})`);
  }

  const json = JSON.parse(text) as unknown;
  return installationAccessTokenResponseSchema.parse(json);
}

export async function getInstallationAccessToken(installationId: number): Promise<string> {
  const cached = getCachedToken(installationId);
  if (cached) {
    logger.info("Using cached installation token", { installationId });
    return cached;
  }

  const appJwt = buildGitHubAppJwt();
  const tokenResponse = await requestInstallationToken(installationId, appJwt);
  const expiresAtMs = parseExpiresAtMs(tokenResponse.expires_at);

  tokenCache.set(installationId, {
    token: tokenResponse.token,
    expiresAtMs
  });

  logger.info("Fetched new installation token", {
    installationId,
    expiresAt: tokenResponse.expires_at
  });

  return tokenResponse.token;
}

export function clearInstallationTokenCache(): void {
  tokenCache.clear();
}
