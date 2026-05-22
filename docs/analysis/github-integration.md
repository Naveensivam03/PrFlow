# GitHub Integration Analysis - PRFlow

## 1. Ingestion Layer
PRFlow integrates with GitHub via a **GitHub App**. The `github-webhook-service` manages the lifecycle of incoming events.

### Capabilities:
*   **Webhook Signature Validation**: Ensures events originate from GitHub.
*   **Installation-based Auth**: Uses JWT and Installation Access Tokens to interact with the GitHub API.
*   **Scope Extraction**: Parses file paths to identify logical scopes (e.g., `src/main/java/` -> `JAVA` scope).

## 2. Organizational Discovery Analysis

### How PRFlow "Learns" the Organization:
Currently, PRFlow's discovery of organizational entities is **reactive** rather than **proactive**.

*   **Organizations**: Discovered when a webhook is received for an installation.
*   **Repositories**: Discovered when a PR is opened in that repository.
*   **Developers**: Discovered when they are the **author** of a PR.

### The "Author-Only" Limitation:
The current implementation of `upsertDeveloper` in `PullRequestPersistenceService.ts` only processes the `pr.author`. 

**Critical Gap**:
*   **No Contributor Sync**: PRFlow does not currently fetch the full list of contributors for a repository upon installation or sync.
*   **No Organization Member Sync**: It doesn't know about developers who haven't opened a PR since PRFlow was installed.
*   **Result**: The "Reviewer Pool" is initially empty and only grows as developers author PRs. This significantly limits the effectiveness of the **Expertise Engine** for suggesting reviewers who are "experts" but haven't authored a PR *yet* in the PRFlow era.

## 3. Synchronization Workflows

| Entity | Discovery Trigger | Sync Completeness |
| :--- | :--- | :--- |
| Organizations | Installation Event | High (Installation ID + Org ID) |
| Repositories | PR Opened | Low (Only active repos seen) |
| Developers | PR Authored | **Very Low** (Authors only) |
| Contributor Rel | Implicit (via PR) | **None** (Explicit relationship missing) |

## 4. Architectural Gaps for Advanced Engines
To support the **Assignment Engine**, the GitHub integration layer must evolve to include:
1.  **Proactive Repository Sync**: Listing all repositories in an organization upon installation.
2.  **Contributor Backfill**: Fetching historical contributors (via GitHub API) to pre-populate `developer_file_expertise` or at least `developers`.
3.  **Membership Synchronization**: Periodically syncing organization members to maintain an accurate pool of potential reviewers.
