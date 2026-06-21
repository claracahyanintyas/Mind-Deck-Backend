package org.individualproject.flashcards.infrastructure.classroom.listener;

import org.individualproject.flashcards.application.classroomSession.JoinClassroomSessionUseCase;
import org.individualproject.flashcards.application.classroomSession.LeaveClassroomSessionUseCase;
import org.individualproject.flashcards.application.classroomSession.DTO.ClassroomSessionOutput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.CloseStatus; // 👈 This fixes the compiler error!
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSocketEventListenerTest {

    @Mock
    private JoinClassroomSessionUseCase joinClassroomSessionUseCase;

    @Mock
    private LeaveClassroomSessionUseCase leaveClassroomSessionUseCase;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private WebSocketEventListener webSocketEventListener;

    @Test
    void handleWebSocketSubscribeListener_WithValidDestination_ShouldJoinAndBroadcast() {
        // Arrange
        String roomCode = "ROOMXYZ";
        String destination = "/topic/room/" + roomCode;
        String username = "student_alpha";

        Principal mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn(username);

        Map<String, Object> sessionAttributes = new HashMap<>();

        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.create(SimpMessageType.SUBSCRIBE);
        headers.setDestination(destination);
        headers.setUser(mockPrincipal);
        headers.setSessionAttributes(sessionAttributes);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());

        SessionSubscribeEvent event = new SessionSubscribeEvent(this, message);
        ClassroomSessionOutput expectedOutput = new ClassroomSessionOutput(
                roomCode, 1L, 10L, false, 1, 0, Collections.emptyMap()
        );

        when(joinClassroomSessionUseCase.execute(roomCode, username)).thenReturn(expectedOutput);

        // Act
        webSocketEventListener.handleWebSocketSubscribeListener(event);

        // Assert
        assertEquals(roomCode, sessionAttributes.get("roomCode"));
        assertEquals(username, sessionAttributes.get("username"));

        verify(joinClassroomSessionUseCase, times(1)).execute(roomCode, username);
        verify(messagingTemplate, times(1)).convertAndSend("/topic/room/" + roomCode, expectedOutput);
    }

    @Test
    void handleWebSocketSubscribeListener_UnauthenticatedUser_ShouldThrowIllegalStateException() {
        // Arrange
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.create(SimpMessageType.SUBSCRIBE);
        headers.setDestination("/topic/room/ROOM123");
        headers.setUser(null);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());
        SessionSubscribeEvent event = new SessionSubscribeEvent(this, message);

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                webSocketEventListener.handleWebSocketSubscribeListener(event)
        );

        verifyNoInteractions(joinClassroomSessionUseCase, messagingTemplate);
    }

    @Test
    void handleWebSocketDisconnectListener_WithCachedAttributes_ShouldLeaveAndBroadcast() {
        // Arrange
        String roomCode = "ROOMXYZ";
        String username = "student_alpha";

        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put("roomCode", roomCode);
        sessionAttributes.put("username", username);

        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.create(SimpMessageType.DISCONNECT);
        headers.setSessionAttributes(sessionAttributes);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());

        // Passed CloseStatus.NORMAL directly as an object here instead of a String representation
        SessionDisconnectEvent event = new SessionDisconnectEvent(this, message, "session-id", CloseStatus.NORMAL);
        ClassroomSessionOutput expectedOutput = new ClassroomSessionOutput(
                roomCode, 1L, 10L, false, 0, 0, Collections.emptyMap()
        );

        when(leaveClassroomSessionUseCase.execute(roomCode, username)).thenReturn(expectedOutput);

        // Act
        webSocketEventListener.handleWebSocketDisconnectListener(event);

        // Assert
        verify(leaveClassroomSessionUseCase, times(1)).execute(roomCode, username);
        verify(messagingTemplate, times(1)).convertAndSend("/topic/room/" + roomCode, expectedOutput);
    }
}