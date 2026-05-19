import { logger } from "../logging/logger";
import type { PrflowEvent } from "./types";

export type PrflowEventHandler = (event: PrflowEvent) => Promise<void> | void;

export class InMemoryEventDispatcher {
  private readonly handlers = new Map<string, PrflowEventHandler[]>();

  subscribe(eventType: PrflowEvent["eventType"], handler: PrflowEventHandler): void {
    const existing = this.handlers.get(eventType) ?? [];
    existing.push(handler);
    this.handlers.set(eventType, existing);
  }

  async publish(event: PrflowEvent): Promise<void> {
    const handlers = this.handlers.get(event.eventType) ?? [];

    logger.info("Publishing internal event", {
      eventType: event.eventType,
      deliveryId: event.deliveryId,
      handlerCount: handlers.length
    });

    await Promise.all(
      handlers.map(async (handler) => {
        try {
          await handler(event);
        } catch (error) {
          logger.error("Event handler failed", {
            eventType: event.eventType,
            deliveryId: event.deliveryId,
            error: error instanceof Error ? error.message : "unknown"
          });
        }
      })
    );
  }
}
