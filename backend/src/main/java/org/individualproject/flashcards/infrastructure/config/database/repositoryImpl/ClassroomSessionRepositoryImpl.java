package org.individualproject.flashcards.infrastructure.config.database.repositoryImpl;

import jakarta.annotation.PostConstruct;
import org.individualproject.flashcards.application.persistence.ClassroomSessionRepository;
import org.individualproject.flashcards.domain.classroomSession.ClassroomSession;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ClassroomSessionRepositoryImpl implements ClassroomSessionRepository {

    private final Map<String, ClassroomSession> storage = new ConcurrentHashMap<>();

    @PostConstruct
    public void initDevData() {
        // Pre-populates room XYZ123 tied to deck ID 1
        storage.put("XYZ123", new ClassroomSession("XYZ123", 1L));
    }

    @Override
    public void save(ClassroomSession session) {
        storage.put(session.getRoomCode(), session);
    }

    @Override
    public ClassroomSession findByCode(String roomCode) {
        return storage.get(roomCode);
    }

    @Override
    public void deleteByCode(String roomCode) {
        storage.remove(roomCode);
    }

    @Override
    public boolean existsByCode(String roomCode) {
        return storage.containsKey(roomCode);
    }
}