package org.individualproject.flashcards.application.classroomSession;

import org.individualproject.flashcards.application.classroomSession.DTO.ClassroomSessionOutput;

public interface FlipCardUseCase {
    ClassroomSessionOutput execute(String roomCode);
}