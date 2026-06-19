package org.individualproject.flashcards.infrastructure.classroom.listener;

import lombok.RequiredArgsConstructor;
import org.individualproject.flashcards.application.classroomSession.JoinClassroomSessionUseCase;
import org.individualproject.flashcards.application.classroomSession.LeaveClassroomSessionUseCase;
import org.individualproject.flashcards.application.classroomSession.DTO.ClassroomSessionOutput;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final JoinClassroomSessionUseCase joinClassroomSessionUseCase;
    private final LeaveClassroomSessionUseCase leaveClassroomSessionUseCase;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();

        if (destination != null && destination.startsWith("/topic/room/")) {
            String roomCode = destination.replace("/topic/room/", "");

            // Extract username safely from the STOMP principal
            java.security.Principal principal = headerAccessor.getUser();
            if (principal == null) {
                throw new IllegalStateException("User is not authenticated via WebSocket handshake");
            }
            String username = principal.getName();

            // Cache details for the disconnect lifecycle
            Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
            if (sessionAttributes != null) {
                sessionAttributes.put("roomCode", roomCode);
                sessionAttributes.put("username", username);
            }

            ClassroomSessionOutput updatedStatus = joinClassroomSessionUseCase.execute(roomCode, username);
            messagingTemplate.convertAndSend("/topic/room/" + roomCode, updatedStatus);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();

        if (sessionAttributes != null && sessionAttributes.containsKey("roomCode")) {
            String roomCode = (String) sessionAttributes.get("roomCode");
            String username = (String) sessionAttributes.get("username");

            // Execute leave flow via boundary hook
            ClassroomSessionOutput updatedStatus = leaveClassroomSessionUseCase.execute(roomCode, username);
            messagingTemplate.convertAndSend("/topic/room/" + roomCode, updatedStatus);
        }
    }
}