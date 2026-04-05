-- Ник (name) должен быть уникальным
ALTER TABLE users ADD CONSTRAINT uq_users_name UNIQUE (name);
