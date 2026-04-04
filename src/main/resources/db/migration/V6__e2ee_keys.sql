-- E2EE key distribution tables

CREATE TABLE e2ee_identity_keys (
    user_id        UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    registration_id INT NOT NULL,
    identity_public_key BYTEA NOT NULL,
    created_at     TIMESTAMP DEFAULT NOW()
);

CREATE TABLE e2ee_signed_pre_keys (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    key_id     INT NOT NULL,
    public_key BYTEA NOT NULL,
    signature  BYTEA NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(user_id, key_id)
);

CREATE TABLE e2ee_pre_keys (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    key_id     INT NOT NULL,
    public_key BYTEA NOT NULL,
    used       BOOLEAN DEFAULT FALSE,
    UNIQUE(user_id, key_id)
);

CREATE INDEX idx_e2ee_pre_keys_user_unused ON e2ee_pre_keys(user_id) WHERE used = FALSE;

ALTER TABLE messages ADD COLUMN encrypted BOOLEAN DEFAULT FALSE;
ALTER TABLE messages ADD COLUMN encrypted_file_key TEXT;
ALTER TABLE messages ADD COLUMN file_iv TEXT;
