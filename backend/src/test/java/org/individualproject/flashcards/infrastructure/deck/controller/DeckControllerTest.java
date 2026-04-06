package org.individualproject.flashcards.infrastructure.deck.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.individualproject.flashcards.infrastructure.config.database.entity.DeckEntity;
import org.individualproject.flashcards.infrastructure.config.database.repository.DeckRepository;
import org.individualproject.flashcards.infrastructure.deck.DTO.CreateDeckRequest;
import org.individualproject.flashcards.infrastructure.deck.DTO.UpdateDeckRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.time.OffsetDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DeckControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeckRepository deckRepository;

    private final String baseUrl = "/api/decks";

    @Test
    void createDeck_AllFields_ReturnsDeck() throws Exception {
        var name = "deck";
        var description = "description";
        var isPrivate = true;
        var request = new CreateDeckRequest(name, description, isPrivate);

        mockMvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description));
    }
    @Test
    void createDeck_EmptyName_ThrowsException() throws Exception {
        var name = "";
        var description = "description";
        var isPrivate = true;
        var request = new CreateDeckRequest(name, description, isPrivate);

        mockMvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0]").value("name cannot be null"));
    }
    @Test
    void getDeck_validId_returnsDeck() throws Exception {
        var name = "name";
        var description = "description";
        var isPrivate = true;
        var entity = new DeckEntity(1L, name, description, OffsetDateTime.now(), OffsetDateTime.now(), isPrivate);
        var saved = deckRepository.save(entity);

        mockMvc.perform(get(baseUrl + '/' + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description));
    }
    @Test
    void getDeck_invalidId_ThrowsException() throws Exception {
        mockMvc.perform(get(baseUrl + '/' + -1L))
                .andExpect(status().isBadRequest());
    }
    @Test
    void getDeck_notFound_ThrowsException() throws Exception {
        mockMvc.perform(get(baseUrl + '/' + 999L))
                .andExpect(status().isNotFound());
    }
    @Test
    void getDecks_availableDecks_returnsDecks() throws Exception {
        var entity = new DeckEntity(1L, "name", "description", OffsetDateTime.now(), OffsetDateTime.now(), true);
        deckRepository.save(entity);
        var entity2 = new DeckEntity(2L,  "name2", "description2", OffsetDateTime.now(), OffsetDateTime.now(), false);
        deckRepository.save(entity2);

        mockMvc.perform(get(baseUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].name").value(entity.getName()));
    }
    @Test
    void updateDeck_AllFields_ReturnsDeck() throws Exception {
        var entity = new DeckEntity(1L, "name", "description", OffsetDateTime.now(), OffsetDateTime.now(), true);
        deckRepository.save(entity);
        var name = "new name";
        var description = "new description";
        var isPrivate = false;
        var request = new UpdateDeckRequest(name, description, isPrivate);

        mockMvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.description").value(description));
    }
    @Test
    void updateDeck_EmptyName_ReturnsDeck() throws Exception {
        var entity = new DeckEntity(1L, "name", "description", OffsetDateTime.now(), OffsetDateTime.now(), true);
        deckRepository.save(entity);
        var name = "";
        var description = "new description";
        var isPrivate = false;
        var request = new UpdateDeckRequest(name, description, isPrivate);

        mockMvc.perform(post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void deleteDeck_ReturnsEmpty() throws Exception {
        var entity = new DeckEntity(1L, "name", "description", OffsetDateTime.now(), OffsetDateTime.now(), true);
        deckRepository.save(entity);
        mockMvc.perform(delete(baseUrl + "/" + entity.getId()))
                .andExpect(status().isNoContent());
    }
}