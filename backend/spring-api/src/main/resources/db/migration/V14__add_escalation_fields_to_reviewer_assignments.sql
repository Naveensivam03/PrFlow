-- Alter reviewer_assignments table to support active workflow SLA tracking
ALTER TABLE reviewer_assignments
    ADD COLUMN assignment_status VARCHAR(50) DEFAULT 'ASSIGNED' NOT NULL,
    ADD COLUMN escalated_at TIMESTAMP,
    ADD COLUMN reminder_sent_at TIMESTAMP,
    ADD COLUMN reassigned_at TIMESTAMP,
    ADD COLUMN escalation_level INT DEFAULT 0 NOT NULL;

-- Performance indexes for fast SLA scanning and status transition logic
CREATE INDEX idx_reviewer_assignments_status ON reviewer_assignments(assignment_status);
CREATE INDEX idx_reviewer_assignments_created_at ON reviewer_assignments(created_at);
CREATE INDEX idx_reviewer_assignments_escalated_at ON reviewer_assignments(escalated_at);
