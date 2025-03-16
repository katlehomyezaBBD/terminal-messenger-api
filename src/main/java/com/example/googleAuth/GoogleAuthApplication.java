package com.example.googleAuth;

import org.springframework.boot.SpringApplication;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import com.example.googleAuth.cli.Cli;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}) // Exclude database configuration
public class GoogleAuthApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(GoogleAuthApplication.class, args);

		// Get the CLI component and run the OAuth flow
		Cli cli = context.getBean(Cli.class);
		cli.runOAuthFlow();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}