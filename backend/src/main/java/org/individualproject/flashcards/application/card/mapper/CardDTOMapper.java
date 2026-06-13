package org.individualproject.flashcards.application.card.mapper;

import org.individualproject.flashcards.application.card.DTO.CardPublicData;
import org.individualproject.flashcards.domain.card.Card;

public class CardDTOMapper {
    public static CardPublicData toDTO(Card card) {
        return new CardPublicData(
                card.getId(),
                card.getFrontSide().content(),
                card.getFrontSide().contentType(),
                card.getBackSide().content(),
                card.getBackSide().contentType(),
                card.getCreatedAt(),
                card.getUpdatedAt()
        );
    }
}
