package org.individualproject.flashcards.application.card.implementation;

import org.individualproject.flashcards.application.card.DTO.AddCardCommand;
import org.individualproject.flashcards.application.card.DTO.CardPublicData;
import org.individualproject.flashcards.application.deck.implementation.AddCardToDeckUseCaseImpl;
import org.individualproject.flashcards.application.exception.DeckNotFoundException;
import org.individualproject.flashcards.application.persistence.DeckRepository;
import org.individualproject.flashcards.domain.card.Card;
import org.individualproject.flashcards.domain.card.CardSide;
import org.individualproject.flashcards.domain.card.ContentType;
import org.individualproject.flashcards.domain.deck.Deck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddCardToDeckUseCaseImplTest {

    @Mock
    private DeckRepository deckRepository;
    @InjectMocks
    private AddCardToDeckUseCaseImpl useCase;

    @Test
    void shouldAddCardToDeckAndReturnPublicData() {
        // Arrange
        Long deckId = 1L;

        Deck deck = new Deck(
                deckId,
                "name",
                "desc",
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                false,
                new ArrayList<>()
        );

        AddCardCommand command = new AddCardCommand(
                deckId,
                "front",
                ContentType.PLAIN_TEXT,
                "back",
                ContentType.PLAIN_TEXT
        );

        Card card = new Card(new CardSide("front", ContentType.PLAIN_TEXT), new CardSide("back", ContentType.PLAIN_TEXT));
        when(deckRepository.findById(deckId)).thenReturn(java.util.Optional.of(deck));
        when(deckRepository.save(any())).thenAnswer(invocation -> {
            Deck inputDeck = invocation.getArgument(0);

            Card inputCard = inputDeck.getCards().get(0);

            Card savedCard = new Card(
                    1L,
                    inputCard.getFrontSide(),
                    inputCard.getBackSide(),
                    inputCard.getCreatedAt(),
                    inputCard.getUpdatedAt()
            );

            return new Deck(
                    inputDeck.getId(),
                    inputDeck.getName(),
                    inputDeck.getDescription(),
                    inputDeck.getCreatedAt(),
                    inputDeck.getUpdatedAt(),
                    inputDeck.getIsPrivate(),
                    List.of(savedCard)
            );
        });

        // Act
        CardPublicData result = useCase.addCardToDeck(command);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.frontContent()).isEqualTo("front");
        assertThat(result.backContent()).isEqualTo("back");

        verify(deckRepository).findById(deckId);
        verify(deckRepository).save(any());
    }
    @Test
    void shouldThrowException_whenDeckIdIsNull() {
        AddCardCommand command = new AddCardCommand(
                null,
                "front",
                ContentType.PLAIN_TEXT,
                "back",
                ContentType.PLAIN_TEXT
        );

        assertThatThrownBy(() -> useCase.addCardToDeck(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Deck ID is required");

        verifyNoInteractions(deckRepository);
    }
    @Test
    void shouldThrowDeckNotFoundException_whenDeckDoesNotExist() {
        Long deckId = 1L;

        AddCardCommand command = new AddCardCommand(
                deckId,
                "front",
                ContentType.PLAIN_TEXT,
                "back",
                ContentType.PLAIN_TEXT
        );

        when(deckRepository.findById(deckId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.addCardToDeck(command))
                .isInstanceOf(DeckNotFoundException.class);

        verify(deckRepository).findById(deckId);
        verify(deckRepository, never()).save(any());
    }
    
}