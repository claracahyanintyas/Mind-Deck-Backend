package org.individualproject.flashcards.application.security.implementation;

import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.persistence.RoleRepository;
import org.individualproject.flashcards.application.persistence.UserRepository;
import org.individualproject.flashcards.application.security.DTO.jwtGuestOutput;
import org.individualproject.flashcards.application.security.GuestService;
import org.individualproject.flashcards.domain.role.Role;
import org.individualproject.flashcards.domain.user.User;
import org.individualproject.flashcards.security.JwtTokenProvider;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service @AllArgsConstructor
public class GuestServiceImpl implements GuestService {
    JwtTokenProvider jwtTokenProvider;
    private RoleRepository roleRepository;
    private UserRepository userRepository;
    @Override
    public jwtGuestOutput createGuestSession() {
        var username = "guest_" + UUID.randomUUID().toString().substring(0, 8);
        User guestUser = new User(username);
        Role guestRole = roleRepository.findByName("ROLE_GUEST")
                .orElseThrow(() -> new RuntimeException("Error: Role ROLE_GUEST is not found in the database."));
        guestUser.addRole(guestRole);
        userRepository.save(guestUser);

        // 2. Generate token
        String token = jwtTokenProvider.generateTokenFromUsername(guestUser.getUsername());
        return new jwtGuestOutput(token, username);
    }
}
