package org.individualproject.flashcards.infrastructure.classroom.controller;

import lombok.RequiredArgsConstructor;
import org.individualproject.flashcards.application.classroomSession.ChangeCardUseCase;
import org.individualproject.flashcards.application.classroomSession.DTO.ClassroomSessionOutput;
import org.individualproject.flashcards.application.classroomSession.FlipCardUseCase;
import org.individualproject.flashcards.application.classroomSession.SubmitVoteUseCase;
import org.individualproject.flashcards.domain.review.ReviewChoice;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ClassroomWebSocketController {
    private final SubmitVoteUseCase submitVoteUseCase;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChangeCardUseCase changeCardUseCase;
    private final FlipCardUseCase flipCardUseCase;

    // 1. Consistent Request DTO naming inside the presentation layer
    public record VoteRequest(ReviewChoice choice) {}
    public record ChangeCardRequest(Long cardId) {}

    @MessageMapping("/room/{roomCode}/vote")
    public void handleStudentVote(
            @DestinationVariable String roomCode,
            VoteRequest request,
            SimpMessageHeaderAccessor headerAccessor, // 👈 Add this to get the raw socket session
            Principal principal) {

        // Fallback: If not logged in, use their unique WebSocket Session ID so the domain can track them
        String username = (principal != null) ? principal.getName() : headerAccessor.getSessionId();

        if (username == null) {
            username = "AnonymousGuest_" + java.util.UUID.randomUUID().toString().substring(0, 5);
        }

        // Process the vote safely without crashing
        ClassroomSessionOutput updatedTally = submitVoteUseCase.execute(roomCode, username, request.choice());

        // Broadcast the new tallies down to the room
        messagingTemplate.convertAndSend("/topic/room/" + roomCode, updatedTally);
    }

    // 3. Teacher Navigation Endpoint
    @MessageMapping("/room/{roomCode}/next-card")
    public void handleNextCard(@DestinationVariable String roomCode, ChangeCardRequest request) {
        ClassroomSessionOutput updatedStatus = changeCardUseCase.execute(roomCode, request.cardId());

        messagingTemplate.convertAndSend("/topic/room/" + roomCode, updatedStatus);
    }

    @MessageMapping("/room/{roomCode}/flip-card")
    public void handleFlipCard(@DestinationVariable String roomCode) {
        ClassroomSessionOutput updatedStatus = flipCardUseCase.execute(roomCode);

        // Broadcast the flipped state to everyone in the room instantly
        messagingTemplate.convertAndSend("/topic/room/" + roomCode, updatedStatus);
    }

}
