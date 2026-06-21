package org.individualproject.flashcards.application.classroomSession.implementation;

import org.individualproject.flashcards.application.classroomSession.DTO.ClassroomSessionOutput;
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
class JoinClassroomSessionUseCaseImplTest {

    @Mock
    private ClassroomSessionRepository sessionRepository;

    @InjectMocks
    private JoinClassroomSessionUseCaseImpl joinClassroomSessionUseCaseImpl;

    @Test
    void execute_WithValidRoomCode_ShouldAddStudentAndReturnUpdatedOutput() {
        // Arrange
        String roomCode = "JOIN123";
        String studentUsername = "student_user";

        // Instantiate a real session aggregate
        ClassroomSession activeSession = new ClassroomSession(roomCode, 10L);

        // Capture initial count before execution to assert accurate incrementation
        int initialStudentCount = activeSession.getConnectedStudentCount();

        when(sessionRepository.findByCode(roomCode)).thenReturn(activeSession);

        // Act
        ClassroomSessionOutput result = joinClassroomSessionUseCaseImpl.execute(roomCode, studentUsername);

        // Assert
        assertNotNull(result);
        assertEquals(roomCode, result.roomCode());
        assertEquals(initialStudentCount + 1, result.totalConnectedStudents(),
                "The student count should have incremented by 1");

        // Verify state persistence back down the infrastructure boundary
        verify(sessionRepository, times(1)).save(activeSession);
    }

    @Test
    void execute_SessionNotFound_ShouldThrowIllegalArgumentException() {
        // Arrange
        String invalidCode = "FAKECODE";
        when(sessionRepository.findByCode(invalidCode)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                joinClassroomSessionUseCaseImpl.execute(invalidCode, "any_user")
        );
        verify(sessionRepository, never()).save(any());
    }
}