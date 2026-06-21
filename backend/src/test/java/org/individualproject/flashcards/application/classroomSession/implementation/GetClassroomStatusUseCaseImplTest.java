package org.individualproject.flashcards.application.classroomSession.implementation;

import org.individualproject.flashcards.application.classroomSession.DTO.ClassroomSessionOutput;
import org.individualproject.flashcards.application.exception.RoomNotFoundException;
import org.individualproject.flashcards.application.persistence.ClassroomSessionRepository;
import org.individualproject.flashcards.domain.classroomSession.ClassroomSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetClassroomStatusUseCaseImplTest {

    @Mock
    private ClassroomSessionRepository repository;

    @InjectMocks
    private GetClassroomStatusUseCaseImpl getClassroomStatusUseCaseImpl;

    @Test
    void execute_WithValidRoomCode_ShouldReturnCorrectClassroomSessionOutput() {
        // Arrange
        String roomCode = "STATUS123";
        Long deckId = 10L;

        // Instantiate a real domain instance to ensure state maps faithfully
        ClassroomSession activeSession = new ClassroomSession(roomCode, deckId);
        activeSession.changeCard(42L); // Give it a card ID state to test mapping coverage

        when(repository.findByCode(roomCode)).thenReturn(activeSession);

        // Act
        ClassroomSessionOutput result = getClassroomStatusUseCaseImpl.execute(roomCode);

        // Assert
        assertNotNull(result);
        assertEquals(roomCode, result.roomCode());
        assertEquals(deckId, result.deckId());
        assertEquals(42L, result.currentCardId());
        assertFalse(result.isCardFlipped());

        // Since it's a read-only view operation, verify we never accidentally save or mutate down the port
        verify(repository, never()).save(any());
    }

    @Test
    void execute_RoomNotFound_ShouldThrowRoomNotFoundException() {
        // Arrange
        String invalidCode = "EMPTY_ROOM";
        when(repository.findByCode(invalidCode)).thenReturn(null);

        // Act & Assert
        assertThrows(RoomNotFoundException.class, () ->
                getClassroomStatusUseCaseImpl.execute(invalidCode)
        );
        verify(repository, never()).save(any());
    }
}