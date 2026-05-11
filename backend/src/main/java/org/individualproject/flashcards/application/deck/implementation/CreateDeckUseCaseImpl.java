package org.individualproject.flashcards.application.deck.implementation;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.persistence.DeckRepository;
import org.individualproject.flashcards.domain.deck.Deck;
import org.individualproject.flashcards.application.deck.DTO.CreateDeckCommand;
import org.individualproject.flashcards.application.deck.DTO.DeckPublicData;
import org.individualproject.flashcards.application.deck.CreateDeckUseCase;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CreateDeckUseCaseImpl implements CreateDeckUseCase {
    private DeckRepository deckRepository;
    public DeckPublicData createDeck(CreateDeckCommand command) {
        if (command == null){
            throw new  IllegalArgumentException("Request cannot be null");
        }
        Deck deck = new Deck(command.name(), command.description(), command.isPrivate());

        var savedDeck = deckRepository.save(deck);

        return new DeckPublicData(savedDeck.getId(), savedDeck.getName(), savedDeck.getDescription(), savedDeck.getCreatedAt(),
                savedDeck.getUpdatedAt(), savedDeck.getIsPrivate());
    }
}
