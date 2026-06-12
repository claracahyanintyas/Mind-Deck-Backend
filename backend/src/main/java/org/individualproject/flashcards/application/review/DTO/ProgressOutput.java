package org.individualproject.flashcards.application.review.DTO;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ProgressOutput (UUID reviewId, double progressPercentage, int totalCards, long cardsArchivedCount,
                              boolean isFinished, ReviewCardPublicData nextCard) {
}
