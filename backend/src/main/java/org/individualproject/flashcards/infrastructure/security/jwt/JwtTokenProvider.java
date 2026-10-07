package org.individualproject.flashcards.infrastructure.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
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

    @Getter @Value("${app.refresh-expiration-ms}")
    private long refreshExpirationMs;

    private static final String ACCESS_COOKIE_NAME = "accessToken";
    private static final String REFRESH_COOKIE_NAME = "refreshToken";

    @Value("${app.cookie.secure:false}")
    private boolean COOKIE_SECURE;

    @Value("${app.cookie.same-site:Lax}")
    private String COOKIE_SAME_SITE;

    // 1. Unified Access Token Generator (Always uses path "/")
    public ResponseCookie generateJwtCookie(String token) {
        return ResponseCookie.from(ACCESS_COOKIE_NAME, token)
                .path("/")
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .sameSite(COOKIE_SAME_SITE)
                .maxAge(jwtExpirationDate / 1000)
                .build();
    }

    // 2. Unified Refresh Token Generator (Always uses path "/api/auth")
    public ResponseCookie generateRefreshCookie(String refreshTokenStr) {
        return ResponseCookie.from(REFRESH_COOKIE_NAME, refreshTokenStr)
                .path("/")
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .sameSite(COOKIE_SAME_SITE)
                .maxAge(refreshExpirationMs / 1000)
                .build();
    }

    // 3. Clean access cookie (Matches creation attributes perfectly)
    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(ACCESS_COOKIE_NAME, null)
                .path("/")
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .sameSite(COOKIE_SAME_SITE)
                .maxAge(0)
                .build();
    }

    // 4. Clean refresh cookie (Matches creation attributes perfectly)
    public ResponseCookie getCleanRefreshCookie() {
        return ResponseCookie.from(REFRESH_COOKIE_NAME, null)
                .path("/")
                .httpOnly(true)
                .secure(COOKIE_SECURE)
                .sameSite(COOKIE_SAME_SITE)
                .maxAge(0)
                .build();
    }

    public String getJwtFromCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, ACCESS_COOKIE_NAME);
        return (cookie != null) ? cookie.getValue() : null;
    }

    public String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        return (cookie != null) ? cookie.getValue() : null;
    }

    public String generateToken(Authentication authentication){
        return generateTokenFromUsername(authentication.getName());
    }

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

    public String getUsername(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

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