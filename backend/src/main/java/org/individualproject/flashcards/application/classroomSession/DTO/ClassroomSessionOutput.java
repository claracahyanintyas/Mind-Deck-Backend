package org.individualproject.flashcards.application.classroomSession.DTO;

import org.individualproject.flashcards.domain.review.ReviewChoice;

import java.util.Map;

public record ClassroomSessionOutput(
        String roomCode,
        Long currentCardId,
        Long deckId,
        boolean isCardFlipped,
        int totalConnectedStudents,
        int totalVotesCast,
        Map<ReviewChoice, Long> currentVoteBreakdown
) {
}
