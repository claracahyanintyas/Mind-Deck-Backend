package org.individualproject.flashcards.infrastructure.user.controller;

import org.individualproject.flashcards.application.user.DTO.UserPublicData;
import org.individualproject.flashcards.application.user.GetUserByUsernameUseCase;
import org.individualproject.flashcards.infrastructure.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // Standalone MVC routing focus
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetUserByUsernameUseCase getUserByUsernameUseCase;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUser_ShouldExtractUsernameFromContextAndReturnUserPublicData() throws Exception {
        // Arrange
        String mockUsername = "authenticated_user_123";
        var publicData = new UserPublicData(
                100L,
                mockUsername,
                "user@test.com",
                OffsetDateTime.now(),
                true,
                Set.of("ROLE_USER")
        );

        // Mock the global Spring Security context for the active test thread
        Authentication mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getName()).thenReturn(mockUsername);
        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);

        when(getUserByUsernameUseCase.getUser(mockUsername)).thenReturn(publicData);

        // Act & Assert
        mockMvc.perform(get("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.username").value(mockUsername))
                .andExpect(jsonPath("$.email").value("user@test.com"));

        verify(getUserByUsernameUseCase, times(1)).getUser(mockUsername);
    }
}