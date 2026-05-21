import type { PrflowEvent } from "./types";
import { logger } from "../logging/logger";
import { env } from "../config/env";

export class PullRequestAnalyzedHandler {
  async handle(event: PrflowEvent): Promise<void> {
    if (event.eventType !== "PULL_REQUEST_ANALYZED") {
      return;
    }

    const { dbPullRequestId, dbRepositoryId, deliveryId } = event;

    if (dbPullRequestId === undefined || dbRepositoryId === undefined) {
      logger.error("Skipping event forwarding: missing database primary keys", {
        deliveryId,
        githubPrNumber: event.githubPrNumber,
        dbPullRequestId,
        dbRepositoryId
      });
      throw new Error("Missing database primary keys for event forwarding");
    }

    const targetUrl = `${env.BACKEND_URL}/api/events/pull-request-analyzed`;

    logger.info("Forwarding PULL_REQUEST_ANALYZED event to backend", {
      deliveryId,
      githubPrNumber: event.githubPrNumber,
      dbPullRequestId,
      dbRepositoryId,
      targetUrl
    });

    try {
      const response = await fetch(targetUrl, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          pullRequestId: dbPullRequestId,
          repositoryId: dbRepositoryId,
          deliveryId
        })
      });

      if (!response.ok) {
        const errorText = await response.text().catch(() => "");
        throw new Error(`HTTP ${response.status}: ${errorText}`);
      }

      logger.info("Successfully forwarded PULL_REQUEST_ANALYZED event", {
        deliveryId,
        githubPrNumber: event.githubPrNumber
      });
    } catch (error) {
      logger.error("Failed to forward PULL_REQUEST_ANALYZED to backend", {
        deliveryId,
        githubPrNumber: event.githubPrNumber,
        error: error instanceof Error ? error.message : "unknown"
      });
      throw error;
    }
  }
}
