package org.individualproject.flashcards.application.implementation;

import org.individualproject.flashcards.application.deck.DTO.UpdateDeckCommand;
import org.individualproject.flashcards.application.persistence.DeckRepository;
import org.individualproject.flashcards.domain.deck.Deck;
import org.individualproject.flashcards.infrastructure.config.database.entity.DeckEntity;
import org.individualproject.flashcards.infrastructure.config.database.JpaRepository.DeckJpaRepository;
import org.individualproject.flashcards.infrastructure.deck.DTO.UpdateDeckRequest;
import org.individualproject.flashcards.application.deck.implementation.UpdateDeckUseCaseImpl;
import org.individualproject.flashcards.application.exception.DeckNotFoundException;
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
class UpdateDeckUseCaseImplTest {

    @Mock
    private DeckRepository deckRepository;

    @InjectMocks
    private UpdateDeckUseCaseImpl updateDeckUseCase;

    @Test
    void updateDeck_properInput_returnsUpdatedDeck() {
        var originalEntity = new Deck(1L, "name", "", OffsetDateTime.now(), OffsetDateTime.now(), false, new ArrayList<>());
        var updatedEntity =  new Deck(1L, "new name", "desc", OffsetDateTime.now(), OffsetDateTime.now(), true, new ArrayList<>());

        when(deckRepository.findById(1L)).thenReturn(Optional.of(originalEntity));
        when(deckRepository.save(any())).thenReturn(updatedEntity);

        var updateRequest = new UpdateDeckCommand("new name", "desc", true);


        var response = updateDeckUseCase.updateDeck(originalEntity.getId(), updateRequest);

        assertEquals(updateRequest.name(), response.name());
        assertEquals(updateRequest.description(), response.description());
        assertEquals(updateRequest.isPrivate(), response.isPrivate());
    }
    @Test
    void updateDeck_deckNotFound_throwsDeckNotFoundException() {
        when(deckRepository.findById(1L)).thenReturn(Optional.empty());

        var updateRequest = new UpdateDeckCommand("name", "desc", true);

        assertThrows(DeckNotFoundException.class, () -> updateDeckUseCase.updateDeck(1L, updateRequest));
        verifyNoMoreInteractions(deckRepository);
    }
    @Test
    void updateDeck_nameIsEmpty_throwsIllegalArgumentException() {
        var updateRequest = new UpdateDeckCommand("", "desc", true);

        assertThrows(IllegalArgumentException.class, () -> updateDeckUseCase.updateDeck(1L, updateRequest));
        verifyNoInteractions(deckRepository);
    }
    @Test
    void updateDeck_invalidId_throwsException() {
        var updateRequest = new UpdateDeckCommand("name", "desc", true);
        assertThrows(IllegalArgumentException.class, () -> updateDeckUseCase.updateDeck(0L, updateRequest));
    }
}