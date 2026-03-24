package com.example.StudentManagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Provide a WebClient.Builder bean
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    // Provide WebClient using the builder
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }
}