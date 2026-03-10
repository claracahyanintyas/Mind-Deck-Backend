package org.individualproject.flashcards.usecase.implementation;

import org.individualproject.flashcards.infrastructure.config.database.entity.DeckEntity;
import org.individualproject.flashcards.infrastructure.config.database.repository.DeckRepository;
import org.individualproject.flashcards.infrastructure.deck.DTO.CreateDeckRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateDeckUseCaseImplTest {

    @Mock
    private DeckRepository deckRepository;

    @InjectMocks
    private CreateDeckUseCaseImpl createDeckUseCaseImpl;


    @Test
    void createDeck_withTitle_shouldReturnCorrectDeck() {
        var name = "name";
        var isPrivate = true;
        var request = new CreateDeckRequest(name, null,isPrivate);
        var saveDeck = new DeckEntity(1L, name, "", OffsetDateTime.now(), OffsetDateTime.now(), isPrivate);

        when(deckRepository.save(any())).thenReturn(saveDeck);

        var result = createDeckUseCaseImpl.createDeck(request);
        verify(deckRepository).save(any());
        assertEquals(saveDeck.getName(), result.name());
        assertNotNull(saveDeck.getId());
    }
    @Test
    void createDeck_EmptyTitle_shouldThrowException() {
        var name = "";
        var isPrivate = true;
        var request = new CreateDeckRequest(name, null,isPrivate);

        assertThrows(IllegalArgumentException.class, () -> createDeckUseCaseImpl.createDeck(request));
        verifyNoInteractions(deckRepository);
    }
    @Test
    void createDeck_NullRequest_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> createDeckUseCaseImpl.createDeck(null));
        verifyNoInteractions(deckRepository);
    }
}