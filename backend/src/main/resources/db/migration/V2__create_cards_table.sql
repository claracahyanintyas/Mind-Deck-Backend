CREATE TABLE cards (
                       id BIGSERIAL PRIMARY KEY,
                       deck_id BIGINT NOT NULL,
                       front_content TEXT,
                       front_content_type VARCHAR(50),
                       back_content TEXT,
                       back_content_type VARCHAR(50),
                       created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT fk_cards_deck
                           FOREIGN KEY (deck_id)
                               REFERENCES decks (id)
                               ON DELETE CASCADE
);

CREATE INDEX idx_cards_deck_id ON cards(deck_id);