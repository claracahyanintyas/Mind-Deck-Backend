package org.individualproject.flashcards.usecase.implementation;

import org.individualproject.flashcards.domain.Deck;
import org.individualproject.flashcards.infrastructure.config.database.entity.DeckEntity;
import org.individualproject.flashcards.infrastructure.config.database.repository.DeckRepository;
import org.individualproject.flashcards.infrastructure.deck.DTO.UpdateDeckRequest;
import org.individualproject.flashcards.usecase.exception.DeckNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateDeckUseCaseImplTest {

    @Mock
    private DeckRepository deckRepository;

    @InjectMocks
    private UpdateDeckUseCaseImpl updateDeckUseCase;

    @Test
    void updateDeck_properInput_returnsUpdatedDeck() {
        var originalEntity = DeckEntity.builder()
                .id(1L)
                .name("name")
                .description("description")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .isPrivate(false)
                .build();
        var updatedEntity = DeckEntity.builder()
                .id(1L)
                .name("new name")
                .description("desc")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .isPrivate(true)
                .build();

        when(deckRepository.findById(1L)).thenReturn(Optional.of(originalEntity));
        when(deckRepository.save(any())).thenReturn(updatedEntity);

        var updateRequest = new UpdateDeckRequest("new name", "desc", true);


        var response = updateDeckUseCase.updateDeck(originalEntity.getId(), updateRequest);

        assertEquals(updateRequest.name(), response.name());
        assertEquals(updateRequest.description(), response.description());
        assertEquals(updateRequest.isPrivate(), response.isPrivate());
    }
    @Test
    void updateDeck_emptyName_returnsUpdatedDeck() {
        var originalEntity = DeckEntity.builder()
                .id(1L)
                .name("name")
                .description("description")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .isPrivate(false)
                .build();
        when(deckRepository.findById(1L)).thenReturn(Optional.of(originalEntity));

        var updateRequest = new UpdateDeckRequest("", "desc", true);

        assertThrows(IllegalArgumentException.class, () -> updateDeckUseCase.updateDeck(1L, updateRequest));
    }
    @Test
    void updateDeck_deckNotFound_throwsDeckNotFoundException() {
        when(deckRepository.findById(1L)).thenReturn(Optional.empty());

        var updateRequest = new UpdateDeckRequest("name", "desc", true);

        assertThrows(DeckNotFoundException.class, () -> updateDeckUseCase.updateDeck(1L, updateRequest));
    }
    @Test
    void updateDeck_invalidId_throwsException() {
        var updateRequest = new UpdateDeckRequest("name", "desc", true);
        assertThrows(IllegalArgumentException.class, () -> updateDeckUseCase.updateDeck(0L, updateRequest));
    }
}