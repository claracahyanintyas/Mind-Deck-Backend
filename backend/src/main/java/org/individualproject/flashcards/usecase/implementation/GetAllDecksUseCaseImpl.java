package org.individualproject.flashcards.usecase.implementation;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.domain.Deck;
import org.individualproject.flashcards.infrastructure.config.database.entity.DeckEntity;
import org.individualproject.flashcards.infrastructure.config.database.repository.DeckRepository;
import org.individualproject.flashcards.infrastructure.deck.DTO.DeckPublicData;
import org.individualproject.flashcards.usecase.GetAllDecksUseCase;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service @AllArgsConstructor
public class GetAllDecksUseCaseImpl implements GetAllDecksUseCase {
    private DeckRepository deckRepository;
    public Collection<DeckPublicData> getAllDecks(){
        var decks = deckRepository.findAll().stream().map(DeckEntity::fromEntity).toList();
        List<DeckPublicData> deckPublicDataList = new ArrayList<>();
        for (Deck deck : decks){
            deckPublicDataList.add(new DeckPublicData(deck.getId(),deck.getName(), deck.getDescription(),
                    deck.getCreatedAt(), deck.getUpdatedAt(), deck.getIsPrivate()));
        }
        return deckPublicDataList;
    }


}
