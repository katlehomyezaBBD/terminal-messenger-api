package com.example.googleAuth.config.handlers;

import com.example.googleAuth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("OAuth2 Authentication Successful!");

        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String email = oauthToken.getPrincipal().getAttribute("email");
            System.out.println("User email: " + email);

            // Generate JWT
            String jwt = jwtUtil.generateToken(email);
            System.out.println("Generated JWT: " + jwt);

            // Set the JWT in the response header
            response.addHeader("Authorization", "Bearer " + jwt);



            // Redirect to the root endpoint
            response.sendRedirect("/");
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}