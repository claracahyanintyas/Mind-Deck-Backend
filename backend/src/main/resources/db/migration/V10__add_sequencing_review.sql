ALTER TABLE review_cards
ADD COLUMN next_review_sequence INT NOT NULL DEFAULT 0;

ALTER TABLE reviews
ADD COLUMN current_sequence INT NOT NULL DEFAULT 0;