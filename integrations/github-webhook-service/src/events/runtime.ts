import { InMemoryEventDispatcher } from "./dispatcher";
import { PullRequestPersistenceService } from "../services/pr-persistence-service";
import { PullRequestOpenedHandler } from "./pull-request-opened-handler";
import { PullRequestAnalyzedHandler } from "./pull-request-analyzed-handler";

export const dispatcher = new InMemoryEventDispatcher();

const persistenceService = new PullRequestPersistenceService(dispatcher);
const pullRequestOpenedHandler = new PullRequestOpenedHandler(persistenceService);
const pullRequestAnalyzedHandler = new PullRequestAnalyzedHandler();

dispatcher.subscribe("PULL_REQUEST_OPENED", async (event) => {
  await pullRequestOpenedHandler.handle(event);
});

dispatcher.subscribe("PULL_REQUEST_ANALYZED", async (event) => {
  await pullRequestAnalyzedHandler.handle(event);
});
