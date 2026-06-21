package org.individualproject.flashcards.application.classroomSession.implementation;

import org.individualproject.flashcards.application.classroomSession.DTO.ClassroomSessionOutput;
import org.individualproject.flashcards.application.persistence.ClassroomSessionRepository;
import org.individualproject.flashcards.domain.classroomSession.ClassroomSession;
import org.individualproject.flashcards.domain.review.ReviewChoice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmitVoteUseCaseImplTest {

    @Mock
    private ClassroomSessionRepository inMemoryRepository;

    @InjectMocks
    private SubmitVoteUseCaseImpl submitVoteUseCaseImpl;

    @Test
    void execute_WithValidRoomCode_ShouldSubmitVoteAndReturnUpdatedOutput() {
        // Arrange
        String roomCode = "VOTE123";
        String username = "student_1";
        ReviewChoice choice = ReviewChoice.UNSURE;

        // Instantiate a real session aggregate and set up dependencies for voting
        ClassroomSession activeSession = new ClassroomSession(roomCode, 10L);
        activeSession.changeCard(42L); // Active card required for a valid vote context
        activeSession.addStudent(username);

        int initialVoteCount = activeSession.getTotalVotesCount(); // Typically 0

        when(inMemoryRepository.findByCode(roomCode)).thenReturn(activeSession);

        // Act
        ClassroomSessionOutput result = submitVoteUseCaseImpl.execute(roomCode, username, choice);

        // Assert
        assertNotNull(result);
        assertEquals(roomCode, result.roomCode());
        assertEquals(initialVoteCount + 1, result.totalConnectedStudents(),
                "The total votes count should have incremented by 1");
        assertNotNull(result.currentVoteBreakdown());

        // Verify state is flushed down the infrastructure repository port
        verify(inMemoryRepository, times(1)).save(activeSession);
    }

    @Test
    void execute_RoomNotFound_ShouldThrowIllegalArgumentException() {
        // Arrange
        String invalidCode = "EMPTY_ROOM";
        when(inMemoryRepository.findByCode(invalidCode)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                submitVoteUseCaseImpl.execute(invalidCode, "student_1", ReviewChoice.UNSURE)
        );
        verify(inMemoryRepository, never()).save(any());
    }
}