package com.ghosttrace.developer;

import java.util.List;

public record CodeAnalysisResult(
    String language,
    int lineCount,
    int complexityScore,
    List<String> potentialIssues,
    List<String> securityRisks,
    List<String> suggestions
) {
    public boolean hasIssues() {
        return !potentialIssues.isEmpty() || !securityRisks.isEmpty();
    }
}
