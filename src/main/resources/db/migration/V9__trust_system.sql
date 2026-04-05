ALTER TABLE conversation_participants
    ADD COLUMN trust_status VARCHAR(10) DEFAULT 'PENDING',
    ADD COLUMN search_method VARCHAR(10);

ALTER TABLE users
    ADD COLUMN wallet_address VARCHAR(130);
