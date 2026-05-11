package org.individualproject.flashcards.infrastructure.config.database.mapper;

import org.springframework.stereotype.Component;
import org.individualproject.flashcards.domain.card.Card;
import org.individualproject.flashcards.domain.card.CardSide;
import org.individualproject.flashcards.infrastructure.config.database.entity.CardEntity;
import org.individualproject.flashcards.infrastructure.config.database.entity.CardSideEmbeddable;
import org.individualproject.flashcards.infrastructure.config.database.entity.DeckEntity;

@Component
public class CardEntityMapper {
    public static Card fromEntity(CardEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Card(
                entity.getId(),
                fromEntity(entity.getFrontSide()),
                fromEntity(entity.getBackSide()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static CardEntity toEntity(Card domain, DeckEntity deckEntity) {
        if (domain == null) {
            return null;
        }

        return CardEntity.builder()
                .id(domain.getId())
                .frontSide(toEmbeddable(domain.getFrontSide()))
                .backSide(toEmbeddable(domain.getBackSide()))
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .deck(deckEntity)
                .build();
    }

    private static CardSide fromEntity(CardSideEmbeddable embeddable) {
        if (embeddable == null) {
            return null;
        }
        return new CardSide(
                embeddable.getContent(),
                embeddable.getContentType()
        );
    }


    private static CardSideEmbeddable toEmbeddable(CardSide cardSide) {
        if (cardSide == null) {
            return null;
        }

        return CardSideEmbeddable.builder()
                .content(cardSide.content())
                .contentType(cardSide.contentType())
                .build();
    }
}