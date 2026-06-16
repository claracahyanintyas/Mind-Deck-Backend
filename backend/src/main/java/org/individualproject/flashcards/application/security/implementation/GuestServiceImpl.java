package org.individualproject.flashcards.application.security.implementation;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.individualproject.flashcards.application.exception.UserNotFoundException;
import org.individualproject.flashcards.application.persistence.RoleRepository;
import org.individualproject.flashcards.application.persistence.UserRepository;
import org.individualproject.flashcards.application.security.DTO.jwtGuestOutput;
import org.individualproject.flashcards.application.security.GuestService;
import org.individualproject.flashcards.application.user.DTO.UserPublicData;
import org.individualproject.flashcards.application.user.mapper.UserDTOMapper;
import org.individualproject.flashcards.domain.role.Role;
import org.individualproject.flashcards.domain.user.User;
import org.individualproject.flashcards.infrastructure.security.CustomUserDetails;
import org.individualproject.flashcards.infrastructure.security.jwt.JwtTokenProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

        String refreshToken = UUID.randomUUID().toString();
        guestUser.updateRefreshToken(refreshToken, jwtTokenProvider.getRefreshExpirationMs());

        var savedUser = userRepository.save(guestUser);

        UserPublicData userPublicData = new UserPublicData(
                savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getCreatedAt(),
                savedUser.isActive(), savedUser.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
        );

        Set<GrantedAuthority> authorities = savedUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        CustomUserDetails userDetails = new CustomUserDetails(
                savedUser.getId(),
                savedUser.getUsername(),
                "",
                authorities
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        String accessToken = jwtTokenProvider.generateToken(authentication);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new jwtGuestOutput(accessToken, refreshToken, userPublicData);
    }

    @Override
    @Transactional
    public jwtGuestOutput refreshExistingGuestSession(String username) {
        // 1. Load the existing guest entity from the DB
        User existingGuest = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Guest session lost"));

        Set<GrantedAuthority> authorities = existingGuest.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        String refreshToken = UUID.randomUUID().toString();
        existingGuest.updateRefreshToken(refreshToken, jwtTokenProvider.getRefreshExpirationMs());
        var updatedGuest = userRepository.save(existingGuest);
        CustomUserDetails userDetails = new CustomUserDetails(
                existingGuest.getId(),
                existingGuest.getUsername(),
                "",
                authorities
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        String newAccessToken = jwtTokenProvider.generateToken(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new jwtGuestOutput(newAccessToken, refreshToken, UserDTOMapper.toDTO(existingGuest));
    }
}
