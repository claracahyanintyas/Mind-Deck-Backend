package org.individualproject.flashcards.application.classroomSession;

import org.individualproject.flashcards.application.classroomSession.DTO.ClassroomSessionOutput;
import org.individualproject.flashcards.domain.review.ReviewChoice;

public interface SubmitVoteUseCase {
    ClassroomSessionOutput execute(String roomCode, String username, ReviewChoice choice);
}
