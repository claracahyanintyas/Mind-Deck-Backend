package org.individualproject.flashcards.domain.review;

import lombok.Getter;
import org.individualproject.flashcards.domain.card.Card;

import java.util.UUID;

@Getter
public class ReviewCard {
    private final UUID id;
    private final Card card;
    private int box;
    private CardState state;

    private int nextReviewSequence;

    public ReviewCard( Card card, int box, CardState state, int nextReviewSequence ) {
        if (card == null) {
            throw new IllegalArgumentException("ReviewCard must reference a valid Card model.");
        }
        this.id = UUID.randomUUID();
        this.card = card;
        this.box = box;
        this.state = state;
        this.nextReviewSequence = nextReviewSequence;
    }

    public ReviewCard(UUID id, Card card, int box, CardState state, int nextReviewSequence ) {
        this.id = id;
        this.card = card;
        this.box = box;
        this.state = state;
        this.nextReviewSequence = nextReviewSequence;
    }

    public void applyReviewChoice(ReviewChoice choice, int currentSessionSequence) {
        switch (choice) {
            case REMEMBER:
                this.state = CardState.ARCHIVED;
                break;

            case FORGET:
                this.box = 1;
                this.nextReviewSequence = currentSessionSequence + 3;
                break;

            case UNSURE:
                this.box = Math.max(1, this.box - 1);
                this.nextReviewSequence = currentSessionSequence + 5;
                break;
        }
    }

    public void reset() {
        this.box = 1;
        this.state = CardState.ACTIVE;
        this.nextReviewSequence = 0;
    }

    public boolean isArchived() {
        return this.state == CardState.ARCHIVED;
    }
}
