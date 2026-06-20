package org.individualproject.flashcards.application.review.implementation;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.persistence.ReviewRepository;
import org.individualproject.flashcards.application.review.DTO.ProgressOutput;
import org.individualproject.flashcards.application.review.DTO.ReviewCardPublicData;
import org.individualproject.flashcards.application.review.ProcessReviewChoiceUseCase;
import org.individualproject.flashcards.application.review.mapper.ReviewCardDTOMapper;
import org.individualproject.flashcards.domain.review.Review;
import org.individualproject.flashcards.domain.review.ReviewCard;
import org.individualproject.flashcards.domain.review.ReviewChoice;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service @AllArgsConstructor
public class ProcessReviewChoiceUseCaseImpl implements ProcessReviewChoiceUseCase {
    private final ReviewRepository reviewRepository;

    @Transactional
    public ProgressOutput processReviewChoice(UUID reviewId, UUID reviewCardId, ReviewChoice choice) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review session not found"));

        review.processCardReview(reviewCardId, choice);
        reviewRepository.save(review);

        ReviewCard nextCard = review.determineNextCard();

        ReviewCardPublicData nextCardDTO = (nextCard != null)
                ? ReviewCardDTOMapper.toDTO(nextCard)
                : null;

        return ProgressOutput.builder()
                .reviewId(review.getId())
                .progressPercentage(review.getProgressPercentage())
                .totalCards(review.getTotalCards())
                .cardsArchivedCount(review.getCardsArchivedCount())
                .isFinished(review.isSessionComplete() || nextCardDTO == null)
                .nextCard(nextCardDTO)
                .build();
    }
}
