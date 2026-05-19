import express from "express";
import { env } from "./config/env";
import { githubWebhookHandler } from "./http/webhook-route";
import { logger } from "./logging/logger";

const app = express();

app.get("/health", (_req, res) => {
  res.status(200).json({ status: "ok", service: "github-webhook-service" });
});

app.post("/webhook/github", express.raw({ type: "application/json", limit: "2mb" }), (req, res) => {
  void githubWebhookHandler(req, res);
});

app.use((error: unknown, _req: express.Request, res: express.Response, _next: express.NextFunction) => {
  logger.error("Unhandled express error", {
    error: error instanceof Error ? error.message : "unknown"
  });
  res.status(500).json({ error: "Internal server error" });
});

app.listen(env.PORT, () => {
  logger.info("GitHub webhook service started", {
    port: env.PORT,
    nodeEnv: env.NODE_ENV
  });
});
