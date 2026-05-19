import type { PrflowEvent } from "./types";
import { logger } from "../logging/logger";

export class PullRequestAnalyzedHandler {
  handle(event: PrflowEvent): void {
    if (event.eventType !== "PULL_REQUEST_ANALYZED") {
      return;
    }

    logger.info("PULL_REQUEST_ANALYZED published", {
      deliveryId: event.deliveryId,
      githubPrNumber: event.githubPrNumber,
      repositoryId: event.repositoryId
    });
  }
}
