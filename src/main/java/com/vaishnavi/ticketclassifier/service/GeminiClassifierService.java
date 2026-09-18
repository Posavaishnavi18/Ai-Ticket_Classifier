package com.vaishnavi.ticketclassifier.service;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class GeminiClassifierService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = JsonMapper.builder().build();

    public String[] classify(String subject, String description) {
        try {
            String prompt = "You are a support ticket classifier. Respond with ONLY a JSON object "
                    + "like {\"category\":\"...\",\"urgency\":\"...\"}. "
                    + "Category must be one of: Billing, Technical, Account, General. "
                    + "Urgency must be one of: Low, Medium, High. "
                    + "Subject: " + subject + " Description: " + description;

            String requestBody = "{\"contents\":[{\"parts\":[{\"text\":"
                    + mapper.writeValueAsString(prompt) + "}]}]}";

               String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key="
            	        + apiKey;
             System.out.println("Gemini API key loaded: " + (apiKey != null && !apiKey.isBlank()));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("x-google-api-key", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println("Gemini HTTP Status: " + response.statusCode());
            System.out.println("Gemini Response: " + response.body());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Gemini API error: " + response.body());
            }


            JsonNode root = mapper.readTree(response.body());
            String text = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asString();
            text = text.replace("```json", "").replace("```", "").trim();

            JsonNode result = mapper.readTree(text);
            String category = result.path("category").asString("General");
            String urgency = result.path("urgency").asString("Medium");

            return new String[] { category, urgency };

        } catch (Exception e) {
            e.printStackTrace();
            return new String[] { "General", "Medium" };
        }
    }
}
