package org.individualproject.flashcards.application.review;

import org.individualproject.flashcards.application.review.DTO.ProgressOutput;
import org.individualproject.flashcards.domain.review.ReviewChoice;

import java.util.UUID;

public interface ProcessReviewChoiceUseCase {
    ProgressOutput processReviewChoice(UUID reviewId, UUID cardId, ReviewChoice reviewChoice);
}
