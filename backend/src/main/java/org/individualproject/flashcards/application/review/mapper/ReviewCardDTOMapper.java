package org.individualproject.flashcards.application.review.mapper;

import org.individualproject.flashcards.application.card.mapper.CardDTOMapper;
import org.individualproject.flashcards.application.review.DTO.ReviewCardPublicData;
import org.individualproject.flashcards.domain.review.ReviewCard;

public class ReviewCardDTOMapper {
    public static ReviewCardPublicData  toDTO(ReviewCard reviewCard) {
        return new ReviewCardPublicData(reviewCard.getId(),
                CardDTOMapper.toDTO(reviewCard.getCard()),
                reviewCard.getBox(),
                reviewCard.getState());
    }
}
