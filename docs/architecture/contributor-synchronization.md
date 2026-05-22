Contributor synchronization (Repository Contributor Synchronization)

Overview

This document describes the new Repository Contributor Synchronization feature: fetching GitHub repository contributors and persisting them into PRFlow's developer and repository_developers tables. This enables organizational memory about who contributes to which repositories beyond PR authorship.

Goals
- Fetch contributors from GitHub using the GitHub App installation token
- Persist contributor details into `developers`
- Persist repository ↔ developer relationships into `repository_developers`
- Ensure replay-safe, idempotent synchronization
- Trigger sync only when necessary (on PR opened or when repository first observed)

Key components implemented
- Flyway migration: V11__create_repository_developers.sql (adds repository_developers table)
- GitHub contributors client: integrations/github-webhook-service/src/github/client/github-contributors-client.ts
  - Handles pagination, retries, rate-limit headers, structured logs, and error classification
- Persistence integration: integrations/github-webhook-service/src/services/pr-persistence-service.ts
  - Detects if repository contributors have not been synchronized
  - Fetches contributors (outside long-running DB transactions)
  - Upserts developers and repository_developers with conflict-safe SQL (ON CONFLICT)
  - Uses pg_advisory_xact_lock for repository-scoped synchronization to avoid concurrent/race conditions
  - Emits structured logs for start, fetched, persisted, and failure events

Replay safety and idempotence
- Developer upserts use ON CONFLICT (organization_id, github_user_id) to update canonical fields without duplicating rows
- Repository relationships use UNIQUE(repository_id, developer_id) and ON CONFLICT to update contribution_count and last_contributed_at; updated_at is set to CURRENT_TIMESTAMP
- Synchronization obtains an advisory transaction lock on a repository-scoped key when writing repository_developers to avoid concurrent sync races
- Contributor fetching occurs outside the main PR persistence transaction to avoid long-held DB locks and keep the system responsive

Triggering rules
- Synchronization runs when:
  - A PR is opened and the repository has no repository_developers rows (first-observed repository)
  - (Future) Could be scheduled or triggered manually — current implementation avoids repeated automatic syncs
- Synchronization is skipped if repository_developers rows already exist, preventing unnecessary GitHub API calls

Data persisted
- developers: github_user_id, username, avatar_url, profile fields; linked to organization via organization_id
- repository_developers: repository_id, developer_id, contribution_count, last_contributed_at, created_at, updated_at

Structured logs
- contributor sync started
- GitHub contributors fetched (includes contributor count)
- developers persisted (per-developer logs emitted via upsertDeveloper)
- repository relationships persisted (per relationship)
- replay-safe updates (logs indicate ON CONFLICT upserts)
- sync completed
- failures (error text and classification)

Verification guide

SQL checks

1) Verify new table exists and indexes

SELECT table_name FROM information_schema.tables WHERE table_name = 'repository_developers';

2) Inspect contributors for a repository

SELECT rd.repository_id, rd.developer_id, rd.contribution_count, rd.last_contributed_at, d.username, d.github_user_id
FROM repository_developers rd
JOIN developers d ON d.id = rd.developer_id
WHERE rd.repository_id = <REPO_ID>
ORDER BY rd.contribution_count DESC
LIMIT 100;

3) Confirm developers grew beyond PR authors

SELECT COUNT(*) FROM developers;
-- Compare against distinct authors in pull_requests
SELECT COUNT(DISTINCT author_id) FROM pull_requests;

4) Replay safety: run sync twice and ensure no duplicates

-- before: count
SELECT COUNT(*) FROM repository_developers WHERE repository_id = <REPO_ID>;
-- run contributor sync by opening a PR or invoking the sync code
-- after: count should be unchanged

5) Contribution counts persisted

SELECT d.username, rd.contribution_count
FROM repository_developers rd
JOIN developers d ON d.id = rd.developer_id
WHERE rd.repository_id = <REPO_ID>
ORDER BY rd.contribution_count DESC
LIMIT 20;

Replay-event test scenario (manual)

1. Ensure repository has no repository_developers rows (DELETE FROM repository_developers WHERE repository_id = <REPO_ID>).
2. Craft a PR opened event (existing test harness) for repository <owner>/<repo> and send it twice (simulate webhook replay).
3. Observe logs: first delivery triggers contributor sync, second should detect repository_developers exists and skip fetching.
4. Run verification SQL to ensure developers inserted and repository_developers rows match GitHub contributors.

Expected logs (samples)
- "Starting PR persistence workflow" {deliveryId, githubPrNumber, owner, repo}
- "PR persistence checkpoint" {checkpoint: "transaction_started"}
- "PR persistence checkpoint" {checkpoint: "files_persisted", needContributorSync: true}
- "Starting contributor synchronization" {owner, repo, repositoryId}
- "GitHub contributors fetched" {contributorCount: N}
- "Repository relationship persisted" {repositoryId, developerId, contributions: M}
- "Contributor synchronization completed" {repositoryId}

How this prepares future intelligence
- repository_developers is the canonical organizational graph linking developers to repositories with contribution weights and recency
- Expertise and assignment engines can read this graph (without changes here) to build reviewer pools, expertise tallies, and routing

Operational notes
- The contributors endpoint returns a contribution count but not per-file timestamps; last_contributed_at is set to CURRENT_TIMESTAMP on sync (can be refined later)
- Rate limits are respected and retried in the contributors client; sync is intentionally conservative and only runs when needed

Files changed/added
- backend/spring-api/src/main/resources/db/migration/V11__create_repository_developers.sql (new)
- integrations/github-webhook-service/src/github/client/github-contributors-client.ts (new)
- integrations/github-webhook-service/src/github/types/contributor.ts (new)
- integrations/github-webhook-service/src/services/pr-persistence-service.ts (modified)

If further expansion is desired
- Add a scheduled background job to refresh contributor graphs periodically
- Populate last_contributed_at via commit history queries (expensive) if recency precision is required

