package org.individualproject.flashcards.application.deck.implementation;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.card.DTO.CardPublicData;
import org.individualproject.flashcards.application.deck.mapper.DeckDTOMapper;
import org.individualproject.flashcards.application.persistence.DeckRepository;
import org.individualproject.flashcards.application.deck.DTO.DeckPublicData;
import org.individualproject.flashcards.application.deck.DTO.UpdateDeckCommand;
import org.individualproject.flashcards.application.deck.UpdateDeckUseCase;
import org.individualproject.flashcards.application.exception.DeckNotFoundException;
import org.springframework.stereotype.Service;

@Service @AllArgsConstructor
public class UpdateDeckUseCaseImpl implements UpdateDeckUseCase {
    private DeckRepository deckRepository;
    public DeckPublicData updateDeck(Long id, UpdateDeckCommand command, String requestingUsername) {
        if (command == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (command.name().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("deckId cannot be null or below 1");
        }

        var deck = deckRepository.findById(id).orElseThrow(DeckNotFoundException::new);

        deck.updateDetails(command.name(),  command.description(), command.isPrivate(), requestingUsername);

        var savedDeck = deckRepository.save(deck);

        return DeckDTOMapper.toDeckPublicData(savedDeck);
    }
}
