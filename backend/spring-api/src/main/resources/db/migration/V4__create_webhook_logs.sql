CREATE TABLE webhook_logs (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    delivery_id VARCHAR(255) NOT NULL,
    payload_json JSONB NOT NULL,
    received_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT uq_webhook_logs_delivery_id UNIQUE (delivery_id)
);

CREATE INDEX idx_webhook_logs_event_type ON webhook_logs(event_type);
CREATE INDEX idx_webhook_logs_received_at ON webhook_logs(received_at);
CREATE INDEX idx_webhook_logs_processed ON webhook_logs(processed);
