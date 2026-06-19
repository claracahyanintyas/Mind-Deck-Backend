package org.individualproject.flashcards.application.classroomSession.implementation;

import lombok.RequiredArgsConstructor;
import org.individualproject.flashcards.application.classroomSession.DTO.ClassroomSessionOutput;
import org.individualproject.flashcards.application.classroomSession.StartClassroomSessionUseCase;
import org.individualproject.flashcards.application.persistence.ClassroomSessionRepository;
import org.individualproject.flashcards.domain.classroomSession.ClassroomSession;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service @RequiredArgsConstructor
public class StartClassroomSessionUseCaseImpl implements StartClassroomSessionUseCase {
    private final ClassroomSessionRepository sessionRepository;

    public ClassroomSessionOutput execute(Long deckId) {
        String roomCode;

        // Loop guarantees that we never exit until we find an UNUSED code
        do {
            roomCode = generateRandomCode();
        } while (sessionRepository.existsByCode(roomCode)); // <-- Check the repository port

        ClassroomSession newSession = new ClassroomSession(roomCode, deckId);
        sessionRepository.save(newSession);

        return new ClassroomSessionOutput(
                newSession.getRoomCode(),
                newSession.getCurrentCardId(),
                newSession.getDeckId(),
                newSession.isCardFlipped(),
                newSession.getConnectedStudentCount(),
                newSession.getTotalVotesCount(),
                newSession.getCurrentVoteTally()
        );
    }

    private String generateRandomCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
