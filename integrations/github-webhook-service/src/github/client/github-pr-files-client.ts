import { logger } from "../../logging/logger";
import { githubPrFilesSchema, type GithubPullRequestFile } from "../types/pr-files";

export type PrFilesFetchErrorCode =
  | "UNAUTHORIZED"
  | "FORBIDDEN"
  | "NOT_FOUND"
  | "RATE_LIMITED"
  | "NETWORK"
  | "GITHUB_API"
  | "PARSE_ERROR";

export class PrFilesFetchError extends Error {
  constructor(
    public readonly code: PrFilesFetchErrorCode,
    message: string,
    public readonly status?: number,
    public readonly retryAfterSeconds?: number
  ) {
    super(message);
    this.name = "PrFilesFetchError";
  }
}

interface FetchPrFilesInput {
  owner: string;
  repo: string;
  pullNumber: number;
  installationToken: string;
  deliveryId: string;
}

function toRetryAfterSeconds(headerValue: string | null): number | undefined {
  if (!headerValue) {
    return undefined;
  }
  const seconds = Number(headerValue);
  return Number.isFinite(seconds) ? seconds : undefined;
}

function isRetriableError(error: PrFilesFetchError): boolean {
  return error.code === "RATE_LIMITED" || error.code === "NETWORK" || error.code === "GITHUB_API";
}

async function sleep(ms: number): Promise<void> {
  await new Promise((resolve) => setTimeout(resolve, ms));
}

function classifyHttpFailure(status: number, responseBody: string, retryAfter: number | undefined): PrFilesFetchError {
  if (status === 401) {
    return new PrFilesFetchError("UNAUTHORIZED", `Invalid or expired installation token: ${responseBody}`, status);
  }
  if (status === 403) {
    if (retryAfter !== undefined) {
      return new PrFilesFetchError("RATE_LIMITED", `GitHub rate limit hit: ${responseBody}`, status, retryAfter);
    }
    return new PrFilesFetchError("FORBIDDEN", `Repository access forbidden for installation: ${responseBody}`, status);
  }
  if (status === 404) {
    return new PrFilesFetchError("NOT_FOUND", `Repository or pull request not found: ${responseBody}`, status);
  }
  if (status === 429) {
    return new PrFilesFetchError("RATE_LIMITED", `GitHub rate limited request: ${responseBody}`, status, retryAfter);
  }
  return new PrFilesFetchError("GITHUB_API", `GitHub API ${status}: ${responseBody}`, status, retryAfter);
}

export async function fetchPullRequestFiles(input: FetchPrFilesInput): Promise<GithubPullRequestFile[]> {
  const { owner, repo, pullNumber, installationToken, deliveryId } = input;
  const maxAttempts = 3;

  for (let attempt = 1; attempt <= maxAttempts; attempt += 1) {
    try {
      const response = await fetch(`https://api.github.com/repos/${owner}/${repo}/pulls/${pullNumber}/files?per_page=100`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${installationToken}`,
          Accept: "application/vnd.github+json",
          "User-Agent": "prflow-github-webhook-service"
        }
      });

      const retryAfter = toRetryAfterSeconds(response.headers.get("retry-after"));
      const bodyText = await response.text();

      if (!response.ok) {
        throw classifyHttpFailure(response.status, bodyText, retryAfter);
      }

      const parsedJson = JSON.parse(bodyText) as unknown;
      const files = githubPrFilesSchema.parse(parsedJson);

      logger.info("Fetched PR files from GitHub", {
        deliveryId,
        owner,
        repo,
        pullNumber,
        fileCount: files.length,
        attempt
      });

      return files;
    } catch (error) {
      const normalizedError =
        error instanceof PrFilesFetchError
          ? error
          : error instanceof SyntaxError
            ? new PrFilesFetchError("PARSE_ERROR", error.message)
            : new PrFilesFetchError("NETWORK", error instanceof Error ? error.message : "network failure");

      const retriable = attempt < maxAttempts && isRetriableError(normalizedError);

      logger.warn("Failed to fetch PR files", {
        deliveryId,
        owner,
        repo,
        pullNumber,
        attempt,
        retriable,
        errorCode: normalizedError.code,
        status: normalizedError.status,
        retryAfterSeconds: normalizedError.retryAfterSeconds,
        error: normalizedError.message
      });

      if (!retriable) {
        throw normalizedError;
      }

      const backoffMs = normalizedError.retryAfterSeconds
        ? normalizedError.retryAfterSeconds * 1000
        : 300 * Math.pow(2, attempt - 1);

      await sleep(backoffMs);
    }
  }

  throw new PrFilesFetchError("GITHUB_API", "Unexpected PR files fetch termination");
}
