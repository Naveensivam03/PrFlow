import type { PullRequestEventPayload, PullRequestReviewEventPayload } from "../github/payload";
import type { PrflowEvent, PrStatus } from "./types";

interface NormalizeInputBase {
  deliveryId: string;
  eventName: string;
}

function resolveStatus(action: PullRequestEventPayload["action"], mergedAt: string | null | undefined): PrStatus {
  if (action === "closed") {
    return mergedAt ? "MERGED" : "CLOSED";
  }
  return "OPEN";
}

export function normalizePullRequestEvent(input: NormalizeInputBase & { payload: PullRequestEventPayload }): PrflowEvent | null {
  const { action, pull_request: pr, repository, organization, installation } = input.payload;
  const tenantOrgId = organization?.id ?? repository.owner.id;

  const base = {
    organizationId: String(tenantOrgId),
    repositoryId: String(repository.id),
    githubPrNumber: pr.number,
    occurredAt: action === "closed" ? pr.closed_at ?? pr.updated_at : pr.updated_at,
    deliveryId: input.deliveryId,
    rawEvent: input.eventName,
    pullRequest: {
      githubRepoId: repository.id,
      githubOrgId: tenantOrgId,
      installationId: installation?.id,
      owner: repository.owner.login,
      repo: repository.name,
      title: pr.title,
      description: pr.body ?? null,
      status: resolveStatus(action, pr.merged_at),
      openedAt: pr.created_at,
      mergedAt: pr.merged_at ?? null,
      closedAt: pr.closed_at ?? null,
      author: {
        githubUserId: pr.user.id,
        username: pr.user.login,
        displayName: pr.user.name ?? null,
        avatarUrl: pr.user.avatar_url ?? null
      }
    }
  };

  if (action === "opened") {
    return { ...base, eventType: "PULL_REQUEST_OPENED" };
  }

  if (action === "synchronize") {
    return { ...base, eventType: "PULL_REQUEST_SYNCHRONIZED" };
  }

  if (action === "closed") {
    return { ...base, eventType: "PULL_REQUEST_CLOSED" };
  }

  return null;
}

export function normalizePullRequestReviewEvent(
  input: NormalizeInputBase & { payload: PullRequestReviewEventPayload }
): PrflowEvent | null {
  const { action, review, repository, organization, pull_request: pr } = input.payload;
  const tenantOrgId = organization?.id ?? repository.owner.id;

  if (action !== "submitted") {
    return null;
  }

  return {
    eventType: "PULL_REQUEST_REVIEW_SUBMITTED",
    organizationId: String(tenantOrgId),
    repositoryId: String(repository.id),
    githubPrNumber: pr.number,
    occurredAt: review.submitted_at,
    deliveryId: input.deliveryId,
    rawEvent: input.eventName
  };
}
