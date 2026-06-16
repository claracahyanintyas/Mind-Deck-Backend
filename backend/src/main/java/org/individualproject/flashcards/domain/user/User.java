package org.individualproject.flashcards.domain.user;

import lombok.Builder;
import lombok.Getter;
import org.individualproject.flashcards.domain.role.Role;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter @Builder
public class User {
    Long id;
    String username;
    String email;
    String password;
    @Builder.Default
    private OffsetDateTime createdAt =  OffsetDateTime.now();
    private boolean active;
    private Set<Role> roles =  new HashSet<>();

    // refresh tokens
    private String refreshToken;
    private Instant refreshTokenExpiryDate;

    //register as guest
    public void setUserCredentials(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
    //register new user
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.active = true;
        this.createdAt = OffsetDateTime.now();
    }

    public User(Long id, String username, String email, String password, OffsetDateTime createdAt, boolean active, Set<Role> roles, String refreshToken, Instant refreshTokenExpiryDate) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.active = active;
        this.roles = roles;
        this.refreshToken = refreshToken;
        this.refreshTokenExpiryDate = refreshTokenExpiryDate;
    }
    //guest user
    public User(String username) {
        this.username = username;
        this.createdAt = OffsetDateTime.now();
        this.active = true;
    }
    public void addRole(Role role) {
        this.roles.add(role);
    }
    public void removeRole(String role) {
        this.roles.removeIf(r -> r.getName().equals(role));
    }


    // Core Business Rule (Invariants)
    public void updateRefreshToken(String token, long durationMs) {
        this.refreshToken = token;
        this.refreshTokenExpiryDate = Instant.now().plusMillis(durationMs);
    }

    public boolean isRefreshTokenExpired() {
        return this.refreshTokenExpiryDate != null && this.refreshTokenExpiryDate.isBefore(Instant.now());
    }

    public void clearRefreshToken() {
        this.refreshToken = null;
        this.refreshTokenExpiryDate = null;
    }

}
