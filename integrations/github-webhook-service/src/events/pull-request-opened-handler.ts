import type { PrflowEvent } from "./types";
import { logger } from "../logging/logger";
import { PullRequestPersistenceService } from "../services/pr-persistence-service";

export class PullRequestOpenedHandler {
  constructor(private readonly persistenceService: PullRequestPersistenceService) {}

  async handle(event: PrflowEvent): Promise<void> {
    if (event.eventType !== "PULL_REQUEST_OPENED") {
      return;
    }

    logger.info("Handling PULL_REQUEST_OPENED", {
      deliveryId: event.deliveryId,
      githubPrNumber: event.githubPrNumber,
      repositoryId: event.repositoryId
    });

    await this.persistenceService.handlePullRequestOpened(event);
  }
}
