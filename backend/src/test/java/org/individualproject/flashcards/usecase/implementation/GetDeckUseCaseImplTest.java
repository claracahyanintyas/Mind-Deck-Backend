package org.individualproject.flashcards.usecase.implementation;

import org.individualproject.flashcards.infrastructure.config.database.entity.DeckEntity;
import org.individualproject.flashcards.infrastructure.config.database.repository.DeckRepository;
import org.individualproject.flashcards.usecase.exception.DeckNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetDeckUseCaseImplTest {
    @Mock
    private DeckRepository deckRepository;

    @InjectMocks
    private GetDeckUseCaseImpl getDeckUseCaseImpl;

    @Test
    void getDeck_validId_returnDeck() {
        var id = 1L;
        var deckEntity = DeckEntity.builder()
                .id(id)
                .name("name")
                .description("desc")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .isPrivate(true)
                .build();

        when(deckRepository.findById(1L)).thenReturn(Optional.of(deckEntity));

        var result = getDeckUseCaseImpl.getDeck(1L);
        assertEquals(deckEntity.getName(), result.name());
        assertEquals(deckEntity.getDescription(), result.description());
        verifyNoMoreInteractions(deckRepository);
    }

    @Test
    void getDeck_deckNotExist_throwsException() {
        var id = 1L;
        when(deckRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DeckNotFoundException.class, () -> getDeckUseCaseImpl.getDeck(id) );
        verifyNoMoreInteractions(deckRepository);
    }

}