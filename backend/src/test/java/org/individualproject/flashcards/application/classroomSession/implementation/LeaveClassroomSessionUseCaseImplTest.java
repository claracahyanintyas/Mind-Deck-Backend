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
class LeaveClassroomSessionUseCaseImplTest {

    @Mock
    private ClassroomSessionRepository sessionRepository;

    @InjectMocks
    private LeaveClassroomSessionUseCaseImpl leaveClassroomSessionUseCaseImpl;

    @Test
    void execute_WithValidRoomCode_ShouldRemoveStudentAndReturnUpdatedOutput() {
        // Arrange
        String roomCode = "LEAVE123";
        String studentUsername = "student_user";

        // Instantiate real domain instance and simulate a student already joined
        ClassroomSession activeSession = new ClassroomSession(roomCode, 10L);
        activeSession.addStudent(studentUsername);

        int initialStudentCount = activeSession.getConnectedStudentCount(); // Should be 1

        when(sessionRepository.findByCode(roomCode)).thenReturn(activeSession);

        // Act
        ClassroomSessionOutput result = leaveClassroomSessionUseCaseImpl.execute(roomCode, studentUsername);

        // Assert
        assertNotNull(result);
        assertEquals(roomCode, result.roomCode());
        assertEquals(initialStudentCount - 1, result.totalConnectedStudents(),
                "The student count should have decremented by 1");

        // Verify changes are saved to the repository
        verify(sessionRepository, times(1)).save(activeSession);
    }

    @Test
    void execute_SessionNotFound_ShouldThrowIllegalArgumentException() {
        // Arrange
        String invalidCode = "MISSING_ROOM";
        when(sessionRepository.findByCode(invalidCode)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                leaveClassroomSessionUseCaseImpl.execute(invalidCode, "any_user")
        );
        verify(sessionRepository, never()).save(any());
    }
}