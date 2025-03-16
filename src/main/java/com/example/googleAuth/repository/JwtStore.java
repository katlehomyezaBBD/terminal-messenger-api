package com.example.googleAuth.util;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server-side storage for JWT tokens
 * This class provides methods to store, retrieve, and invalidate JWT tokens
 */
@Component
public class JwtStore {

    // Using ConcurrentHashMap for thread safety
    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();

    /**
     * Store a JWT token for a user
     * @param username The user identifier (email or username)
     * @param token The JWT token
     */
    public void storeToken(String username, String token) {
        tokenStore.put(username, token);
    }

    /**
     * Retrieve a JWT token for a user
     * @param username The user identifier
     * @return The stored JWT token or null if not found
     */
    public String getToken(String username) {
        return tokenStore.get(username);
    }

    /**
     * Check if a token exists and matches the stored token for a user
     * @param username The user identifier
     * @param token The token to validate
     * @return true if the token matches the stored token, false otherwise
     */
    public boolean validateToken(String username, String token) {
        String storedToken = tokenStore.get(username);
        return storedToken != null && storedToken.equals(token);
    }

    /**
     * Invalidate a user's token
     * @param username The user identifier
     */
    public void invalidateToken(String username) {
        tokenStore.remove(username);
    }

    /**
     * Clear all stored tokens
     */
    public void clearAllTokens() {
        tokenStore.clear();
    }
}