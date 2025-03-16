package com.example.googleAuth.controllers;

import com.example.googleAuth.util.JwtUtil;
import com.example.googleAuth.util.JwtStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
        // Extract token from "Bearer <token>"
        String oldToken = authHeader;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            oldToken = authHeader.substring(7);
        }

        try {
            // Skip expiration check for refresh token
            String username = jwtUtil.extractUsername(oldToken);
            String newToken = jwtUtil.generateToken(username); // Generate a new token
            response.put("token", newToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Token processing error: " + e.getMessage());
            return ResponseEntity.status(401).body(response);
        }
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