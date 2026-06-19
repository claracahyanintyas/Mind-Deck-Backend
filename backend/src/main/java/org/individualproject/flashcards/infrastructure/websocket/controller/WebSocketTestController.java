package org.individualproject.flashcards.infrastructure.websocket.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketTestController {

    // A simple record to match incoming JSON payloads
    public record TestMessage(String sender, String content) {}

    // When frontend sends to: /app/room/{roomCode}/test
    @MessageMapping("/room/{roomCode}/test")
    // It broadcasts automatically to: /topic/room/{roomCode}
    @SendTo("/topic/room/{roomCode}")
    public TestMessage handleTestMessage(@DestinationVariable String roomCode, TestMessage message) {
        System.out.println("Received message in room " + roomCode + " from " + message.sender());
        return new TestMessage(message.sender(), "Server echo: " + message.content());
    }
}