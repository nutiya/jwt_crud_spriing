package com.example.StudentManagement.service;

import com.example.StudentManagement.exception.ExternalServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class ThirdPartyService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    //http
    public ThirdPartyService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl("https://fakerapi.it/api/v1/persons").build();
        this.objectMapper = objectMapper;
    }

    // Get a dynamic list of users
    public List<Map<String, Object>> getRandomUsers(int quantity) {
        try {
            String jsonString = webClient.get()
                    .uri(uriBuilder -> uriBuilder.queryParam("_quantity", quantity).build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            Map<String, Object> map = objectMapper.readValue(jsonString, Map.class);
            return (List<Map<String, Object>>) map.get("data");
        } catch (Exception e) {
            throw new ExternalServiceException("Failed to fetch data from third-party API", e);
        }
    }
}