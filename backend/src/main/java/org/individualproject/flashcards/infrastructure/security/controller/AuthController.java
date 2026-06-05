package org.individualproject.flashcards.infrastructure.security.controller;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.security.AuthService;
import org.individualproject.flashcards.application.security.DTO.LoginInput;
import org.individualproject.flashcards.application.security.DTO.RegisterCommand;
import org.individualproject.flashcards.application.security.DTO.jwtAuthOutput;
import org.individualproject.flashcards.application.security.DTO.jwtGuestOutput;
import org.individualproject.flashcards.application.security.GuestService;
import org.individualproject.flashcards.application.security.RegisterUserUseCase;
import org.individualproject.flashcards.infrastructure.security.DTO.LoginRequest;
import org.individualproject.flashcards.infrastructure.security.DTO.RegisterRequest;
import org.individualproject.flashcards.security.JwtTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private JwtTokenProvider tokenProvider;

    private AuthService authService;
    private GuestService guestService;
    private RegisterUserUseCase registerUserUseCase;

    @PostMapping("/guest")
    public ResponseEntity<?> createGuestSession() {
        jwtGuestOutput jwtGuestOutput = guestService.createGuestSession();

        ResponseCookie jwtCookie = tokenProvider.generateJwtCookie(jwtGuestOutput.token());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body("Guest session initialized as: " + jwtGuestOutput.username());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        LoginInput loginInput = new LoginInput(loginRequest.usernameOrEmail(), loginRequest.password());
        jwtAuthOutput jwtAuthOutput = authService.login(loginInput);

        // 1. Extract the raw token string and turn it into a secure HttpOnly cookie object
        ResponseCookie jwtCookie = tokenProvider.generateJwtCookie(jwtAuthOutput.token());

        // 2. Attach it to the HTTP headers. Return only the public profile metadata to React
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
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

        // 2. Hand it off to the headers
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(authOutput.user());
    }
}
