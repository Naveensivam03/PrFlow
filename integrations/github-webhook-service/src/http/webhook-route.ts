import type { Request, Response } from "express";
import { ZodError } from "zod";
import { env } from "../config/env";
import { normalizePullRequestEvent, normalizePullRequestReviewEvent } from "../events/normalizer";
import { dispatcher } from "../events/runtime";
import { parsePayloadByEvent } from "../github/payload";
import { verifyGitHubSignature } from "../github/signature";
import { logger } from "../logging/logger";
import { WebhookLogRepository } from "../persistence/webhook-log-repository";

const webhookLogRepository = new WebhookLogRepository();

export async function githubWebhookHandler(req: Request, res: Response): Promise<void> {
  const deliveryId = req.header("x-github-delivery") ?? "missing-delivery-id";
  const eventName = req.header("x-github-event") ?? "unknown";
  const signature = req.header("x-hub-signature-256") ?? undefined;
  const rawBody = req.body as Buffer;

  logger.info("GitHub webhook received", { deliveryId, eventName });

  if (!verifyGitHubSignature(rawBody, env.GITHUB_WEBHOOK_SECRET, signature)) {
    logger.warn("Invalid GitHub webhook signature", { deliveryId, eventName });
    res.status(401).json({ error: "Invalid signature" });
    return;
  }

  let parsedBody: unknown;
  try {
    parsedBody = JSON.parse(rawBody.toString("utf8"));
  } catch {
    logger.warn("Webhook payload is not valid JSON", { deliveryId, eventName });
    res.status(400).json({ error: "Malformed JSON payload" });
    return;
  }

  try {
    const parsed = parsePayloadByEvent(eventName, parsedBody);

    if (parsed.kind === "unsupported") {
      const inserted = await webhookLogRepository.insertIfAbsent({
        eventType: eventName,
        deliveryId,
        payloadJson: JSON.stringify(parsedBody),
        processed: false
      });
      if (!inserted) {
        logger.info("Duplicate delivery ignored", { deliveryId, eventName });
      }

      logger.info("Ignored unsupported event", { deliveryId, eventName });
      res.status(202).json({ message: "Event ignored" });
      return;
    }

    if (parsed.kind === "installation") {
      const inserted = await webhookLogRepository.insertIfAbsent({
        eventType: "INSTALLATION",
        deliveryId,
        payloadJson: JSON.stringify(parsed.payload),
        processed: true
      });
      if (!inserted) {
        logger.info("Duplicate delivery ignored", { deliveryId, eventName });
      }
      res.status(202).json({ message: "Installation event recorded" });
      return;
    }

    const normalized =
      parsed.kind === "pull_request"
        ? normalizePullRequestEvent({ deliveryId, eventName, payload: parsed.payload })
        : normalizePullRequestReviewEvent({ deliveryId, eventName, payload: parsed.payload });

    const inserted = await webhookLogRepository.insertIfAbsent({
      eventType:
        normalized?.eventType ??
        (parsed.kind === "pull_request" ? "PULL_REQUEST_UNHANDLED_ACTION" : "PULL_REQUEST_REVIEW_UNHANDLED_ACTION"),
      deliveryId,
      payloadJson: JSON.stringify(parsed.payload),
      processed: false
    });
    if (!inserted) {
      logger.info("Duplicate delivery ignored", {
        deliveryId,
        eventName,
        normalizedEventType: normalized?.eventType ?? "UNHANDLED"
      });
      res.status(202).json({ message: "Duplicate delivery ignored" });
      return;
    }

    if (!normalized) {
      logger.info("Ignored unsupported action for supported event", { deliveryId, eventName });
      res.status(202).json({ message: "Action ignored" });
      return;
    }

    await dispatcher.publish(normalized);
    await webhookLogRepository.markProcessed(deliveryId);

    logger.info("Webhook processed", {
      deliveryId,
      eventName,
      normalizedEventType: normalized.eventType
    });

    res.status(202).json({ message: "Accepted", eventType: normalized.eventType });
  } catch (error) {
    if (error instanceof ZodError) {
      logger.warn("Webhook payload validation failed", {
        deliveryId,
        eventName,
        issues: error.issues.map((issue) => ({
          path: issue.path.join("."),
          message: issue.message
        }))
      });
      res.status(400).json({ error: "Invalid payload schema" });
      return;
    }

    logger.error("Webhook processing failed", {
      deliveryId,
      eventName,
      error: error instanceof Error ? error.message : "unknown"
    });

    res.status(500).json({ error: "Internal webhook processing error" });
  }
}
