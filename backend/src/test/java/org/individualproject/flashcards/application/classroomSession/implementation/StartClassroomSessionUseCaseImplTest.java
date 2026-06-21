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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StartClassroomSessionUseCaseImplTest {

    @Mock
    private ClassroomSessionRepository sessionRepository;

    @InjectMocks
    private StartClassroomSessionUseCaseImpl startClassroomSessionUseCaseImpl;

    @Test
    void execute_ShouldGenerateUniqueCodeAndCreateSession() {
        // Arrange
        Long deckId = 10L;

        // Simulate that the first generated code is completely unique right away
        when(sessionRepository.existsByCode(anyString())).thenReturn(false);

        // Act
        ClassroomSessionOutput result = startClassroomSessionUseCaseImpl.execute(deckId);

        // Assert
        assertNotNull(result);
        assertNotNull(result.roomCode());
        assertEquals(6, result.roomCode().length(), "The room code should be 6 characters long");
        assertEquals(deckId, result.deckId());
        assertEquals(0, result.totalConnectedStudents());
        assertFalse(result.isCardFlipped());

        // Verify it checked the code availability and saved the session instance exactly once
        verify(sessionRepository, times(1)).existsByCode(anyString());
        verify(sessionRepository, times(1)).save(any(ClassroomSession.class));
    }

    @Test
    void execute_WhenCodeCollides_ShouldLoopUntilUniqueCodeIsFound() {
        // Arrange
        Long deckId = 10L;

        // Simulate a collision scenario:
        // First check returns true (code already exists), second check returns false (new code is unique)
        when(sessionRepository.existsByCode(anyString()))
                .thenReturn(true)  // First iteration triggers the loop again
                .thenReturn(false); // Second iteration breaks out of the loop

        // Act
        ClassroomSessionOutput result = startClassroomSessionUseCaseImpl.execute(deckId);

        // Assert
        assertNotNull(result);
        assertNotNull(result.roomCode());

        // Verify that existsByCode was queried twice due to the initial collision loop
        verify(sessionRepository, times(2)).existsByCode(anyString());
        verify(sessionRepository, times(1)).save(any(ClassroomSession.class));
    }
}