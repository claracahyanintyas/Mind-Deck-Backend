package org.individualproject.flashcards.infrastructure.classroom.controller;

import lombok.RequiredArgsConstructor;
import org.individualproject.flashcards.application.classroomSession.DTO.ClassroomSessionOutput;
import org.individualproject.flashcards.application.classroomSession.StartClassroomSessionUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/classroom")
public class ClassroomRESTController {
    private final StartClassroomSessionUseCase startClassroomSessionUseCase;
    // Clean consistent request naming!
    public record StartSessionRequest(Long deckId) {}

    @PostMapping("/start")
    public ResponseEntity<ClassroomSessionOutput> startSession(@RequestBody StartSessionRequest request) {
        ClassroomSessionOutput sessionOutput = startClassroomSessionUseCase.execute(request.deckId());
        return ResponseEntity.ok(sessionOutput);
    }
}
