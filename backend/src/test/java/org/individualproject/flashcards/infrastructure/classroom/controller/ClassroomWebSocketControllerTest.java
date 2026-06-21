package org.individualproject.flashcards.infrastructure.classroom.controller;

import org.individualproject.flashcards.application.classroomSession.ChangeCardUseCase;
import org.individualproject.flashcards.application.classroomSession.DTO.ClassroomSessionOutput;
import org.individualproject.flashcards.application.classroomSession.FlipCardUseCase;
import org.individualproject.flashcards.application.classroomSession.SubmitVoteUseCase;
import org.individualproject.flashcards.domain.review.ReviewChoice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.allow-bean-definition-overriding=true"
)
class ClassroomWebSocketControllerTest {

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

    @MockBean
    private SubmitVoteUseCase submitVoteUseCase;
    @MockBean
    private ChangeCardUseCase changeCardUseCase;
    @MockBean
    private FlipCardUseCase flipCardUseCase;

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
    void handleFlipCard_ShouldReceivePayloadAndBroadcastToTopic() throws Exception {
        String roomCode = "ROOM123";
        var expectedOutput = new ClassroomSessionOutput(
                roomCode, 42L, 10L, true, 5, 2, Collections.emptyMap()
        );

        when(flipCardUseCase.execute(roomCode)).thenReturn(expectedOutput);

        StompSession session = stompClient
                .connectAsync(wsUrl, new StompSessionHandlerAdapter() {})
                .get(3, TimeUnit.SECONDS);

        CompletableFuture<ClassroomSessionOutput> resultFuture = new CompletableFuture<>();

        session.subscribe("/topic/room/" + roomCode, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) { return ClassroomSessionOutput.class; }
            @Override
            public void handleFrame(StompHeaders headers, Object payload) { resultFuture.complete((ClassroomSessionOutput) payload); }
        });

        session.send("/app/room/" + roomCode + "/flip-card", null);

        ClassroomSessionOutput broadcastedPayload = resultFuture.get(3, TimeUnit.SECONDS);
        assertNotNull(broadcastedPayload);
        assertTrue(broadcastedPayload.isCardFlipped());
        assertEquals(roomCode, broadcastedPayload.roomCode());
    }

    @Test
    void handleStudentVote_ShouldProcessVotePayloadAndBroadcastToRoom() throws Exception {
        String roomCode = "ROOM123";
        var voteRequest = new ClassroomWebSocketController.VoteRequest(ReviewChoice.UNSURE);
        var expectedOutput = new ClassroomSessionOutput(
                roomCode, 42L, 10L, false, 5, 1, Collections.emptyMap()
        );

        when(submitVoteUseCase.execute(eq(roomCode), anyString(), eq(ReviewChoice.UNSURE)))
                .thenReturn(expectedOutput);

        StompSession session = stompClient
                .connectAsync(wsUrl, new StompSessionHandlerAdapter() {})
                .get(3, TimeUnit.SECONDS);

        CompletableFuture<ClassroomSessionOutput> resultFuture = new CompletableFuture<>();
        session.subscribe("/topic/room/" + roomCode, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) { return ClassroomSessionOutput.class; }
            @Override
            public void handleFrame(StompHeaders headers, Object payload) { resultFuture.complete((ClassroomSessionOutput) payload); }
        });

        session.send("/app/room/" + roomCode + "/vote", voteRequest);

        ClassroomSessionOutput broadcastedPayload = resultFuture.get(3, TimeUnit.SECONDS);
        assertNotNull(broadcastedPayload);
        assertEquals(1, broadcastedPayload.totalVotesCast());

        verify(submitVoteUseCase, times(1)).execute(eq(roomCode), anyString(), eq(ReviewChoice.UNSURE));
    }
}