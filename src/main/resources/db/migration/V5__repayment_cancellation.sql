ALTER TABLE repayment ADD COLUMN cancelled_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE repayment ADD COLUMN cancelled_by_id BIGINT REFERENCES app_user(id);
ALTER TABLE repayment ADD COLUMN cancellation_reason VARCHAR(250);
ALTER TABLE repayment ADD COLUMN request_id VARCHAR(36) UNIQUE;
ALTER TABLE repayment ADD CONSTRAINT ck_repayment_cancellation CHECK (
(cancelled_at IS NULL AND cancelled_by_id IS NULL AND cancellation_reason IS NULL) OR
(cancelled_at IS NOT NULL AND cancelled_by_id IS NOT NULL AND cancellation_reason IS NOT NULL));
