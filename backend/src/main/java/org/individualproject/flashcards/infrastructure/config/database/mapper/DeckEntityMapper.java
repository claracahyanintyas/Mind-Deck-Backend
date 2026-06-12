package org.individualproject.flashcards.infrastructure.config.database.mapper;

import org.individualproject.flashcards.domain.deck.Deck;
import org.individualproject.flashcards.infrastructure.config.database.entity.CardEntity;
import org.individualproject.flashcards.infrastructure.config.database.entity.DeckEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeckEntityMapper {

    public static DeckEntity toEntity(Deck deck){
        DeckEntity deckEntity = DeckEntity.builder()
                .id(deck.getId())
                .name(deck.getName())
                .description(deck.getDescription())
                .createdAt(deck.getCreatedAt())
                .updatedAt(deck.getUpdatedAt())
                .isPrivate(deck.getIsPrivate())
                .createdBy(UserEntityMapper.toEntity(deck.getCreatedBy()))
                .cards(new ArrayList<>())
                .build();

        if (deck.getCards() != null && !deck.getCards().isEmpty()) {
            List<CardEntity> cardEntities = deck.getCards().stream()
                    .map(card -> CardEntityMapper.toEntity(card, deckEntity))
                    .collect(Collectors.toList());

            deckEntity.setCards(cardEntities);
        }

        return deckEntity;
    }
    public static Deck fromEntity(DeckEntity entity){
        return new Deck(entity.getId(), entity.getName(), entity.getDescription(), entity.getCreatedAt(),entity.getUpdatedAt(),
                entity.isPrivate(), UserEntityMapper.fromEntity(entity.getCreatedBy()),
                entity.getCards().stream().map(CardEntityMapper::fromEntity).collect(Collectors.toList()));
    }
}
