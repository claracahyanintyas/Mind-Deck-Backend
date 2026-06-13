INSERT INTO users (id, email, username)
VALUES (1, 'system@app.com', 'system_default')
ON CONFLICT (id) DO NOTHING;

ALTER TABLE decks
ADD COLUMN created_by BIGINT NOT NULL DEFAULT 6;

ALTER TABLE decks
ADD CONSTRAINT fk_decks_user
FOREIGN KEY (created_by) REFERENCES users(id)
ON DELETE CASCADE;