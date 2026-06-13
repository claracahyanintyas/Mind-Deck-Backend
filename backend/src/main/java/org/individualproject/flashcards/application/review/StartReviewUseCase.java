package org.individualproject.flashcards.application.review;

import org.individualproject.flashcards.application.review.DTO.ReviewPublicData;

public interface StartReviewUseCase {
    ReviewPublicData startReview(String username, Long deckId);
}
