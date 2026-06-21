package org.individualproject.flashcards.application.classroomSession.implementation;

import org.individualproject.flashcards.application.classroomSession.DTO.ClassroomSessionOutput;
import org.individualproject.flashcards.application.persistence.ClassroomSessionRepository;
import org.individualproject.flashcards.domain.classroomSession.ClassroomSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlipCardUseCaseImplTest {

    @Mock
    private ClassroomSessionRepository inMemoryRepository;

    @InjectMocks
    private FlipCardUseCaseImpl flipCardUseCaseImpl;

    @Test
    void execute_WithValidRoomCode_ShouldFlipCardAndReturnOutput() {
        // Arrange
        String roomCode = "FLIP123";
        ClassroomSession activeSession = new ClassroomSession(roomCode, 10L);

        // Set a current card ID so the guard condition passes
        activeSession.changeCard(42L);

        when(inMemoryRepository.findByCode(roomCode)).thenReturn(activeSession);

        // Act
        ClassroomSessionOutput result = flipCardUseCaseImpl.execute(roomCode);

        // Assert
        assertNotNull(result);
        assertEquals(roomCode, result.roomCode());
        assertTrue(result.isCardFlipped(), "The card should be flipped because currentCardId is present");
        assertTrue(activeSession.isCardFlipped());

        // Verify state persistence
        verify(inMemoryRepository, times(1)).save(activeSession);
    }
    @Test
    void execute_RoomNotFound_ShouldThrowIllegalArgumentException() {
        // Arrange
        String invalidCode = "MISSING";
        when(inMemoryRepository.findByCode(invalidCode)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                flipCardUseCaseImpl.execute(invalidCode)
        );
        verify(inMemoryRepository, never()).save(any());
    }
}