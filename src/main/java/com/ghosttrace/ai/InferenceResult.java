package com.ghosttrace.ai;

public record InferenceResult(
    String content,
    String modelName,
    int promptTokens,
    int completionTokens,
    long durationMs,
    boolean isOfflineFallback,
    String finishReason
) {
    public int getTotalTokens() {
        return promptTokens + completionTokens;
    }
}
