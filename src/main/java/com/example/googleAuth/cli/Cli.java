package com.example.googleAuth.cli;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.*;

@Component
public class Cli {

    @Autowired
    private RestTemplate restTemplate;

    private String jwtToken;

    public void runOAuthFlow() {
        try {
            System.out.println("Starting Google OAuth Authentication flow...");

            // Trigger the OAuth2 login flow
            System.out.println("Please open the following URL in your browser to log in with Google:");
            System.out.println("http://localhost:8080/oauth2/authorization/google");

            // Wait for the user to complete the login
            System.out.println("After logging in, return to this console and press Enter...");

            // Wait for user input
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();

            // Capture the JWT token from the file
            captureJwtToken();

            // Provide options to the user
            provideOptions();
        } catch (Exception e) {
            System.err.println("Error during authentication: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void captureJwtToken() {
        try {
            // Read the JWT token from the file
            File file = new File("jwt-token.txt");
            Scanner fileScanner = new Scanner(file);
            jwtToken = fileScanner.nextLine();
            fileScanner.close();

            System.out.println("Captured JWT: " + jwtToken);
        } catch (FileNotFoundException e) {
            System.err.println("JWT token file not found. Please log in first.");
        }
    }

    private void refreshToken() {
        try {
            // Set the JWT in the request header
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + jwtToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Make a POST request to the /refresh-token endpoint
            String url = "http://localhost:8080/refresh-token";
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            // Extract the new JWT token from the response body
            Map<String, String> body = response.getBody();
            if (body != null && body.containsKey("token")) {
                jwtToken = body.get("token");
                System.out.println("Refreshed JWT: " + jwtToken);

                // Save the new token to the file
                saveTokenToFile(jwtToken);
            } else {
                System.out.println("No new JWT token found in the response.");
            }
        } catch (Exception e) {
            System.err.println("Error refreshing JWT token: " + e.getMessage());
        }
    }

    private void saveTokenToFile(String jwt) {
        try (FileWriter writer = new FileWriter("jwt-token.txt")) {
            writer.write(jwt);
            System.out.println("JWT token saved to jwt-token.txt");
        } catch (IOException e) {
            System.err.println("Error saving JWT token to file: " + e.getMessage());
        }
    }

    private void provideOptions() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Print Hello World");
            System.out.println("2. Print 1 + 1");
            System.out.println("3. Exit");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    callHelloWorldEndpoint();
                    break;
                case 2:
                    callSumEndpoint();
                    break;
                case 3:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void callHelloWorldEndpoint() {
        if (jwtToken == null) {
            System.out.println("No JWT token available. Please log in first.");
            return;
        }

        try {
            // Set the JWT in the request header
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + jwtToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Make a GET request to the /compute/hello endpoint
            String url = "http://localhost:8080/compute/hello";
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            System.out.println("API Response: " + response.getBody());
        } catch (Exception e) {
            System.err.println("Error making API call: " + e.getMessage());

            // If the token is expired, refresh it and retry the request

                System.out.println("Token expired. Refreshing token...");
                refreshToken();
                callHelloWorldEndpoint(); // Retry the request with the new token

        }
    }

    private void callSumEndpoint() {
        if (jwtToken == null) {
            System.out.println("No JWT token available. Please log in first.");
            return;
        }

        try {
            // Set the JWT in the request header
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + jwtToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Make a GET request to the /compute/sum endpoint
            String url = "http://localhost:8080/compute/sum";
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            System.out.println("API Response: " + response.getBody());
        } catch (Exception e) {
            System.err.println("Error making API call: " + e.getMessage());

            // If the token is expired, refresh it and retry the request
            if (e.getMessage().contains("JWT expired")) {
                System.out.println("Token expired. Refreshing token...");
                refreshToken();
                callSumEndpoint(); // Retry the request with the new token
            }
        }
    }
}