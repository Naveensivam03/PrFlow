# GitHub Webhook Service

PRFlow GitHub ingestion and PR persistence service using Bun + TypeScript + Express.

## Responsibilities

- Receive GitHub webhooks at `POST /webhook/github`
- Verify `x-hub-signature-256` using HMAC SHA-256
- Validate payload schema safely with Zod
- Normalize supported payloads into internal PRFlow events
- Persist raw webhook payloads into `webhook_logs`
- For `PULL_REQUEST_OPENED`, persist PR workflow state + changed files
- Publish internal `PULL_REQUEST_ANALYZED` event

## Folder Structure

```text
integrations/github-webhook-service/
├── src/
│   ├── config/
│   │   └── env.ts
│   ├── events/
│   │   ├── dispatcher.ts
│   │   ├── normalizer.ts
│   │   ├── runtime.ts
│   │   └── types.ts
│   ├── github/
│   │   ├── app-auth.ts
│   │   ├── github-api-client.ts
│   │   ├── payload.ts
│   │   └── signature.ts
│   ├── http/
│   │   └── webhook-route.ts
│   ├── logging/
│   │   └── logger.ts
│   ├── scope/
│   │   └── scope-extractor.ts
│   ├── services/
│   │   └── pr-persistence-service.ts
│   ├── persistence/
│   │   └── webhook-log-repository.ts
│   └── index.ts
├── index.ts
├── package.json
└── tsconfig.json
```

## Endpoints

- `GET /health` -> liveness probe
- `POST /webhook/github` -> GitHub webhook receiver

## Event Flow

- `pull_request.opened` -> normalize -> persist PR + files -> publish `PULL_REQUEST_ANALYZED`

## Setup

1. Install deps:

```bash
bun install
```

2. Create `.env` from `.env.example` and set values:

```bash
PORT=3001
NODE_ENV=development
GITHUB_WEBHOOK_SECRET=replace_me
GITHUB_APP_ID=123456
GITHUB_APP_PRIVATE_KEY="-----BEGIN RSA PRIVATE KEY-----\n...\n-----END RSA PRIVATE KEY-----"
DATABASE_URL=postgresql://prflow_app:change_me_in_local_env@localhost:5432/prflow_db
```

3. Run:

```bash
bun run dev
```

## Required DB Migrations

Apply Spring Flyway migrations through `V6` before webhook persistence tests.

## Testing Locally

1. Check health:

```bash
curl http://localhost:3001/health
```

2. Trigger a real PR opened webhook from GitHub App delivery.

3. Verify persistence:

```bash
psql -U prflow_app -d prflow_db -c "SELECT id, github_pr_number, status FROM pull_requests ORDER BY id DESC LIMIT 10;"
psql -U prflow_app -d prflow_db -c "SELECT pull_request_id, file_path, scope_identifier FROM pull_request_files ORDER BY id DESC LIMIT 20;"
```
