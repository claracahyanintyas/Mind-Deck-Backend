package org.individualproject.flashcards.infrastructure.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.individualproject.flashcards.application.security.AuthService;
import org.individualproject.flashcards.application.security.DTO.LoginInput;
import org.individualproject.flashcards.application.security.DTO.RegisterCommand;
import org.individualproject.flashcards.application.security.DTO.jwtAuthOutput;
import org.individualproject.flashcards.application.security.DTO.jwtGuestOutput;
import org.individualproject.flashcards.application.security.GuestService;
import org.individualproject.flashcards.application.security.RefreshTokenUseCase;
import org.individualproject.flashcards.application.security.RegisterUserUseCase;
import org.individualproject.flashcards.application.user.DTO.UserPublicData;
import org.individualproject.flashcards.infrastructure.security.DTO.LoginRequest;
import org.individualproject.flashcards.infrastructure.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Disables global spring security filter chain interceptors for standalone MVC routing focus
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @MockBean
    private AuthService authService;

    @MockBean
    private GuestService guestService;

    @MockBean
    private RegisterUserUseCase registerUserUseCase;

    @MockBean
    private RefreshTokenUseCase refreshTokenUseCase;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createGuestSession_WithoutCookie_ShouldReturnNewGuestAndCookies() throws Exception {
        // Arrange
        var publicData = new UserPublicData(1L, "guest_abc123", null, OffsetDateTime.now(), true, Set.of("ROLE_GUEST"));
        var guestOutput = new jwtGuestOutput("access-tok", "refresh-tok", publicData);

        when(guestService.createGuestSession()).thenReturn(guestOutput);
        when(tokenProvider.generateJwtCookie("access-tok")).thenReturn(ResponseCookie.from("accessToken", "access-tok").build());
        when(tokenProvider.generateRefreshCookie("refresh-tok")).thenReturn(ResponseCookie.from("refreshToken", "refresh-tok").build());

        // Act & Assert
        mockMvc.perform(post("/api/auth/guest"))
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(header().stringValues(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.hasItem(org.hamcrest.Matchers.containsString("accessToken=access-tok"))))
                .andExpect(header().stringValues(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.hasItem(org.hamcrest.Matchers.containsString("refreshToken=refresh-tok"))))
                .andExpect(jsonPath("$.username").value("guest_abc123"));
    }

    @Test
    void createGuestSession_WithValidGuestCookie_ShouldRefreshExistingGuestSession() throws Exception {
        // Arrange
        String existingToken = "valid-refresh";
        var publicData = new UserPublicData(1L, "guest_abc123", null, OffsetDateTime.now(), true, Set.of("ROLE_GUEST"));
        var guestOutput = new jwtGuestOutput("new-access", "new-refresh", publicData);

        when(tokenProvider.validateToken(existingToken)).thenReturn(true);
        when(tokenProvider.getUsername(existingToken)).thenReturn("guest_abc123");
        when(guestService.refreshExistingGuestSession("guest_abc123")).thenReturn(guestOutput);

        when(tokenProvider.generateJwtCookie("new-access")).thenReturn(ResponseCookie.from("accessToken", "new-access").build());
        when(tokenProvider.generateRefreshCookie("new-refresh")).thenReturn(ResponseCookie.from("refreshToken", "new-refresh").build());

        // Act & Assert
        mockMvc.perform(post("/api/auth/guest").cookie(new Cookie("refreshToken", existingToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("guest_abc123"));
    }

    @Test
    void login_WithValidBody_ShouldSetSecureCookiesAndReturnUser() throws Exception {
        // Arrange
        var request = new LoginRequest("user", "password");
        var publicData = new UserPublicData(2L, "user", "user@test.com", OffsetDateTime.now(), true, Set.of("ROLE_USER"));
        var authOutput = new jwtAuthOutput("access", "refresh", publicData);

        when(authService.login(any(LoginInput.class))).thenReturn(authOutput);
        when(tokenProvider.generateJwtCookie("access")).thenReturn(ResponseCookie.from("accessToken", "access").build());
        when(tokenProvider.generateRefreshCookie("refresh")).thenReturn(ResponseCookie.from("refreshToken", "refresh").build());

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.email").value("user@test.com"));
    }

    @Test
    void logoutUser_ShouldClearContextAndWipeCookies() throws Exception {
        // Arrange
        when(tokenProvider.getCleanJwtCookie()).thenReturn(ResponseCookie.from("accessToken", "").maxAge(0).build());
        when(tokenProvider.getCleanRefreshCookie()).thenReturn(ResponseCookie.from("refreshToken", "").maxAge(0).build());

        // Act & Assert
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(body -> "User logged out successfully and secure cookies were destroyed.".equals(body.getResponse().getContentAsString()));
    }

    @Test
    void register_StandardFlow_ShouldPassNullGuestAndReturnAuthOutput() throws Exception {
        // Arrange
        var command = new RegisterCommand("registeredUser", "reg@test.com", "pass1234");
        var publicData = new UserPublicData(3L, "registeredUser", "reg@test.com", OffsetDateTime.now(), true, Set.of("ROLE_USER"));
        var authOutput = new jwtAuthOutput("access-reg", "refresh-reg", publicData);

        when(registerUserUseCase.registerUser(any(RegisterCommand.class), eq(null))).thenReturn(authOutput);
        when(tokenProvider.generateJwtCookie("access-reg")).thenReturn(ResponseCookie.from("accessToken", "access-reg").build());
        when(tokenProvider.generateRefreshCookie("refresh-reg")).thenReturn(ResponseCookie.from("refreshToken", "refresh-reg").build());

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("registeredUser"));
    }

    @Test
    void register_GuestUpgradeFlow_ShouldDetectGuestUsernameFromSecurityContext() throws Exception {
        // Arrange
        var command = new RegisterCommand("guest_upgrade", "up@test.com", "pass1234");
        var publicData = new UserPublicData(4L, "guest_upgrade", "up@test.com", OffsetDateTime.now(), true, Set.of("ROLE_USER"));
        var authOutput = new jwtAuthOutput("access-up", "refresh-up", publicData);

        // Seed Security Context Thread manually with an authenticated user holding ROLE_GUEST
        UserDetails guestDetails = new User("guest_upgrade", "", List.of(new SimpleGrantedAuthority("ROLE_GUEST")));
        var authentication = new UsernamePasswordAuthenticationToken(guestDetails, null, guestDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(registerUserUseCase.registerUser(any(RegisterCommand.class), eq("guest_upgrade"))).thenReturn(authOutput);
        when(tokenProvider.generateJwtCookie("access-up")).thenReturn(ResponseCookie.from("accessToken", "access-up").build());
        when(tokenProvider.generateRefreshCookie("refresh-up")).thenReturn(ResponseCookie.from("refreshToken", "refresh-up").build());

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("guest_upgrade"));
    }

    @Test
    void refreshSession_WithValidCookieValue_ShouldRotateTokens() throws Exception {
        // Arrange
        String currentTokenValue = "active-refresh-token-from-browser";
        var publicData = new UserPublicData(5L, "tokenUser", "tk@test.com", OffsetDateTime.now(), true, Set.of("ROLE_USER"));
        var rotatedOutput = new jwtAuthOutput("new-access-rotated", "new-refresh-rotated", publicData);

        when(tokenProvider.getCookieValue(any(), eq("refreshToken"))).thenReturn(currentTokenValue);
        when(refreshTokenUseCase.rotateSessionTokens(currentTokenValue)).thenReturn(rotatedOutput);

        when(tokenProvider.generateJwtCookie("new-access-rotated")).thenReturn(ResponseCookie.from("accessToken", "new-access-rotated").build());
        when(tokenProvider.generateRefreshCookie("new-refresh-rotated")).thenReturn(ResponseCookie.from("refreshToken", "new-refresh-rotated").build());

        // Act & Assert
        mockMvc.perform(post("/api/auth/refresh"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("tokenUser"));
    }

    @Test
    void refreshSession_CookieMissing_ShouldReturn401Unauthorized() throws Exception {
        // Arrange
        when(tokenProvider.getCookieValue(any(), eq("refreshToken"))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/api/auth/refresh"))
                .andExpect(status().isUnauthorized())
                .andExpect(body -> "Session token missing.".equals(body.getResponse().getContentAsString()));

        verifyNoInteractions(refreshTokenUseCase);
    }
}