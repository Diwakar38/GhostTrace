package com.ghosttrace.ai;

import com.ghosttrace.config.Settings;
import com.ghosttrace.ghost.GhostEngine;
import com.ghosttrace.ghost.PersonalityProfile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AIEngine {

    private final Settings settings;
    private final GhostEngine ghostEngine;
    private final InferenceEngine inferenceEngine;
    private final List<Map<String, String>> conversationHistory = new ArrayList<>();

    public AIEngine(Settings settings, GhostEngine ghostEngine) {
        this.settings = settings;
        this.ghostEngine = ghostEngine;
        InferenceConfig config = new InferenceConfig(
            settings.getAiEndpoint(),
            settings.getAiModel(),
            settings.getAiMaxTokens(),
            settings.getAiTemperature(),
            0.90,
            settings.getGuardianTimeoutSeconds()
        );
        this.inferenceEngine = new InferenceEngine(config);
    }

    public InferenceResult ask(String userPrompt, String projectContext) {
        PersonalityProfile profile = ghostEngine.getProfile();

        StringBuilder systemPrompt = new StringBuilder();
        systemPrompt.append(profile.identityPrompt()).append("\n\n");
        systemPrompt.append("Speaking Style: ").append(profile.speakingStyle()).append("\n");
        if (projectContext != null && !projectContext.isBlank()) {
            systemPrompt.append("\nProject Context:\n").append(projectContext).append("\n");
        }

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt.toString()));

        synchronized (conversationHistory) {
            // Keep last 10 turns to avoid context overflow
            int start = Math.max(0, conversationHistory.size() - 10);
            for (int i = start; i < conversationHistory.size(); i++) {
                messages.add(conversationHistory.get(i));
            }
            messages.add(Map.of("role", "user", "content", userPrompt));
        }

        InferenceResult result = inferenceEngine.generate(profile, systemPrompt.toString(), messages, userPrompt);

        // Decorate response through GhostEngine atmospheric effects
        String decorated = ghostEngine.decorateResponse(result.content());

        InferenceResult finalResult = new InferenceResult(
            decorated,
            result.modelName(),
            result.promptTokens(),
            result.completionTokens(),
            result.durationMs(),
            result.isOfflineFallback(),
            result.finishReason()
        );

        synchronized (conversationHistory) {
            conversationHistory.add(Map.of("role", "user", "content", userPrompt));
            conversationHistory.add(Map.of("role", "assistant", "content", finalResult.content()));
        }

        return finalResult;
    }

    public void clearHistory() {
        synchronized (conversationHistory) {
            conversationHistory.clear();
        }
    }

    public int getHistoryTurnCount() {
        synchronized (conversationHistory) {
            return conversationHistory.size() / 2;
        }
    }
}
