package com.ghosttrace.guardian;

import java.util.List;

public record CommandAnalysis(
    String command,
    RiskLevel riskLevel,
    List<PatternRule> matchedRules,
    List<String> detectedOperators,
    boolean requiresApproval,
    boolean blocked,
    String explanation
) {
    public boolean isSafe() {
        return riskLevel == RiskLevel.LOW;
    }
}
