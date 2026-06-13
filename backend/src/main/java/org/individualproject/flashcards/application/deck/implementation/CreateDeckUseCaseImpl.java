package org.individualproject.flashcards.application.deck.implementation;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.deck.mapper.DeckDTOMapper;
import org.individualproject.flashcards.application.exception.UserNotFoundException;
import org.individualproject.flashcards.application.persistence.DeckRepository;
import org.individualproject.flashcards.application.persistence.UserRepository;
import org.individualproject.flashcards.domain.deck.Deck;
import org.individualproject.flashcards.application.deck.DTO.CreateDeckCommand;
import org.individualproject.flashcards.application.deck.DTO.DeckPublicData;
import org.individualproject.flashcards.application.deck.CreateDeckUseCase;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CreateDeckUseCaseImpl implements CreateDeckUseCase {
    private final DeckRepository deckRepository;
    private final UserRepository userRepository;
    public DeckPublicData createDeck(CreateDeckCommand command, String username) {
        if (command == null){
            throw new  IllegalArgumentException("Request cannot be null");
        }
        if (username == null || username.isEmpty()){
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        var user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("Username not found"));
        Deck deck = new Deck(command.name(), command.description(), command.isPrivate(), user);

        var savedDeck = deckRepository.save(deck);

        return DeckDTOMapper.toDeckPublicData(savedDeck);
    }
}
