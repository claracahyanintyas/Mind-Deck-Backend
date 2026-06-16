package org.individualproject.flashcards.application.deck.mapper;

import org.individualproject.flashcards.application.card.mapper.CardDTOMapper;
import org.individualproject.flashcards.application.deck.DTO.DeckPublicData;
import org.individualproject.flashcards.application.user.mapper.UserDTOMapper;
import org.individualproject.flashcards.domain.deck.Deck;

import java.util.stream.Collectors;

public class DeckDTOMapper {
    public static DeckPublicData  toDeckPublicData(Deck deck) {
        return new DeckPublicData(deck.getId(), deck.getName(), deck.getDescription(), deck.getCreatedAt(),
                deck.getUpdatedAt(), deck.getIsPrivate(), UserDTOMapper.toDTO(deck.getCreatedBy()),
                deck.getCards().stream().map(CardDTOMapper::toDTO).collect(Collectors.toList()));
    }
}
