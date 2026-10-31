package com.ghosttrace.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghosttrace.ghost.PersonalityProfile;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class InferenceEngine {

    private final InferenceConfig config;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public InferenceEngine(InferenceConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();
    }

    public InferenceResult generate(
        PersonalityProfile profile,
        String systemPrompt,
        List<Map<String, String>> messages,
        String latestUserPrompt
    ) {
        long start = System.currentTimeMillis();

        // 1. Attempt local Ollama / OpenAI API call
        try {
            String endpoint = config.endpoint().replaceAll("/+$", "");
            String url = endpoint + "/api/chat"; // Ollama standard API

            Map<String, Object> payload = Map.of(
                "model", config.model(),
                "messages", messages,
                "stream", false,
                "options", Map.of(
                    "temperature", config.temperature(),
                    "top_p", config.topP(),
                    "num_predict", config.maxTokens()
                )
            );

            String requestBody = objectMapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(config.timeoutSeconds()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                String content = root.path("message").path("content").asText("");
                int promptTokens = root.path("prompt_eval_count").asInt(10);
                int completionTokens = root.path("eval_count").asInt(50);
                long duration = System.currentTimeMillis() - start;

                return new InferenceResult(
                    content,
                    config.model(),
                    promptTokens,
                    completionTokens,
                    duration,
                    false,
                    "stop"
                );
            }
        } catch (Exception ignored) {
            // Local server offline; fall through to Spectral Brain
        }

        // 2. Offline Fallback: Spectral Brain
        long duration = System.currentTimeMillis() - start;
        String spectralThought = SpectralBrain.think(latestUserPrompt, profile);
        int estPromptTokens = Math.max(1, latestUserPrompt.length() / 4);
        int estCompletionTokens = Math.max(1, spectralThought.length() / 4);

        return new InferenceResult(
            spectralThought,
            "SpectralBrain (Offline Fallback)",
            estPromptTokens,
            estCompletionTokens,
            duration,
            true,
            "stop"
        );
    }
}
