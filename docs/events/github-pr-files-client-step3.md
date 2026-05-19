# Step 3: GitHub PR Files API Client

## Scope
This step implements only GitHub integration concerns:
- GitHub App JWT generation
- Installation access token generation + short-lived caching
- Authenticated PR files API requests
- Typed response parsing
- Structured integration logging

Not implemented in this step:
- orchestration services
- persistence changes
- reviewer assignment/expertise engines

## Implementation structure

- `src/github/auth/github-app-auth.ts`
  - `buildGitHubAppJwt()`
  - `getInstallationAccessToken(installationId)`
  - `clearInstallationTokenCache()`
- `src/github/client/github-pr-files-client.ts`
  - `fetchPullRequestFiles(...)`
  - `PrFilesFetchError` (typed failure classification)
- `src/github/types/github-auth.ts`
  - typed installation-token response schema
- `src/github/types/pr-files.ts`
  - typed PR files response schema
- compatibility exports:
  - `src/github/app-auth.ts`
  - `src/github/github-api-client.ts`

## GitHub App auth lifecycle

1. Build short-lived App JWT signed with app private key (`RS256`).
2. Use JWT to call `POST /app/installations/{id}/access_tokens`.
3. Receive installation token (`token`, `expires_at`).
4. Use installation token for repository-scoped API calls like PR files.
5. Refresh token before expiration (cache uses safety window).

### JWT vs installation token

- JWT:
  - identifies the GitHub App itself
  - very short-lived
  - used only to request installation tokens
- Installation token:
  - scoped to one installation/repository permissions
  - temporary (expires)
  - used for actual repository API access

### Why temporary scoped access matters

- limits blast radius if token leaks
- enforces least privilege per installation
- supports revocation/rotation without long-lived shared secrets

## PR files API workflow

Endpoint used:
- `GET /repos/{owner}/{repo}/pulls/{pull_number}/files?per_page=100`

Captured typed response fields:
- `filename`
- `status`
- `additions`
- `deletions`
- `changes`

### Why webhook payload alone is insufficient

`pull_request` webhook payload does not include full changed-file list with per-file churn metrics. PRFlow needs file-level details for later scope and expertise computations.

### Why changed-file intelligence is foundational

Changed-file data becomes base input for:
- scope extraction
- domain ownership inference
- complexity/churn signal generation
- future assignment decisions

## Error handling and retry-safe behavior

`PrFilesFetchError` codes:
- `UNAUTHORIZED` (invalid/expired installation token)
- `FORBIDDEN` (installation lacks repo access)
- `NOT_FOUND` (repo/PR missing)
- `RATE_LIMITED` (403/429 with retry metadata)
- `NETWORK` (transport failures)
- `GITHUB_API` (other API failures)
- `PARSE_ERROR` (invalid response schema/json)

Retry policy:
- max 3 attempts
- retry only retriable classes (`RATE_LIMITED`, `NETWORK`, `GITHUB_API`)
- exponential backoff; honors `Retry-After` when provided

This is retry-safe because requests are read-only (`GET`) and idempotent.

## Integration boundaries

Boundary separation keeps logic maintainable:
- auth concerns in `github/auth`
- transport/retry/error classification in `github/client`
- schema contracts in `github/types`

Typed schemas protect upstream changes from silently corrupting downstream orchestration assumptions.

## How this fits PRFlow architecture

Event ingestion normalizes PR events; this integration enriches those events with changed-file intelligence from GitHub. Future engines depend on this enrichment layer but are intentionally not implemented in this step.

## Verification steps

### 1) Typecheck
```bash
cd integrations/github-webhook-service
bun run typecheck
```

### 2) Runtime smoke with webhook service
```bash
cd integrations/github-webhook-service
bun run dev
```

Expect no startup errors related to:
- `GITHUB_APP_ID`
- `GITHUB_APP_PRIVATE_KEY`
- auth/client imports

### 3) Functional verification via real webhook flow
Trigger a PR `opened` event from an installed repository and check logs for:
- installation token fetch (or cache hit)
- successful PR files fetch with `fileCount`

## Sample API calls (reference)

### Installation token exchange
```bash
curl -i -X POST \
  -H "Authorization: Bearer <app-jwt>" \
  -H "Accept: application/vnd.github+json" \
  -H "User-Agent: prflow-github-webhook-service" \
  https://api.github.com/app/installations/<installation_id>/access_tokens
```

### PR files fetch
```bash
curl -i \
  -H "Authorization: Bearer <installation-token>" \
  -H "Accept: application/vnd.github+json" \
  -H "User-Agent: prflow-github-webhook-service" \
  https://api.github.com/repos/<owner>/<repo>/pulls/<pr_number>/files?per_page=100
```

## Expected logs

Success:
- `Fetched new installation token`
- `Using cached installation token` (subsequent calls)
- `Fetched PR files from GitHub` with `fileCount`

Failures:
- `GitHub installation token request failed` with status/response body
- `Failed to fetch PR files` with `errorCode`, `status`, `retriable`, `attempt`

## Debugging guide

1. `UNAUTHORIZED`:
- confirm `GITHUB_APP_ID`
- verify `GITHUB_APP_PRIVATE_KEY` formatting (`\n` escaped in env)
- ensure system clock is sane for JWT `iat/exp`

2. `FORBIDDEN`:
- app installation lacks repo access
- missing `pull_requests` read permission in app settings

3. `NOT_FOUND`:
- wrong owner/repo/pr number
- installation not granted to target repo

4. `RATE_LIMITED`:
- inspect `Retry-After`
- reduce call volume, keep cache effective

5. `PARSE_ERROR`:
- inspect raw GitHub response shape
- adjust schema only if API contract truly changed
