export interface WebhookLogRecord {
  eventType: string;
  deliveryId: string;
  payloadJson: string;
  processed: boolean;
}

export class WebhookLogRepository {
  async insertIfAbsent(record: WebhookLogRecord): Promise<boolean> {
    const rows = await Bun.sql<{ inserted: boolean }[]>`
      INSERT INTO webhook_logs (event_type, delivery_id, payload_json, processed)
      VALUES (${record.eventType}, ${record.deliveryId}, ${record.payloadJson}::jsonb, ${record.processed})
      ON CONFLICT (delivery_id) DO NOTHING
      RETURNING TRUE AS inserted
    `;

    return rows.length > 0;
  }

  async markProcessed(deliveryId: string): Promise<void> {
    await Bun.sql`
      UPDATE webhook_logs
      SET processed = TRUE
      WHERE delivery_id = ${deliveryId}
    `;
  }
}
