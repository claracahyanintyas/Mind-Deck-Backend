package org.individualproject.flashcards.infrastructure.user.controller;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.user.GetUserByUsernameUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private GetUserByUsernameUseCase getUserByUsernameUseCase;
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        // Find the current guest authentication string from the cookie context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        var user = getUserByUsernameUseCase.getUser(auth.getName());

        return ResponseEntity.ok()
                .body(user);
    }
}
