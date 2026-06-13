package org.individualproject.flashcards.application.implementation;

import org.individualproject.flashcards.application.deck.DTO.CreateDeckCommand;
import org.individualproject.flashcards.application.persistence.DeckRepository;
import org.individualproject.flashcards.application.persistence.UserRepository;
import org.individualproject.flashcards.domain.deck.Deck;
import org.individualproject.flashcards.domain.user.User;
import org.individualproject.flashcards.application.deck.implementation.CreateDeckUseCaseImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateDeckUseCaseImplTest {

    @Mock
    private DeckRepository deckRepository;

    @InjectMocks
    private CreateDeckUseCaseImpl createDeckUseCaseImpl;

    @Mock
    private UserRepository userRepository;

    @Test
    void createDeck_withTitle_shouldReturnCorrectDeck() {
        var name = "name";
        var isPrivate = true;
        var request = new CreateDeckCommand(name, null,isPrivate);
        var user = new User("guest");
        var saveDeck = new Deck(1L, name, "", OffsetDateTime.now(), OffsetDateTime.now(), isPrivate, user, new ArrayList<>());

        when(deckRepository.save(any())).thenReturn(saveDeck);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        var result = createDeckUseCaseImpl.createDeck(request, user.getUsername());
        verify(deckRepository).save(any());
        assertEquals(saveDeck.getName(), result.name());
        assertNotNull(saveDeck.getId());
    }
    @Test
    void createDeck_EmptyTitle_shouldThrowException() {
        var name = "";
        var isPrivate = true;
        var request = new CreateDeckCommand(name, null,isPrivate);
        var user = new User("guest");
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> createDeckUseCaseImpl.createDeck(request, "guest"));
        verifyNoInteractions(deckRepository);
    }
    @Test
    void createDeck_NullRequest_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> createDeckUseCaseImpl.createDeck(null, "guest"));
        verifyNoInteractions(deckRepository);
    }
}