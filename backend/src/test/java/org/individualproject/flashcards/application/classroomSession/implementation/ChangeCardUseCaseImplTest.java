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
class ChangeCardUseCaseImplTest {

    @Mock
    private ClassroomSessionRepository inMemoryRepository;

    @InjectMocks
    private ChangeCardUseCaseImpl changeCardUseCaseImpl;

    @Test
    void execute_WithValidRoomCode_ShouldChangeCardAndReturnOutput() {
        // Arrange
        String roomCode = "ROOM123";
        Long newCardId = 1L;

        ClassroomSession activeSession = new ClassroomSession(roomCode, 1L);

        when(inMemoryRepository.findByCode(roomCode)).thenReturn(activeSession);

        // Act
        ClassroomSessionOutput result = changeCardUseCaseImpl.execute(roomCode, newCardId);

        // Assert
        assertNotNull(result);
        assertEquals(roomCode, result.roomCode()); // Using record-style or standard getter format
        assertEquals(newCardId, result.currentCardId());

        // Verify changes persist back down the storage port boundary
        verify(inMemoryRepository, times(1)).save(activeSession);
    }

    @Test
    void execute_RoomNotFound_ShouldThrowIllegalArgumentException() {
        // Arrange
        String invalidCode = "BADCODE";
        when(inMemoryRepository.findByCode(invalidCode)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                changeCardUseCaseImpl.execute(invalidCode, 99L)
        );
        verify(inMemoryRepository, never()).save(any());
    }
}