package org.individualproject.flashcards.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    // FIX: Define a consistent name for your cookie instead of using the secret
    private static final String COOKIE_NAME = "jwt_token";

    // Create the HttpOnly cookie
    public ResponseCookie generateJwtCookie(String token) {
        return ResponseCookie.from(COOKIE_NAME, token) // Use COOKIE_NAME here
                .path("/")
                .httpOnly(true)                    // Prevents JavaScript access (XSS protection)
                .secure(false)                     // Set to true in production (requires HTTPS)
                .sameSite("Lax")                   // Protects against CSRF
                .maxAge(jwtExpirationDate / 1000)  // Dynamically use your app properties expiration (in seconds)
                .build();
    }

    // Extract the token from the cookie instead of the Authorization Header
    public String getJwtFromCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, COOKIE_NAME); // Use COOKIE_NAME here
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    // Clean cookie for logout
    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(COOKIE_NAME, null).path("/").maxAge(0).build(); // Use COOKIE_NAME here
    }


    // generate JWT token (For normal login)
    public String generateToken(Authentication authentication){
        String username = authentication.getName();
        return generateTokenFromUsername(username);
    }

    // UPGRADE: Overload method so you can easily generate tokens for Guests using just a username string
    public String generateTokenFromUsername(String username) {
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .subject(username)
                .issuedAt(currentDate)
                .expiration(expireDate)
                .signWith(key())
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // get username from JWT token
    public String getUsername(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // validate JWT token
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parse(token);
            return true;
        } catch (Exception ex) {
            System.out.println("❌ Invalid JWT: " + ex.getMessage());
            return false;
        }
    }
}