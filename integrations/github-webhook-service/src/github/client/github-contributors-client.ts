import { logger } from "../../logging/logger";
import type { GithubContributor } from "../types/contributor";

export type ContributorsFetchErrorCode =
  | "UNAUTHORIZED"
  | "FORBIDDEN"
  | "NOT_FOUND"
  | "RATE_LIMITED"
  | "NETWORK"
  | "GITHUB_API"
  | "PARSE_ERROR";

export class ContributorsFetchError extends Error {
  constructor(
    public readonly code: ContributorsFetchErrorCode,
    message: string,
    public readonly status?: number,
    public readonly retryAfterSeconds?: number,
  ) {
    super(message);
    this.name = "ContributorsFetchError";
  }
}

interface FetchContributorsInput {
  owner: string;
  repo: string;
  installationToken: string;
  deliveryId: string;
}

function toRetryAfterSeconds(headerValue: string | null): number | undefined {
  if (!headerValue) return undefined;
  const seconds = Number(headerValue);
  return Number.isFinite(seconds) ? seconds : undefined;
}

function classifyHttpFailure(
  status: number,
  responseBody: string,
  retryAfter: number | undefined,
): ContributorsFetchError {
  if (status === 401)
    return new ContributorsFetchError(
      "UNAUTHORIZED",
      `Invalid or expired installation token: ${responseBody}`,
      status,
    );
  if (status === 403) {
    if (retryAfter !== undefined)
      return new ContributorsFetchError(
        "RATE_LIMITED",
        `GitHub rate limit hit: ${responseBody}`,
        status,
        retryAfter,
      );
    return new ContributorsFetchError(
      "FORBIDDEN",
      `Repository access forbidden for installation: ${responseBody}`,
      status,
    );
  }
  if (status === 404)
    return new ContributorsFetchError(
      "NOT_FOUND",
      `Repository not found: ${responseBody}`,
      status,
    );
  if (status === 429)
    return new ContributorsFetchError(
      "RATE_LIMITED",
      `GitHub rate limited request: ${responseBody}`,
      status,
      retryAfter,
    );
  return new ContributorsFetchError(
    "GITHUB_API",
    `GitHub API ${status}: ${responseBody}`,
    status,
    retryAfter,
  );
}

async function sleep(ms: number): Promise<void> {
  await new Promise((resolve) => setTimeout(resolve, ms));
}

export async function fetchRepositoryContributors(
  input: FetchContributorsInput,
): Promise<GithubContributor[]> {
  const { owner, repo, installationToken, deliveryId } = input;
  const perPage = 100;
  const results: GithubContributor[] = [];

  let url = `https://api.github.com/repos/${owner}/${repo}/contributors?per_page=${perPage}`;
  const maxAttempts = 3;

  for (let attempt = 1; attempt <= maxAttempts; attempt += 1) {
    try {
      // iterate pages
      while (url) {
        const response = await fetch(url, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${installationToken}`,
            Accept: "application/vnd.github+json",
            "User-Agent": "prflow-github-webhook-service",
          },
        });

        const retryAfter = toRetryAfterSeconds(
          response.headers.get("retry-after"),
        );
        const bodyText = await response.text();

        if (!response.ok) {
          throw classifyHttpFailure(response.status, bodyText, retryAfter);
        }

        const parsed = JSON.parse(bodyText) as unknown;
        if (!Array.isArray(parsed)) {
          throw new ContributorsFetchError(
            "PARSE_ERROR",
            "Unexpected contributors response shape",
          );
        }

        const page = parsed as GithubContributor[];
        results.push(...page);

        logger.info("Fetched contributors page from GitHub", {
          deliveryId,
          owner,
          repo,
          attempt,
          pageCount: page.length,
        });

        // parse Link header for next
        const link = response.headers.get("link");
        if (link) {
          const match = link.match(/<([^>]+)>; rel="next"/);
          if (match) {
            url = match[1];
            continue;
          }
        }
        // no next
        url = "";
      }

      logger.info("Fetched all contributors from GitHub", {
        deliveryId,
        owner,
        repo,
        total: results.length,
      });
      return results;
    } catch (error) {
      const normalized =
        error instanceof ContributorsFetchError
          ? error
          : error instanceof SyntaxError
            ? new ContributorsFetchError("PARSE_ERROR", error.message)
            : new ContributorsFetchError(
                "NETWORK",
                error instanceof Error ? error.message : "network error",
              );

      const retriable =
        attempt < maxAttempts &&
        (normalized.code === "RATE_LIMITED" ||
          normalized.code === "NETWORK" ||
          normalized.code === "GITHUB_API");

      logger.warn("Failed to fetch repository contributors", {
        deliveryId,
        owner,
        repo,
        attempt,
        retriable,
        errorCode: normalized.code,
        status: normalized.status,
        retryAfterSeconds: normalized.retryAfterSeconds,
        error: normalized.message,
      });

      if (!retriable) throw normalized;

      const backoffMs = normalized.retryAfterSeconds
        ? normalized.retryAfterSeconds * 1000
        : 300 * Math.pow(2, attempt - 1);
      await sleep(backoffMs);
    }
  }

  throw new ContributorsFetchError(
    "GITHUB_API",
    "Unexpected contributors fetch termination",
  );
}
