package com.example.googleAuth.controllers;

import com.example.googleAuth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.*;
import java.util.*;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class HomeController {

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/")
    public String home(HttpServletResponse response) {
        // Get the authenticated user from the SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName(); // This will be the email (subject of the JWT)

            // Generate JWT
            String jwt = jwtUtil.generateToken(username);

            // Set the JWT in the response header
            response.addHeader("Authorization", "Bearer " + jwt);

            return "Authentication successful! User: " + username;
        } else {
            return "Not authenticated. Please log in.";
        }
    }

    @GetMapping("/compute/hello")
    public String computeHello() {
        return "Hello World";
    }

    @GetMapping("/compute/sum")
    public String computeSum() {
        int result = 1 + 1;
        return "1 + 1 = " + result;
    }
}