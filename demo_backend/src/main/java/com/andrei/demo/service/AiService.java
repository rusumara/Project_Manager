package com.andrei.demo.service;

import com.andrei.demo.model.ChatResponse;
import com.andrei.demo.model.PredictionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiService {

    private final RestTemplate restTemplate;

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    public PredictionResponse predict(String personName, List<String> skills) {
        try {
            Map<String, Object> requestBody = Map.of("skills", skills);
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(
                    aiServiceUrl + "/predict", requestBody, Map.class);
            if (response == null) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Prediction service unavailable");
            }
            String projectType = (String) response.get("projectType");
            double confidence = ((Number) response.get("confidence")).doubleValue();
            return new PredictionResponse(personName, skills, projectType, confidence);
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Prediction service unavailable");
        }
    }

    public ChatResponse chat(String message, List<Object> people) {
        try {
            Map<String, Object> requestBody = Map.of("message", message, "people", people);
            ChatResponse response = restTemplate.postForObject(
                    aiServiceUrl + "/chat", requestBody, ChatResponse.class);
            if (response == null) {
                return new ChatResponse("AI service is currently unavailable. Please try again later.");
            }
            return response;
        } catch (RestClientException e) {
            return new ChatResponse("AI service is currently unavailable. Please try again later.");
        }
    }
}
