package org.individualproject.flashcards.application.deck.implementation;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.persistence.DeckRepository;
import org.individualproject.flashcards.domain.card.Card;
import org.individualproject.flashcards.domain.card.CardSide;
import org.individualproject.flashcards.application.deck.AddCardToDeckUseCase;
import org.individualproject.flashcards.application.card.DTO.AddCardCommand;
import org.individualproject.flashcards.application.card.DTO.CardPublicData;
import org.individualproject.flashcards.application.exception.DeckNotFoundException;
import org.individualproject.flashcards.domain.card.ContentType;
import org.individualproject.flashcards.domain.deck.Deck;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service @AllArgsConstructor
public class AddCardToDeckUseCaseImpl implements AddCardToDeckUseCase {
    private final DeckRepository deckRepository;
    public CardPublicData addCardToDeck(AddCardCommand command) {
        if (command.deckId() == null) {
            throw new IllegalArgumentException("Deck ID is required");
        }
        var deck = deckRepository.findById(command.deckId()).orElseThrow(DeckNotFoundException::new);
        CardSide front = new CardSide(command.frontContent(),  command.frontContentType());
        CardSide back = new CardSide(command.backContent(),  command.backContentType());
        Card newCard = new Card(front, back);
        deck.addCard(newCard);
        Deck savedDeck = deckRepository.saveAndFlush(deck);
        Card savedCard = savedDeck.getCards().stream()
                .filter(c -> c.getCreatedAt() != null)
                .max(Comparator.comparing(Card::getCreatedAt))
                .orElseThrow();
        return new CardPublicData(
                savedCard.getId(),
                front.content(),
                front.contentType(),
                back.content(),
                back.contentType(),
                newCard.getCreatedAt(),
                newCard.getUpdatedAt());
    }
}
