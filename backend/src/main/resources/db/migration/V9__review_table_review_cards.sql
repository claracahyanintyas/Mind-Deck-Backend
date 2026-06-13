CREATE TABLE reviews (
    id UUID PRIMARY KEY,
    user_id BIGSERIAL NOT NULL,
    deck_id BIGSERIAL NOT NULL,
    started_at TIMESTAMP WITH TIME ZONE,

    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_reviews_deck FOREIGN KEY (deck_id) REFERENCES decks(id) ON DELETE CASCADE
);

CREATE TABLE review_cards (
    id UUID PRIMARY KEY,
    review_id UUID NOT NULL,
    card_id BIGSERIAL NOT NULL,
    state VARCHAR(50) NOT NULL,
    box INT NOT NULL DEFAULT 1,

    CONSTRAINT fk_review_cards_review FOREIGN KEY (review_id) REFERENCES reviews(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_cards_card FOREIGN KEY (card_id) REFERENCES cards(id) ON DELETE CASCADE
);

CREATE INDEX idx_reviews_user_id ON reviews(user_id);
CREATE INDEX idx_reviews_deck_id ON reviews(deck_id);
CREATE INDEX idx_review_cards_review_id ON review_cards(review_id);
CREATE INDEX idx_review_cards_card_id ON review_cards(card_id);