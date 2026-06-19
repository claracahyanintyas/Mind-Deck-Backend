package org.individualproject.flashcards.application.classroomSession.implementation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.individualproject.flashcards.application.classroomSession.FlipCardUseCase;
import org.individualproject.flashcards.application.classroomSession.DTO.ClassroomSessionOutput;
import org.individualproject.flashcards.application.persistence.ClassroomSessionRepository;
import org.individualproject.flashcards.domain.classroomSession.ClassroomSession;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class FlipCardUseCaseImpl implements FlipCardUseCase {
    private final ClassroomSessionRepository inMemoryRepository;

    @Override
    public ClassroomSessionOutput execute(String roomCode) {
        ClassroomSession session = inMemoryRepository.findByCode(roomCode);
        if (session == null) {
            throw new IllegalArgumentException("Room not found: " + roomCode);
        }

        session.flipCard();
        inMemoryRepository.save(session);

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