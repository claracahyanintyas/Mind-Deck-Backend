package org.individualproject.flashcards.application.deck.implementation;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.deck.mapper.DeckDTOMapper;
import org.individualproject.flashcards.application.persistence.DeckRepository;
import org.individualproject.flashcards.domain.deck.Deck;
import org.individualproject.flashcards.application.deck.DTO.DeckPublicData;
import org.individualproject.flashcards.application.deck.GetAllDecksUseCase;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service @AllArgsConstructor
public class GetAllDecksUseCaseImpl implements GetAllDecksUseCase {
    private DeckRepository deckRepository;
    public Collection<DeckPublicData> getAllDecks(){
        var decks = deckRepository.findAllPublicDecks();
        List<DeckPublicData> deckPublicDataList = new ArrayList<>();
        for (Deck deck : decks){
            deckPublicDataList.add(DeckDTOMapper.toDeckPublicData(deck));
        }
        return deckPublicDataList;
    }


}
