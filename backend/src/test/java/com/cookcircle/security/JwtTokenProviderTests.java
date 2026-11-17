package com.cookcircle.security;

import com.cookcircle.entity.Role;
import com.cookcircle.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class JwtTokenProviderTests {

    private JwtTokenProvider tokenProvider;
    private final String secret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final long expirationMs = 86400000; // 24 hours

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider(secret, expirationMs);
    }

    @Test
    @DisplayName("TC-01: Generate JWT token and verify subject and claims")
    void testTokenGenerationAndClaims() {
        Role role = new Role("ROLE_RESIDENT", "Resident role");
        User user = new User();
        user.setId(101L);
        user.setEmail("aarav.sharma@example.com");
        user.setFullName("Aarav Sharma");
        user.setPasswordHash("hashed_pw");
        user.setRole(role);

        UserPrincipal principal = UserPrincipal.create(user, 1L);
        String token = tokenProvider.generateToken(principal);

        assertNotNull(token);
        assertTrue(token.length() > 20);
        assertTrue(tokenProvider.validateToken(token));
        assertEquals("aarav.sharma@example.com", tokenProvider.getUsernameFromToken(token));

        var claims = tokenProvider.getClaimsFromToken(token);
        assertEquals("ROLE_RESIDENT", claims.get("role"));
        assertEquals("Aarav Sharma", claims.get("fullName"));
        assertEquals(1, ((Number) claims.get("societyId")).intValue());
    }

    @Test
    @DisplayName("TC-02: Reject invalid or tampered JWT token")
    void testTamperedTokenRejection() {
        String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalidpayload.invalidsignature";
        assertFalse(tokenProvider.validateToken(invalidToken));
        assertFalse(tokenProvider.validateToken(""));
        assertFalse(tokenProvider.validateToken(null));
    }

    @Test
    @DisplayName("TC-03: BCrypt password hashing and matching verification")
    void testBCryptPasswordHashing() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "Password@123";
        String encoded = encoder.encode(rawPassword);

        assertNotNull(encoded);
        assertNotEquals(rawPassword, encoded);
        assertTrue(encoder.matches(rawPassword, encoded));
        assertFalse(encoder.matches("WrongPassword", encoded));
    }

    @Test
    @DisplayName("TC-04: UserPrincipal authority mapping across 4 roles")
    void testUserPrincipalRoles() {
        String[] roleNames = {"ROLE_SUPER_ADMIN", "ROLE_SOCIETY_ADMIN", "ROLE_RESIDENT", "ROLE_COOK"};

        for (int i = 0; i < roleNames.length; i++) {
            Role role = new Role(roleNames[i], "Description");
            User user = new User();
            user.setId((long) (i + 1));
            user.setEmail("user" + i + "@cookcircle.com");
            user.setFullName("User " + i);
            user.setPasswordHash("hash");
            user.setRole(role);

            UserPrincipal principal = UserPrincipal.create(user, (long) (i + 1));
            Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

            assertEquals(1, authorities.size());
            assertEquals(roleNames[i], authorities.iterator().next().getAuthority());
        }
    }
}

