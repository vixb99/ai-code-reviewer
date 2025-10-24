package com.vix.ai.control;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class OpenAiService {

    @ConfigProperty(name = "openai.api.key")
    String apiKey;

    public String analyzeCode(String diff) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();

            String body = mapper.writeValueAsString(Map.of(
                    "model", "gpt-4o-mini",
                    "messages", new Object[]{
                            Map.of("role", "system", "content", "You are a senior code reviewer."),
                            Map.of("role", "user", "content", "Review this code diff:\n" + diff)
                    }
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            var json = mapper.readTree(response.body());
            return json.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
