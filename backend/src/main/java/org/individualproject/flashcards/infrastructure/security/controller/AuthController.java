package org.individualproject.flashcards.infrastructure.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.exception.UserNotFoundException;
import org.individualproject.flashcards.application.security.AuthService;
import org.individualproject.flashcards.application.security.DTO.LoginInput;
import org.individualproject.flashcards.application.security.DTO.RegisterCommand;
import org.individualproject.flashcards.application.security.DTO.jwtAuthOutput;
import org.individualproject.flashcards.application.security.DTO.jwtGuestOutput;
import org.individualproject.flashcards.application.security.GuestService;
import org.individualproject.flashcards.application.security.RefreshTokenUseCase;
import org.individualproject.flashcards.application.security.RegisterUserUseCase;
import org.individualproject.flashcards.infrastructure.security.CustomUserDetails;
import org.individualproject.flashcards.infrastructure.security.DTO.LoginRequest;
import org.individualproject.flashcards.infrastructure.security.DTO.RegisterRequest;
import org.individualproject.flashcards.infrastructure.security.jwt.JwtTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<?> createGuestSession(
            @CookieValue(name = "refreshToken", required = false) String existingRefreshToken
    ) {
        jwtGuestOutput jwtGuestOutput;

        if (existingRefreshToken != null && tokenProvider.validateToken(existingRefreshToken)) {
            String username = tokenProvider.getUsername(existingRefreshToken);

            if (username.startsWith("guest")) {
                try {
                    jwtGuestOutput = guestService.refreshExistingGuestSession(username);
                } catch (UserNotFoundException e) {
                    // Fallback if the database was wiped or the guest row expired
                    jwtGuestOutput = guestService.createGuestSession();
                }
            } else {
                jwtGuestOutput = guestService.createGuestSession();
            }
        } else {
            jwtGuestOutput = guestService.createGuestSession();
        }

        ResponseCookie jwtCookie = tokenProvider.generateJwtCookie(jwtGuestOutput.token());
        ResponseCookie refreshCookie = tokenProvider.generateRefreshCookie(jwtGuestOutput.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(jwtGuestOutput.user());
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

        ResponseCookie cleanJwtCookie = tokenProvider.getCleanJwtCookie();
        ResponseCookie cleanRefreshCookie = tokenProvider.getCleanRefreshCookie(); // Assumes this utility exists to maxAge(0) the refresh token

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanJwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, cleanRefreshCookie.toString())
                .body("User logged out successfully and secure cookies were destroyed.");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterCommand command
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 1. Try to extract the existing guest's username from the SecurityContext
        String guestUsername = null;
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                boolean isGuest = userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_GUEST"));
                if (isGuest) {
                    guestUsername = userDetails.getUsername();
                }
            }
        }

        jwtAuthOutput output = registerUserUseCase.registerUser(command, guestUsername);

        ResponseCookie jwtCookie = tokenProvider.generateJwtCookie(output.token());
        ResponseCookie refreshCookie = tokenProvider.generateRefreshCookie(output.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(output.user()); // Return just the UserPublicData to the frontend
    }
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshSession(HttpServletRequest request) {
        // FIX: Look for "refreshToken" to match your JwtTokenProvider's static name!
        String currentRefreshToken = tokenProvider.getCookieValue(request, "refreshToken");

        if (currentRefreshToken == null || currentRefreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session token missing.");
        }

        jwtAuthOutput outputs = refreshTokenUseCase.rotateSessionTokens(currentRefreshToken);

        ResponseCookie accessCookie = tokenProvider.generateJwtCookie(outputs.token());
        ResponseCookie refreshCookie = tokenProvider.generateRefreshCookie(outputs.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(outputs.user());
    }
}
