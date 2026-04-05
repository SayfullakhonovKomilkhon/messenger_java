-- Add public_id column (8-char alphanumeric identifier for user search/sharing)
ALTER TABLE users ADD COLUMN IF NOT EXISTS public_id VARCHAR(8);

-- Generate public_id for existing users that don't have one
UPDATE users SET public_id = UPPER(SUBSTRING(REPLACE(gen_random_uuid()::text, '-', ''), 1, 8))
WHERE public_id IS NULL;

-- Ensure uniqueness by appending random chars if duplicates exist
DO $$
DECLARE
    dup_record RECORD;
    new_id TEXT;
    chars TEXT := 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789';
BEGIN
    FOR dup_record IN
        SELECT id FROM users u1
        WHERE (SELECT COUNT(*) FROM users u2 WHERE u2.public_id = u1.public_id) > 1
    LOOP
        new_id := '';
        FOR i IN 1..8 LOOP
            new_id := new_id || SUBSTR(chars, FLOOR(RANDOM() * LENGTH(chars) + 1)::int, 1);
        END LOOP;
        UPDATE users SET public_id = new_id WHERE id = dup_record.id;
    END LOOP;
END $$;

ALTER TABLE users ALTER COLUMN public_id SET NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_public_id ON users(public_id);

-- Add ai_name column (name for AI agent / telepathy)
ALTER TABLE users ADD COLUMN IF NOT EXISTS ai_name VARCHAR(100);
