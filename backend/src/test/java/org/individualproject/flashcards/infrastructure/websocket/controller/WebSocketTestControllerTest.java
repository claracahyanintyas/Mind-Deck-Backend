package org.individualproject.flashcards.infrastructure.websocket.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.allow-bean-definition-overriding=true"
)
class WebSocketTestControllerTest {

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        @Primary
        @Order(Ordered.HIGHEST_PRECEDENCE)
        public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(csrf -> csrf.disable())
                    .cors(cors -> cors.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .build();
        }
    }

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;
    private String wsUrl;

    @BeforeEach
    void setUp() {
        List<Transport> transports = List.of(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);

        this.stompClient = new WebSocketStompClient(sockJsClient);
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        this.wsUrl = "http://localhost:" + port + "/ws-classroom";
    }

    @Test
    void handleTestMessage_ShouldEchoPayloadBackToSubscribedRoom() throws Exception {
        // Arrange
        String roomCode = "SMOKE_TEST_ROOM";
        var outgoingMessage = new WebSocketTestController.TestMessage("Developer", "Hello Spring Boot WebSockets!");

        StompSession session = stompClient
                .connectAsync(wsUrl, new StompSessionHandlerAdapter() {})
                .get(3, TimeUnit.SECONDS);

        CompletableFuture<WebSocketTestController.TestMessage> resultFuture = new CompletableFuture<>();

        // Subscribe to the dynamic @SendTo broker channel destination
        session.subscribe("/topic/room/" + roomCode, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return WebSocketTestController.TestMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                resultFuture.complete((WebSocketTestController.TestMessage) payload);
            }
        });

        // Act: Deliver message payload directly to the message controller route
        session.send("/app/room/" + roomCode + "/test", outgoingMessage);

        // Assert
        WebSocketTestController.TestMessage broadcastedPayload = resultFuture.get(3, TimeUnit.SECONDS);

        assertNotNull(broadcastedPayload);
        assertEquals("Developer", broadcastedPayload.sender());
        assertEquals("Server echo: Hello Spring Boot WebSockets!", broadcastedPayload.content());
    }
}