package org.individualproject.flashcards.infrastructure.config.database.mapper;

import org.individualproject.flashcards.domain.card.Card;
import org.individualproject.flashcards.domain.review.ReviewCard;
import org.individualproject.flashcards.infrastructure.config.database.entity.ReviewCardEntity;
import org.individualproject.flashcards.infrastructure.config.database.entity.ReviewEntity;

public class ReviewCardEntityMapper {
    public static ReviewCard toDomain(ReviewCardEntity entity, Card card) {
        if (entity == null) {
            return null;
        }
        return new ReviewCard(
                entity.getId(),
                card,
                entity.getBox(),
                entity.getState(),
                entity.getNextReviewSequence()
        );
    }

    public static ReviewCardEntity toEntity(ReviewCard domain, ReviewEntity reviewEntity) {
        if (domain == null) {
            return null;
        }
        ReviewCardEntity entity = new ReviewCardEntity();
        entity.setId(domain.getId());
        entity.setReview(reviewEntity);

        entity.setCard(CardEntityMapper.toEntity(domain.getCard(), reviewEntity.getDeck()));
        entity.setState(domain.getState());
        entity.setBox(domain.getBox());
        entity.setNextReviewSequence(domain.getNextReviewSequence());
        return entity;
    }
}
