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
class GetCardByIdUseCaseImplTest {
    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private GetCardByIdUseCaseImpl getCardUseCaseImpl;

    @Test
    void getDeck_validId_returnDeck() {
        var id = 1L;
        var card = new Card(1L, new CardSide("front", ContentType.PLAIN_TEXT), new CardSide("back", ContentType.PLAIN_TEXT),OffsetDateTime.now(), OffsetDateTime.now());

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        var result = getCardUseCaseImpl.getCardById(1L);
        assertEquals(card.getFrontSide().content(), result.frontContent());
        assertEquals(card.getBackSide().content(), result.backContent());
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void getDeck_deckNotExist_throwsException() {
        var id = 1L;
        when(cardRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> getCardUseCaseImpl.getCardById(id) );
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void getDeck_nullId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> getCardUseCaseImpl.getCardById(null) );
        verifyNoInteractions(cardRepository);
    }
}