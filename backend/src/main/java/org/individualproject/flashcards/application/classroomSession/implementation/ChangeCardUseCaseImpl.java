package org.individualproject.flashcards.application.classroomSession.implementation;

import lombok.RequiredArgsConstructor;
import org.individualproject.flashcards.application.classroomSession.ChangeCardUseCase;
import org.individualproject.flashcards.application.classroomSession.DTO.ClassroomSessionOutput;
import org.individualproject.flashcards.application.persistence.ClassroomSessionRepository;
import org.individualproject.flashcards.domain.classroomSession.ClassroomSession;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeCardUseCaseImpl implements ChangeCardUseCase {
    private final ClassroomSessionRepository inMemoryRepository;

    public ClassroomSessionOutput execute(String roomCode, Long newCardId) {
        // 1. Retrieve from your in-memory port
        ClassroomSession session = inMemoryRepository.findByCode(roomCode);
        if (session == null) {
            throw new IllegalArgumentException("Active room not found: " + roomCode);
        }
        // 2. Execute business domain logic (mutates card state, clears active vote map)
        session.changeCard(newCardId);

        // 3. Save modified state back to the port
        inMemoryRepository.save(session);

        // 4. Map to clean output representation
        return new ClassroomSessionOutput(
                session.getRoomCode(),
                session.getCurrentCardId(),
                session.getDeckId(),
                session.isCardFlipped(),
                session.getConnectedStudentCount(),
                session.getTotalVotesCount(),
                session.getCurrentVoteTally()
        );
    }
}
