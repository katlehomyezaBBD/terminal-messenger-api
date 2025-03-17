package com.example.googleAuth.controllers;

import com.example.googleAuth.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.*;
import java.util.*;

@RestController
public class TokenController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtStore jwtStore;

    @GetMapping("/api/token")
    public ResponseEntity<Map<String, String>> getToken(Authentication authentication) {
        Map<String, String> response = new HashMap<>();

        if (authentication == null) {
            response.put("error", "Not authenticated");
            return ResponseEntity.status(401).body(response);
        }

        String username;
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            username = oauthToken.getPrincipal().getAttribute("email");
        } else {
            username = authentication.getName();
        }

        String jwt = jwtUtil.generateToken(username);
        // Store the token in the JwtStore
        jwtStore.storeToken(username, jwt);

        response.put("token", jwt);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestHeader("Authorization") String authHeader) {
        Map<String, String> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.put("error", "Invalid Authorization header format");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        String oldToken = authHeader.substring(7);
        String username = null;

        try {
            // Try to extract username from token, ignoring expiration
            username = jwtUtil.extractUsernameFromExpiredToken(oldToken);
        } catch (JwtException e) {
            // Token is malformed or tampered with
            response.put("error", "Invalid token: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            response.put("error", "Token processing error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        if (username == null) {
            response.put("error", "Could not extract username from token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Generate a new token with the username
        String newToken = jwtUtil.generateToken(username);
        response.put("token", newToken);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(Authentication authentication) {

        Map<String, String> response = new HashMap<>();

        if (authentication != null) {
            String username;
            if (authentication instanceof OAuth2AuthenticationToken) {
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                username = oauthToken.getPrincipal().getAttribute("email");
            } else {
                username = authentication.getName();
            }

            // Remove the token from storage
            jwtStore.invalidateToken(username);

            response.put("message", "Successfully logged out");
            return ResponseEntity.ok(response);
        }

        response.put("message", "No active session");
        return ResponseEntity.ok(response);
    }
}