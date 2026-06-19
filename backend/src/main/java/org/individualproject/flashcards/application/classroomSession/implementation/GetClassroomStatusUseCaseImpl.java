package org.individualproject.flashcards.application.classroomSession.implementation;

import lombok.RequiredArgsConstructor;
import org.individualproject.flashcards.application.classroomSession.DTO.ClassroomSessionOutput;
import org.individualproject.flashcards.application.classroomSession.GetClassroomStatusUseCase;
import org.individualproject.flashcards.application.exception.RoomNotFoundException;
import org.individualproject.flashcards.application.persistence.ClassroomSessionRepository;
import org.individualproject.flashcards.domain.classroomSession.ClassroomSession;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class GetClassroomStatusUseCaseImpl implements GetClassroomStatusUseCase {
    private final ClassroomSessionRepository repository;

    public ClassroomSessionOutput execute(String roomCode) {
        // 1. Fetch the live internal domain model from memory
        ClassroomSession session = repository.findByCode(roomCode);
        if (session == null) throw new RoomNotFoundException();

        // 2. Map the internal state to your clean, immutable Output DTO
        return new ClassroomSessionOutput(
                session.getRoomCode(),
                session.getCurrentCardId(),
                session.getDeckId(),
                session.isCardFlipped(),
                session.getConnectedStudentCount(),
                session.getTotalVotesCount(),
                session.getCurrentVoteTally() // Returns a safe copy of the map
        );
    }
}