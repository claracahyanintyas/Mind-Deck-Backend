package org.individualproject.flashcards.application.card.implementation;

import org.individualproject.flashcards.application.card.DTO.CardPublicData;
import org.individualproject.flashcards.application.card.DTO.EditCardCommand;
import org.individualproject.flashcards.application.card.mapper.CardDTOMapper;
import org.individualproject.flashcards.application.exception.CardNotFoundException;
import org.individualproject.flashcards.application.persistence.CardRepository;
import org.individualproject.flashcards.domain.card.Card;
import org.individualproject.flashcards.domain.card.CardSide;
import org.individualproject.flashcards.domain.card.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EditCardUseCaseImplTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private EditCardUseCaseImpl editCardUseCase;

    @Test
    void editCard_WithValidId_ShouldUpdateCardAndReturnPublicData() {
        // Arrange
        Long cardId = 100L;
        EditCardCommand command = new EditCardCommand(
                "New Front Text", ContentType.PLAIN_TEXT,
                "New Back Text", ContentType.PLAIN_TEXT
        );

        Card mockCard = mock(Card.class);
        CardPublicData expectedDto = new CardPublicData(
                cardId, "New Front Text", ContentType.PLAIN_TEXT, "New Back Text", ContentType.PLAIN_TEXT, OffsetDateTime.now(), OffsetDateTime.now()
        );

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(mockCard));
        when(cardRepository.save(mockCard)).thenReturn(Optional.of(mockCard));

        // Use try-with-resources to mock the static CardDTOMapper method safely
        try (MockedStatic<CardDTOMapper> mockedMapper = mockStatic(CardDTOMapper.class)) {
            mockedMapper.when(() -> CardDTOMapper.toDTO(mockCard)).thenReturn(expectedDto);

            // Act
            CardPublicData result = editCardUseCase.editCard(command, cardId);

            // Assert
            assertNotNull(result);
            assertEquals("New Front Text", result.frontContent());
            assertEquals("New Back Text", result.backContent());

            // Verify domain interactions and repository storage lifecycles
            verify(mockCard, times(1)).updateCard(any(CardSide.class), any(CardSide.class));
            verify(cardRepository, times(1)).findById(cardId);
            verify(cardRepository, times(1)).save(mockCard);
        }
    }

    @Test
    void editCard_WhenCardDoesNotExistInFind_ShouldThrowCardNotFoundException() {
        // Arrange
        Long targetId = 404L;
        EditCardCommand command = new EditCardCommand("Front", ContentType.PLAIN_TEXT, "Back", ContentType.PLAIN_TEXT);

        when(cardRepository.findById(targetId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CardNotFoundException.class, () ->
                editCardUseCase.editCard(command, targetId)
        );

        verify(cardRepository, times(1)).findById(targetId);
        verify(cardRepository, never()).save(any());
    }

    @Test
    void editCard_WhenSaveFailsOrReturnsEmpty_ShouldThrowCardNotFoundException() {
        // Arrange
        Long cardId = 100L;
        EditCardCommand command = new EditCardCommand("Front", ContentType.PLAIN_TEXT, "Back", ContentType.PLAIN_TEXT);
        Card mockCard = mock(Card.class);

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(mockCard));
        // Simulate a save edge-case failure that returns Optional.empty()
        when(cardRepository.save(mockCard)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CardNotFoundException.class, () ->
                editCardUseCase.editCard(command, cardId)
        );

        verify(cardRepository, times(1)).findById(cardId);
        verify(cardRepository, times(1)).save(mockCard);
    }
}