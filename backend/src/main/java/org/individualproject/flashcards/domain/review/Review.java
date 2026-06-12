package org.individualproject.flashcards.domain.review;

import lombok.Getter;
import org.individualproject.flashcards.domain.deck.Deck;
import org.individualproject.flashcards.domain.user.User;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Getter
public class Review {
    private final UUID id;
    private final User user;
    private final Deck deck;
    private final OffsetDateTime startedAt;
    private final List<ReviewCard> reviewCards;

    private final int totalCards;

    private int currentSequence;

    public Review(User user, Deck deck, List<ReviewCard> reviewCards) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.deck = deck;
        this.reviewCards = reviewCards;
        startedAt = OffsetDateTime.now();
        totalCards = reviewCards.size();
    }

    public Review(UUID id, User user, Deck deck, OffsetDateTime startedAt, List<ReviewCard> reviewCards, int curentSequence) {
        this.id = id;
        this.user = user;
        this.deck = deck;
        this.reviewCards = reviewCards;
        this.startedAt = startedAt;
        this.currentSequence = curentSequence;
        totalCards = reviewCards.size();
    }

    public void processCardReview(UUID reviewCardId, ReviewChoice choice) {
        this.currentSequence++;

        this.reviewCards.stream()
                .filter(rc -> rc.getId().equals(reviewCardId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Card not found in this review session"))
                .applyReviewChoice(choice, this.currentSequence);
    }

    public ReviewCard determineNextCard() {
        return this.reviewCards.stream()
                .filter(rc -> rc.getState() == CardState.ACTIVE)
                // RULE: Card is only eligible if its waiting buffer has expired
                .filter(rc -> this.currentSequence >= rc.getNextReviewSequence())
                // Priority A: Show lowest box number first
                .min(Comparator.comparingInt(ReviewCard::getBox)
                        // Priority B (Tie-Breaker): Show the one that has been waiting the longest
                        .thenComparingInt(ReviewCard::getNextReviewSequence))
                // Fallback: If ALL remaining active cards are locked in a buffer,
                // temporarily break the buffer rule and just grab the earliest available one so the app doesn't stall.
                .orElseGet(() -> this.reviewCards.stream()
                        .filter(rc -> rc.getState() == CardState.ACTIVE)
                        .min(Comparator.comparingInt(ReviewCard::getNextReviewSequence))
                        .orElse(null));
    }

    public long getCardsArchivedCount() {
        return this.reviewCards.stream().filter(ReviewCard::isArchived).count();
    }

    public double getProgressPercentage() {
        if (totalCards == 0) return 0.0;
        return ((double) getCardsArchivedCount() / totalCards) * 100.0;
    }

    public boolean isSessionComplete() {
        return getCardsArchivedCount() >= totalCards;
    }

    public void restart() {
        this.reviewCards.forEach(ReviewCard::reset);
    }
}
