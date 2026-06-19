package org.individualproject.flashcards.application.classroomSession;

import org.individualproject.flashcards.application.classroomSession.DTO.ClassroomSessionOutput;

public interface ChangeCardUseCase {
    ClassroomSessionOutput execute(String roomCode, Long newCardId);
}
