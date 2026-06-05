package org.individualproject.flashcards.infrastructure.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.security.AuthService;
import org.individualproject.flashcards.application.security.DTO.LoginInput;
import org.individualproject.flashcards.application.security.DTO.RegisterCommand;
import org.individualproject.flashcards.application.security.DTO.jwtAuthOutput;
import org.individualproject.flashcards.application.security.DTO.jwtGuestOutput;
import org.individualproject.flashcards.application.security.GuestService;
import org.individualproject.flashcards.application.security.RefreshTokenUseCase;
import org.individualproject.flashcards.application.security.RegisterUserUseCase;
import org.individualproject.flashcards.infrastructure.security.DTO.LoginRequest;
import org.individualproject.flashcards.infrastructure.security.DTO.RegisterRequest;
import org.individualproject.flashcards.infrastructure.security.jwt.JwtTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private JwtTokenProvider tokenProvider;

    private AuthService authService;
    private GuestService guestService;
    private RegisterUserUseCase registerUserUseCase;
    private RefreshTokenUseCase refreshTokenUseCase;

    @PostMapping("/guest")
    public ResponseEntity<?> createGuestSession() {
        jwtGuestOutput jwtGuestOutput = guestService.createGuestSession();

        ResponseCookie jwtCookie = tokenProvider.generateJwtCookie(jwtGuestOutput.token());
        ResponseCookie refreshCookie = tokenProvider.generateRefreshCookie(jwtGuestOutput.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body("Guest session initialized as: " + jwtGuestOutput.user().username());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        LoginInput loginInput = new LoginInput(loginRequest.usernameOrEmail(), loginRequest.password());
        jwtAuthOutput jwtAuthOutput = authService.login(loginInput);

        // 1. Extract the raw token string and turn it into a secure HttpOnly cookie object
        ResponseCookie jwtCookie = tokenProvider.generateJwtCookie(jwtAuthOutput.token());
        ResponseCookie refreshCookie = tokenProvider.generateRefreshCookie(jwtAuthOutput.refreshToken());

        // 2. Attach it to the HTTP headers. Return only the public profile metadata to React
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(jwtAuthOutput.user());
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser() {
        // 1. Clear memory credentials out of Spring Security context
        SecurityContextHolder.clearContext();

        // 2. Build an empty cookie with maxAge(0) using your existing helper
        ResponseCookie cleanCookie = tokenProvider.getCleanJwtCookie();

        // 3. Send the clean cookie back to overwrite and delete the one stored in the browser
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
                .body("User logged out successfully and secure cookie was destroyed.");
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        // Find the current guest authentication string from the cookie context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        RegisterCommand registerCommand = new RegisterCommand(registerRequest.username(), registerRequest.email(), registerRequest.password());
        jwtAuthOutput authOutput = registerUserUseCase.registerUser(registerCommand, auth.getName());

        // 1. Convert the upgraded session's token into an HttpOnly cookie
        ResponseCookie jwtCookie = tokenProvider.generateJwtCookie(authOutput.token());
        ResponseCookie refreshCookie = tokenProvider.generateRefreshCookie(authOutput.refreshToken());

        // 2. Hand it off to the headers
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(authOutput.user());
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshSession(HttpServletRequest request) {
        // 1. Extract raw token from secure incoming cookie
        String currentRefreshToken = tokenProvider.getCookieValue(request, "jwt_refresh_token");

        if (currentRefreshToken == null || currentRefreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session token missing.");
        }

        // 2. Fire application orchestration logic
        jwtAuthOutput outputs = refreshTokenUseCase.rotateSessionTokens(currentRefreshToken);

        // 3. Convert outputs to fresh secure browser cookies
        ResponseCookie accessCookie = tokenProvider.generateAccessCookie(outputs.token());
        ResponseCookie refreshCookie = tokenProvider.generateRefreshCookie(outputs.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(outputs.user());
    }
}
