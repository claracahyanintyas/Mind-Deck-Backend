ALTER TABLE users
    ALTER COLUMN email DROP NOT NULL,
    ADD CONSTRAINT uq_users_username UNIQUE (username),
    ALTER COLUMN username SET NOT NULL;