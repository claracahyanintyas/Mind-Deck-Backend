package org.individualproject.flashcards.application.review.mapper;

import org.individualproject.flashcards.application.deck.mapper.DeckDTOMapper;
import org.individualproject.flashcards.application.review.DTO.ReviewPublicData;
import org.individualproject.flashcards.application.user.mapper.UserDTOMapper;
import org.individualproject.flashcards.domain.review.Review;

import java.util.stream.Collectors;

public class ReviewDTOMapper {
    public static ReviewPublicData  toReviewPublicData(Review review) {
        return new ReviewPublicData(review.getId(), UserDTOMapper.toDTO(review.getUser()),
                DeckDTOMapper.toDeckPublicData(review.getDeck()), review.getStartedAt(),
                review.getReviewCards().stream().map(ReviewCardDTOMapper::toDTO).collect(Collectors.toList()),
                review.getTotalCards());
    }
}
