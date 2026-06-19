package org.individualproject.flashcards.application.persistence;

import org.individualproject.flashcards.domain.classroomSession.ClassroomSession;

public interface ClassroomSessionRepository {
    void save(ClassroomSession session);
    ClassroomSession findByCode(String roomCode);
    void deleteByCode(String roomCode);
    boolean existsByCode(String roomCode);
}