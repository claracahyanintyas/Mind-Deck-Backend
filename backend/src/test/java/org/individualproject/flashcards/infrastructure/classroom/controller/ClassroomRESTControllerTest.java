package org.individualproject.flashcards.infrastructure.classroom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.antlr.v4.runtime.misc.Array2DHashSet;
import org.individualproject.flashcards.infrastructure.config.database.JpaRepository.DeckJpaRepository;
import org.individualproject.flashcards.infrastructure.config.database.JpaRepository.UserJpaRepository;
import org.individualproject.flashcards.infrastructure.config.database.entity.DeckEntity;
import org.individualproject.flashcards.infrastructure.config.database.entity.UserEntity;
import org.individualproject.flashcards.infrastructure.classroom.controller.ClassroomRESTController.StartSessionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClassroomRESTControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeckJpaRepository deckRepository;

    @Autowired
    private UserJpaRepository userRepository;

    private final String baseUrl = "/api/classroom";

    @Test
    @WithMockUser(username = "guest")
    void startSession_ValidDeckId_ReturnsClassroomSessionOutput() throws Exception {
        // 1. Arrange - Seed the database with valid data prerequisites
        var user = new UserEntity(
                1L,
                "guest",
                "",
                "",
                OffsetDateTime.now(),
                false,
                new Array2DHashSet<>(),
                "",
                Instant.now()
        );
        var savedUser = userRepository.save(user);

        var deck = new DeckEntity(
                null,
                "Classroom Deck",
                "Description",
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                false,
                savedUser,
                new ArrayList<>()
        );
        // Ensure you have at least one card in the deck if your use-case enforces it
        var savedDeck = deckRepository.save(deck);

        var request = new StartSessionRequest(savedDeck.getId());

        // 2. Act & Assert
        mockMvc.perform(post(baseUrl + "/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deckId").value(savedDeck.getId()));
    }
}