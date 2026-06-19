package org.individualproject.flashcards.application.classroomSession.implementation;

import lombok.RequiredArgsConstructor;
import org.individualproject.flashcards.application.classroomSession.DTO.ClassroomSessionOutput;
import org.individualproject.flashcards.application.classroomSession.LeaveClassroomSessionUseCase;
import org.individualproject.flashcards.application.persistence.ClassroomSessionRepository;
import org.individualproject.flashcards.domain.classroomSession.ClassroomSession;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor @Service
public class LeaveClassroomSessionUseCaseImpl implements LeaveClassroomSessionUseCase {
    private final ClassroomSessionRepository sessionRepository;

    public ClassroomSessionOutput execute(String roomCode, String username) {
        ClassroomSession session = sessionRepository.findByCode(roomCode);
        if (session == null) {
            throw new IllegalArgumentException("Session not found: " + roomCode);
        }

        session.removeStudent(username);
        sessionRepository.save(session);

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
