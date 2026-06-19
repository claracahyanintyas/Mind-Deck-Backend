package org.individualproject.flashcards.application.classroomSession;

import org.individualproject.flashcards.application.classroomSession.DTO.ClassroomSessionOutput;

public interface LeaveClassroomSessionUseCase {
    ClassroomSessionOutput execute(String roomCode, String username);
}
