package com.example.googleAuth.repository;

import java.util.*;
import com.example.googleAuth.util.JwtUtil;
import com.example.googleAuth.entity.User;



public class InMemoryUserStore {
    private static final Map<String, User> users = new HashMap<>();
    private static boolean authenticationComplete = false;

    public static void addUser(String email, String firstName, String lastName) {
        User user = new User();
        user.setEmail(email);
        user.setFirstname(firstName);
        user.setLastname(lastName);
        users.put(email, user);
    }

    public static User getUser(String email) {
        return users.get(email);
    }

    public static Map<String, User> getAllUsers() {
        return users;
    }

    public static void setAuthenticationComplete(boolean complete) {
        authenticationComplete = complete;
    }

    public static boolean isAuthenticationComplete() {
        return authenticationComplete;
    }
}