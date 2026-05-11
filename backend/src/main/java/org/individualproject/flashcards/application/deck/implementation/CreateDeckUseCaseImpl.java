package org.individualproject.flashcards.application.deck.implementation;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.card.DTO.CardPublicData;
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
        var cards = savedDeck.getCards()
                .stream()
                .map(card -> new CardPublicData(
                        card.getId(),
                        card.getFrontSide().content(),
                        card.getFrontSide().contentType(),
                        card.getBackSide().content(),
                        card.getBackSide().contentType(),
                        card.getCreatedAt(),
                        card.getUpdatedAt()
                ))
                .toList();


        return new DeckPublicData(savedDeck.getId(), savedDeck.getName(), savedDeck.getDescription(), savedDeck.getCreatedAt(),
                savedDeck.getUpdatedAt(), savedDeck.getIsPrivate(), cards);
    }
}
