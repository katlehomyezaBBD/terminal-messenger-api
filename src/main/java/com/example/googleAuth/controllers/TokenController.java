package com.example.googleAuth.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.example.googleAuth.util.JwtUtil;

@RestController
public class TokenController {

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/refresh-token")
    public String refreshToken(@RequestHeader("Authorization") String oldToken) {
        String username = jwtUtil.extractUsername(oldToken);
        if (jwtUtil.validateToken(oldToken, username)) {
            return jwtUtil.generateToken(username);
        }
        return "Invalid token";
    }
}