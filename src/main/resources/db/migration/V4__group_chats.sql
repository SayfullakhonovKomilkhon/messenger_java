-- Group chats support

ALTER TABLE conversations ADD COLUMN IF NOT EXISTS type VARCHAR(10) NOT NULL DEFAULT 'DIRECT';
ALTER TABLE conversations ADD COLUMN IF NOT EXISTS title VARCHAR(100);
ALTER TABLE conversations ADD COLUMN IF NOT EXISTS description VARCHAR(500);
ALTER TABLE conversations ADD COLUMN IF NOT EXISTS avatar_url TEXT;
ALTER TABLE conversations ADD COLUMN IF NOT EXISTS created_by UUID;
ALTER TABLE conversations ADD COLUMN IF NOT EXISTS invite_link VARCHAR(40) UNIQUE;
ALTER TABLE conversations ADD COLUMN IF NOT EXISTS max_members INTEGER NOT NULL DEFAULT 1000;

ALTER TABLE conversation_participants ADD COLUMN IF NOT EXISTS role VARCHAR(10) NOT NULL DEFAULT 'MEMBER';
ALTER TABLE conversation_participants ADD COLUMN IF NOT EXISTS joined_at TIMESTAMP DEFAULT NOW();

-- Set existing direct conversations
UPDATE conversations SET type = 'DIRECT' WHERE type IS NULL OR type = '';
UPDATE conversation_participants SET role = 'MEMBER' WHERE role IS NULL OR role = '';
