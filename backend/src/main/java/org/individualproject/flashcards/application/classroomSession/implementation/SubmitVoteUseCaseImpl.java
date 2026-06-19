package org.individualproject.flashcards.application.classroomSession.implementation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.individualproject.flashcards.application.classroomSession.DTO.ClassroomSessionOutput;
import org.individualproject.flashcards.application.classroomSession.SubmitVoteUseCase;
import org.individualproject.flashcards.application.persistence.ClassroomSessionRepository;
import org.individualproject.flashcards.domain.classroomSession.ClassroomSession;
import org.individualproject.flashcards.domain.review.ReviewChoice;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class SubmitVoteUseCaseImpl implements SubmitVoteUseCase {
    private final ClassroomSessionRepository inMemoryRepository;

    public ClassroomSessionOutput execute(String roomCode, String username, ReviewChoice choice) {
        ClassroomSession session = inMemoryRepository.findByCode(roomCode);
        if (session == null) {
            throw new IllegalArgumentException("Room not found: " + roomCode);
        }

        // 2. Apply business rules (record the vote)
        session.submitVote(username, choice);

        // 3. Save state back to RAM
        inMemoryRepository.save(session);

        // 4. Return the calculated anonymous tally output
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
