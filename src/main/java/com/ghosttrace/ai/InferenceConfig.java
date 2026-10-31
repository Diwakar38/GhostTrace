package com.ghosttrace.ai;

public record InferenceConfig(
    String endpoint,
    String model,
    int maxTokens,
    double temperature,
    double topP,
    int timeoutSeconds
) {
    public static InferenceConfig defaultLocal() {
        return new InferenceConfig("http://localhost:11434", "qwen2.5:1.5b", 512, 0.70, 0.90, 60);
    }
}
