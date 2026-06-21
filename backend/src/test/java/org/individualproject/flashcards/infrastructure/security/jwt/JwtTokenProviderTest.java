package org.individualproject.flashcards.infrastructure.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    // Must be at least 32 characters long to satisfy JJWT security requirements
    private final String testSecret = "my_ultra_secure_secret_key_32_characters_long!!";
    private final long accessExpirationMs = 60000; // 60 seconds
    private final long refreshExpirationMs = 120000; // 120 seconds

    @BeforeEach
    void setUp() {
        // Manually inject the private @Value properties into the utility component
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationDate", accessExpirationMs);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshExpirationMs", refreshExpirationMs);
    }

    @Test
    void generateTokenFromUsername_ShouldCreateValidParsableToken() {
        // Arrange
        String username = "test_user_99";

        // Act
        String token = jwtTokenProvider.generateTokenFromUsername(username);

        // Assert
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(username, jwtTokenProvider.getUsername(token));
    }

    @Test
    void generateToken_ShouldExtractNameFromAuthentication() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("auth_user");

        // Act
        String token = jwtTokenProvider.generateToken(authentication);

        // Assert
        assertNotNull(token);
        assertEquals("auth_user", jwtTokenProvider.getUsername(token));
    }

    @Test
    void validateToken_WithInvalidOrTamperedToken_ShouldReturnFalse() {
        // Act & Assert
        assertFalse(jwtTokenProvider.validateToken("invalid.garbage.token"));
    }

    @Test
    void generateJwtCookie_ShouldBuildCookieWithCorrectMetadata() {
        // Arrange
        String mockToken = "eyJhbGciOiJIUzI1NiJ9.testToken";

        // Act
        ResponseCookie cookie = jwtTokenProvider.generateJwtCookie(mockToken);

        // Assert
        assertNotNull(cookie);
        assertEquals("accessToken", cookie.getName());
        assertEquals(mockToken, cookie.getValue());
        assertEquals("/", cookie.getPath());
        assertTrue(cookie.isHttpOnly());
        assertEquals(accessExpirationMs / 1000, cookie.getMaxAge().getSeconds());
    }

    @Test
    void generateRefreshCookie_ShouldBuildCookieWithCorrectMetadata() {
        // Arrange
        String mockRefreshToken = "refresh-uuid-token";

        // Act
        ResponseCookie cookie = jwtTokenProvider.generateRefreshCookie(mockRefreshToken);

        // Assert
        assertNotNull(cookie);
        assertEquals("refreshToken", cookie.getName());
        assertEquals(mockRefreshToken, cookie.getValue());
        assertEquals("/", cookie.getPath());
        assertTrue(cookie.isHttpOnly());
        assertEquals(refreshExpirationMs / 1000, cookie.getMaxAge().getSeconds());
    }

    @Test
    void getJwtFromCookie_WithValidCookie_ShouldExtractValue() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie targetCookie = new Cookie("accessToken", "extracted-jwt-string");
        when(request.getCookies()).thenReturn(new Cookie[]{targetCookie});

        // Act
        String result = jwtTokenProvider.getJwtFromCookie(request);

        // Assert
        assertEquals("extracted-jwt-string", result);
    }

    @Test
    void getJwtFromCookie_WhenNoCookiesExist_ShouldReturnNull() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(null);

        // Act
        String result = jwtTokenProvider.getJwtFromCookie(request);

        // Assert
        assertNull(result);
    }
}