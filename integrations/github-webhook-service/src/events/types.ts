export type PrflowEventType =
  | "PULL_REQUEST_OPENED"
  | "PULL_REQUEST_SYNCHRONIZED"
  | "PULL_REQUEST_CLOSED"
  | "PULL_REQUEST_REVIEW_SUBMITTED"
  | "PULL_REQUEST_ANALYZED";

export type PrStatus = "OPEN" | "CLOSED" | "MERGED";

export interface PrflowPullRequestContext {
  githubRepoId: number;
  githubOrgId?: number;
  installationId?: number;
  owner: string;
  repo: string;
  title: string;
  description: string | null;
  status: PrStatus;
  openedAt: string;
  mergedAt: string | null;
  closedAt: string | null;
  author: {
    githubUserId: number;
    username: string;
    displayName: string | null;
    avatarUrl: string | null;
  };
}

export interface PrflowEvent {
  eventType: PrflowEventType;
  organizationId: string;
  repositoryId: string;
  githubPrNumber: number;
  occurredAt: string;
  deliveryId: string;
  rawEvent: string;
  pullRequest?: PrflowPullRequestContext;
}
