package org.individualproject.flashcards.infrastructure.config.database.mapper;

import org.individualproject.flashcards.domain.card.Card;
import org.individualproject.flashcards.domain.deck.Deck;
import org.individualproject.flashcards.domain.review.Review;
import org.individualproject.flashcards.domain.review.ReviewCard;
import org.individualproject.flashcards.domain.user.User;
import org.individualproject.flashcards.infrastructure.config.database.entity.CardEntity;
import org.individualproject.flashcards.infrastructure.config.database.entity.ReviewCardEntity;
import org.individualproject.flashcards.infrastructure.config.database.entity.ReviewEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ReviewEntityMapper {

    public static Review toDomain(ReviewEntity entity) {
        if (entity == null) {
            return null;
        }

        List<ReviewCard> domainCards = entity.getReviewCards().stream()
                .map(cardEntity -> {
                    Card domainCard = CardEntityMapper.fromEntity(cardEntity.getCard());
                    return ReviewCardEntityMapper.toDomain(cardEntity, domainCard);
                })
                .collect(Collectors.toList());

        return new Review(
                entity.getId(),
                UserEntityMapper.fromEntity(entity.getUser()),
                DeckEntityMapper.fromEntity(entity.getDeck()),
                entity.getStartedAt(),
                domainCards,
                entity.getCurrentSequence()
        );
    }

    public static ReviewEntity toEntity(Review domain) {
        if (domain == null) {
            return null;
        }

        ReviewEntity reviewEntity = new ReviewEntity();
        reviewEntity.setId(domain.getId());
        reviewEntity.setUser(UserEntityMapper.toEntity(domain.getUser()));
        reviewEntity.setDeck(DeckEntityMapper.toEntity(domain.getDeck()));
        reviewEntity.setStartedAt(domain.getStartedAt());

        Set<ReviewCardEntity> reviewCardEntities = domain.getReviewCards().stream()
                .map(domainCard -> ReviewCardEntityMapper.toEntity(domainCard, reviewEntity))
                .collect(Collectors.toSet());

        reviewEntity.setReviewCards(reviewCardEntities);
        reviewEntity.setCurrentSequence(domain.getCurrentSequence());
        return reviewEntity;
    }
}