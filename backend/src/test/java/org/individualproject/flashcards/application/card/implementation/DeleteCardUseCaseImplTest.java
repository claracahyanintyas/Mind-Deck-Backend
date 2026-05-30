package org.individualproject.flashcards.application.card.implementation;

import org.individualproject.flashcards.application.exception.CardNotFoundException;
import org.individualproject.flashcards.application.persistence.CardRepository;
import org.individualproject.flashcards.domain.card.Card;
import org.individualproject.flashcards.domain.card.CardSide;
import org.individualproject.flashcards.domain.card.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCardUseCaseImplTest {
    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private DeleteCardUseCaseImpl deleteCardUseCase;

    @Test
    void getDeck_validId_returnDeck() {
        var id = 1L;

        deleteCardUseCase.deleteCard(1L);
        verify(cardRepository, times(1)).deleteById(anyLong());
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void getDeck_invalidID_throwsException() {
        var id = -1L;
        assertThrows(IllegalArgumentException.class, () -> deleteCardUseCase.deleteCard(id) );
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void getDeck_nullId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> deleteCardUseCase.deleteCard(null) );
        verifyNoInteractions(cardRepository);
    }
}