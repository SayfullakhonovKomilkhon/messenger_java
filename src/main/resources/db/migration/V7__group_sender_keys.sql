CREATE TABLE group_sender_keys (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    sender_id UUID NOT NULL REFERENCES users(id),
    recipient_id UUID NOT NULL REFERENCES users(id),
    distribution_message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    consumed BOOLEAN DEFAULT false,
    UNIQUE(group_id, sender_id, recipient_id)
);

CREATE INDEX idx_gsk_recipient ON group_sender_keys(recipient_id, consumed);
CREATE INDEX idx_gsk_group ON group_sender_keys(group_id);
