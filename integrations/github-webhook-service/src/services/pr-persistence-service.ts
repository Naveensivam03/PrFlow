import { InMemoryEventDispatcher } from "../events/dispatcher";
import type { PrflowEvent, PrStatus } from "../events/types";
import { getInstallationAccessToken } from "../github/app-auth";
import {
  fetchPullRequestFiles,
  type GithubPullRequestFile,
} from "../github/github-api-client";
import { fetchRepositoryContributors } from "../github/client/github-contributors-client";
import { logger } from "../logging/logger";
import { extractScopeFromFilePath } from "../scope/scope-extractor";

interface InsertedRow {
  id: number;
}

function requireInsertedId(rows: InsertedRow[], entityName: string): number {
  const row = rows[0];
  if (!row) {
    throw new Error(`Failed to upsert ${entityName}: no id returned`);
  }
  return row.id;
}

function mapPrStatus(status: PrStatus): "OPEN" | "CLOSED" | "MERGED" {
  if (status === "MERGED") {
    return "MERGED";
  }
  if (status === "CLOSED") {
    return "CLOSED";
  }
  return "OPEN";
}

function mapChangeType(fileStatus: string): string {
  return fileStatus.toUpperCase();
}

function deduplicateFilesByPath(
  files: GithubPullRequestFile[],
): GithubPullRequestFile[] {
  const byPath = new Map<string, GithubPullRequestFile>();
  for (const file of files) {
    byPath.set(file.filename, file);
  }
  return Array.from(byPath.values());
}

export class PullRequestPersistenceService {
  constructor(private readonly dispatcher: InMemoryEventDispatcher) {}

  async handlePullRequestOpened(event: PrflowEvent): Promise<void> {
    if (event.eventType !== "PULL_REQUEST_OPENED" || !event.pullRequest) {
      return;
    }

    const pr = event.pullRequest;
    const installationId = pr.installationId;
    const githubOrgId = pr.githubOrgId ?? Number(event.organizationId);

    if (!installationId) {
      throw new Error("Missing installation information for PR persistence");
    }
    if (!Number.isFinite(githubOrgId)) {
      throw new Error(
        "Missing tenant organization identifier for PR persistence",
      );
    }

    logger.info("Starting PR persistence workflow", {
      deliveryId: event.deliveryId,
      githubPrNumber: event.githubPrNumber,
      owner: pr.owner,
      repo: pr.repo,
    });

    const installationToken = await getInstallationAccessToken(installationId);
    const files = await fetchPullRequestFiles({
      owner: pr.owner,
      repo: pr.repo,
      pullNumber: event.githubPrNumber,
      installationToken,
      deliveryId: event.deliveryId,
    });
    const uniqueFiles = deduplicateFilesByPath(files);
    let organizationId: number = 0;
    let repositoryId: number = 0;
    let pullRequestId: number = 0;
    let needContributorSync = false;

    await Bun.sql.begin(async (tx) => {
      await this.acquireWorkflowLock(tx, {
        owner: pr.owner,
        repo: pr.repo,
        githubPrNumber: event.githubPrNumber,
      });

      logger.info("PR persistence checkpoint", {
        deliveryId: event.deliveryId,
        githubPrNumber: event.githubPrNumber,
        checkpoint: "transaction_started",
      });

      organizationId = await this.upsertOrganization(tx, {
        githubInstallationId: installationId,
        githubOrgId,
        name: pr.owner,
      });

      repositoryId = await this.upsertRepository(tx, {
        organizationId,
        githubRepoId: pr.githubRepoId,
        name: pr.repo,
      });

      // detect whether repository contributors have been synchronized before
      const existing =
        (await tx`SELECT 1 as exists FROM repository_developers WHERE repository_id = ${repositoryId} LIMIT 1`) as {
          exists?: number;
        }[];
      needContributorSync = existing.length === 0;

      const authorId = await this.upsertDeveloper(tx, {
        organizationId,
        githubUserId: pr.author.githubUserId,
        username: pr.author.username,
        displayName: pr.author.displayName,
        avatarUrl: pr.author.avatarUrl,
      });

      pullRequestId = await this.upsertPullRequest(tx, {
        repositoryId,
        authorId,
        githubPrNumber: event.githubPrNumber,
        title: pr.title,
        description: pr.description,
        status: mapPrStatus(pr.status),
        openedAt: pr.openedAt,
        mergedAt: pr.mergedAt,
        closedAt: pr.closedAt,
      });

      await this.replacePullRequestFiles(tx, pullRequestId, uniqueFiles);

      logger.info("PR persistence checkpoint", {
        deliveryId: event.deliveryId,
        githubPrNumber: event.githubPrNumber,
        checkpoint: "files_persisted",
        fileCount: uniqueFiles.length,
        needContributorSync,
      });
    });

    // Perform contributor synchronization outside the main transaction to avoid long-running DB locks
    if (needContributorSync) {
      try {
        logger.info("Starting contributor synchronization", {
          deliveryId: event.deliveryId,
          owner: pr.owner,
          repo: pr.repo,
          repositoryId,
        });

        const contributors = await fetchRepositoryContributors({
          owner: pr.owner,
          repo: pr.repo,
          installationToken: installationToken,
          deliveryId: event.deliveryId,
        });

        logger.info("GitHub contributors fetched", {
          deliveryId: event.deliveryId,
          owner: pr.owner,
          repo: pr.repo,
          contributorCount: contributors.length,
        });

        // persist contributors and relationships in a fresh transaction
        await Bun.sql.begin(async (tx) => {
          // lock repository-level work to be replay-safe
          await tx`SELECT pg_advisory_xact_lock(hashtext(${`${pr.owner}/${pr.repo}#contributors`}))`;

          for (const c of contributors) {
            const developerId = await this.upsertDeveloper(tx, {
              organizationId: organizationId,
              githubUserId: c.id,
              username: c.login,
              displayName: null,
              avatarUrl: c.avatar_url ?? null,
            });

            // upsert repository_developers
            await tx`
              INSERT INTO repository_developers (repository_id, developer_id, contribution_count, last_contributed_at)
              VALUES (${repositoryId}, ${developerId}, ${c.contributions}, CURRENT_TIMESTAMP)
              ON CONFLICT (repository_id, developer_id)
              DO UPDATE SET
                contribution_count = EXCLUDED.contribution_count,
                last_contributed_at = GREATEST(repository_developers.last_contributed_at, EXCLUDED.last_contributed_at),
                updated_at = CURRENT_TIMESTAMP
            `;

            logger.info("Repository relationship persisted", {
              deliveryId: event.deliveryId,
              repositoryId,
              developerId,
              contributions: c.contributions,
            });
          }
        });

        logger.info("Contributor synchronization completed", {
          deliveryId: event.deliveryId,
          owner: pr.owner,
          repo: pr.repo,
          repositoryId,
        });
      } catch (error) {
        logger.warn("Contributor synchronization failed", {
          deliveryId: event.deliveryId,
          owner: pr.owner,
          repo: pr.repo,
          error: error instanceof Error ? error.message : String(error),
        });
      }
    }

    await this.dispatcher.publish({
      ...event,
      eventType: "PULL_REQUEST_ANALYZED",
      dbPullRequestId: pullRequestId,
      dbRepositoryId: repositoryId,
    });

    logger.info("PR persistence workflow completed", {
      deliveryId: event.deliveryId,
      githubPrNumber: event.githubPrNumber,
    });
  }

  private async acquireWorkflowLock(
    tx: any,
    input: { owner: string; repo: string; githubPrNumber: number },
  ): Promise<void> {
    const lockKey = `${input.owner}/${input.repo}#${input.githubPrNumber}`;
    await tx`SELECT pg_advisory_xact_lock(hashtext(${lockKey}))`;
  }

  private async upsertOrganization(
    tx: any,
    input: { githubInstallationId: number; githubOrgId: number; name: string },
  ): Promise<number> {
    const rows = (await tx`
      INSERT INTO organizations (github_installation_id, github_org_id, name, plan_type, is_active)
      VALUES (${input.githubInstallationId}, ${input.githubOrgId}, ${input.name}, ${"FREE"}, ${true})
      ON CONFLICT (github_org_id)
      DO UPDATE SET
        github_installation_id = EXCLUDED.github_installation_id,
        name = EXCLUDED.name,
        updated_at = CURRENT_TIMESTAMP
      RETURNING id
    `) as InsertedRow[];

    return requireInsertedId(rows, "organization");
  }

  private async upsertRepository(
    tx: any,
    input: { organizationId: number; githubRepoId: number; name: string },
  ): Promise<number> {
    const rows = (await tx`
      INSERT INTO repositories (organization_id, github_repo_id, name, default_branch, expertise_mode, is_active)
      VALUES (${input.organizationId}, ${input.githubRepoId}, ${input.name}, ${"main"}, ${"GENERAL"}, ${true})
      ON CONFLICT (organization_id, github_repo_id)
      DO UPDATE SET
        name = EXCLUDED.name,
        updated_at = CURRENT_TIMESTAMP
      RETURNING id
    `) as InsertedRow[];

    return requireInsertedId(rows, "repository");
  }

  private async upsertDeveloper(
    tx: any,
    input: {
      organizationId: number;
      githubUserId: number;
      username: string;
      displayName: string | null;
      avatarUrl: string | null;
    },
  ): Promise<number> {
    const rows = (await tx`
      INSERT INTO developers (organization_id, github_user_id, username, display_name, avatar_url, is_active)
      VALUES (${input.organizationId}, ${input.githubUserId}, ${input.username}, ${input.displayName}, ${input.avatarUrl}, ${true})
      ON CONFLICT (organization_id, github_user_id)
      DO UPDATE SET
        username = EXCLUDED.username,
        display_name = EXCLUDED.display_name,
        avatar_url = EXCLUDED.avatar_url,
        updated_at = CURRENT_TIMESTAMP
      RETURNING id
    `) as InsertedRow[];

    return requireInsertedId(rows, "developer");
  }

  private async upsertPullRequest(
    tx: any,
    input: {
      repositoryId: number;
      authorId: number;
      githubPrNumber: number;
      title: string;
      description: string | null;
      status: string;
      openedAt: string;
      mergedAt: string | null;
      closedAt: string | null;
    },
  ): Promise<number> {
    const rows = (await tx`
      INSERT INTO pull_requests
        (repository_id, author_id, github_pr_number, title, description, status, opened_at, merged_at, closed_at)
      VALUES
        (${input.repositoryId}, ${input.authorId}, ${input.githubPrNumber}, ${input.title}, ${input.description}, ${input.status}, ${input.openedAt}, ${input.mergedAt}, ${input.closedAt})
      ON CONFLICT (repository_id, github_pr_number)
      DO UPDATE SET
        author_id = EXCLUDED.author_id,
        title = EXCLUDED.title,
        description = EXCLUDED.description,
        status = EXCLUDED.status,
        merged_at = EXCLUDED.merged_at,
        closed_at = EXCLUDED.closed_at,
        updated_at = CURRENT_TIMESTAMP
      RETURNING id
    `) as InsertedRow[];

    return requireInsertedId(rows, "pull_request");
  }

  private async replacePullRequestFiles(
    tx: any,
    pullRequestId: number,
    files: GithubPullRequestFile[],
  ): Promise<void> {
    await tx`DELETE FROM pull_request_files WHERE pull_request_id = ${pullRequestId}`;

    for (const file of files) {
      const scope = extractScopeFromFilePath(file.filename);

      await tx`
        INSERT INTO pull_request_files
          (pull_request_id, file_path, scope_type, scope_identifier, change_type, lines_added, lines_deleted)
        VALUES
          (
            ${pullRequestId},
            ${file.filename},
            ${scope.scopeType},
            ${scope.scopeIdentifier},
            ${mapChangeType(file.status)},
            ${file.additions},
            ${file.deletions}
          )
      `;
    }
  }
}
