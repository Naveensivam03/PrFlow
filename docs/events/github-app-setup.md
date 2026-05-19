# GitHub App Setup (PRFlow)

## 1. Create the GitHub App

1. Go to GitHub: `Settings -> Developer settings -> GitHub Apps -> New GitHub App`.
2. Set **GitHub App name**: `prflow` (or your environment-specific variant).
3. Set **Homepage URL**: your PRFlow backend URL.
4. Set **Webhook URL**: `https://<your-domain>/webhook/github`.
5. Generate and copy a **Webhook secret** (store as `GITHUB_WEBHOOK_SECRET`).
6. Disable unnecessary features for now (keep foundation scope only).

## 2. Configure Permissions

Set repository permissions exactly as below:

- Pull requests: **Read-only**
- Metadata: **Read-only**
- Members: **Read-only**
- Contents: **Read-only**

No write permissions are needed for this foundation phase.

## 3. Configure Webhook Events

Subscribe only to these webhook events:

- `pull_request`
- `pull_request_review`
- `installation`

## 4. Install the App

1. Open the app page and click **Install App**.
2. Install on the target organization.
3. Select repositories (all or selected based on rollout).

## 5. Configure Environment

Set in `integrations/github-webhook-service/.env`:

- `PORT=3001`
- `NODE_ENV=development`
- `GITHUB_WEBHOOK_SECRET=<your_webhook_secret>`
- `DATABASE_URL=postgresql://...`

## 6. Verify Delivery

1. Start the webhook service.
2. In GitHub App settings, open **Advanced -> Recent Deliveries**.
3. Redeliver a sample event and confirm `202 Accepted`.
4. Confirm `webhook_logs` row was created with matching `delivery_id`.
