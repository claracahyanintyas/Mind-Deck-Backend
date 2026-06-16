package org.individualproject.flashcards.infrastructure.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        LoginInput loginInput = new LoginInput(loginRequest.usernameOrEmail(), loginRequest.password());
        jwtAuthOutput jwtAuthOutput = authService.login(loginInput);

        ResponseCookie jwtCookie = tokenProvider.generateJwtCookie(jwtAuthOutput.token());
        ResponseCookie refreshCookie = tokenProvider.generateRefreshCookie(jwtAuthOutput.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(jwtAuthOutput.user());
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser() {
        SecurityContextHolder.clearContext();

        ResponseCookie cleanCookie = tokenProvider.getCleanJwtCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
                .body("User logged out successfully and secure cookie was destroyed.");
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegisterRequest registerRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        RegisterCommand registerCommand = new RegisterCommand(registerRequest.username(), registerRequest.email(), registerRequest.password());
        jwtAuthOutput authOutput = registerUserUseCase.registerUser(registerCommand, auth.getName());

        ResponseCookie jwtCookie = tokenProvider.generateJwtCookie(authOutput.token());
        ResponseCookie refreshCookie = tokenProvider.generateRefreshCookie(authOutput.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(authOutput.user());
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshSession(HttpServletRequest request) {
        String currentRefreshToken = tokenProvider.getCookieValue(request, "jwt_refresh_token");

        if (currentRefreshToken == null || currentRefreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session token missing.");
        }

        jwtAuthOutput outputs = refreshTokenUseCase.rotateSessionTokens(currentRefreshToken);

        ResponseCookie accessCookie = tokenProvider.generateAccessCookie(outputs.token());
        ResponseCookie refreshCookie = tokenProvider.generateRefreshCookie(outputs.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(outputs.user());
    }
}
