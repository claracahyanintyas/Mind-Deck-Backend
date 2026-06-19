package org.individualproject.flashcards.application.classroomSession.implementation;

import lombok.RequiredArgsConstructor;
import org.individualproject.flashcards.application.classroomSession.EndClassroomSessionUseCase;
import org.individualproject.flashcards.application.persistence.ClassroomSessionRepository;
import org.individualproject.flashcards.domain.classroomSession.ClassroomSession;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class EndClassroomSessionUseCaseImpl implements EndClassroomSessionUseCase {
    private final ClassroomSessionRepository inMemoryRepository;

    public void execute(String roomCode) {
        ClassroomSession session = inMemoryRepository.findByCode(roomCode);
        if (session != null) {
            inMemoryRepository.deleteByCode(roomCode); // Wipe from RAM
        }
    }
}
