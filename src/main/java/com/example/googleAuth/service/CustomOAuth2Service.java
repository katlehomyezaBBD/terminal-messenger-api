package com.example.googleAuth.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import com.example.googleAuth.repository.InMemoryUserStore;

@Service
public class CustomOAuth2Service extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("Loading OAuth2 User...");

        OAuth2User oauth2User = super.loadUser(userRequest);

        // Extract user details
        String email = oauth2User.getAttribute("email");
        String firstName = oauth2User.getAttribute("given_name");
        String lastName = oauth2User.getAttribute("family_name");

        System.out.println("User details - Email: " + email);
        System.out.println("First Name: " + firstName);
        System.out.println("Last Name: " + lastName);

        // Store user in memory instead of database
        if (email != null) {
            InMemoryUserStore.addUser(email, firstName, lastName);
            System.out.println("User stored in memory: " + email);
        } else {
            System.out.println("Email is null. User not stored in memory.");
        }

        return oauth2User;
    }
}