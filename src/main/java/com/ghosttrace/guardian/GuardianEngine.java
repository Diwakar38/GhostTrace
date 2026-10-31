package com.ghosttrace.guardian;

import com.ghosttrace.config.Settings;
import java.util.ArrayList;
import java.util.List;

public class GuardianEngine {

    private final Settings settings;
    private final ApprovalTokenManager tokenManager = new ApprovalTokenManager();
    private final GuardianExecutor executor = new GuardianExecutor();
    private final List<CommandAnalysis> history = new ArrayList<>();

    public GuardianEngine(Settings settings) {
        this.settings = settings;
    }

    public CommandAnalysis analyze(String command) {
        if (command == null || command.isBlank()) {
            return new CommandAnalysis("", RiskLevel.LOW, List.of(), List.of(), false, false, "Empty command.");
        }

        String trimmed = command.trim();
        List<String> detectedOperators = findShellOperators(trimmed);
        List<PatternRule> matched = new ArrayList<>();

        for (PatternRule rule : PatternRule.DEFAULT_RULES) {
            if (rule.pattern().matcher(trimmed).find()) {
                matched.add(rule);
            }
        }

        // Determine highest risk level
        RiskLevel highest = RiskLevel.LOW;
        if (!detectedOperators.isEmpty()) {
            highest = RiskLevel.MEDIUM; // Chained execution has inherent medium risk
        }
        for (PatternRule rule : matched) {
            if (rule.riskLevel().getSeverity() > highest.getSeverity()) {
                highest = rule.riskLevel();
            }
        }

        boolean requiresApproval = highest == RiskLevel.HIGH || highest == RiskLevel.CRITICAL;
        boolean blocked = false;
        if (settings.isGuardianEnabled() && settings.isGuardianBlockHighRisk()) {
            if (highest == RiskLevel.CRITICAL) {
                blocked = true;
            }
        }

        StringBuilder explanation = new StringBuilder();
        if (matched.isEmpty() && detectedOperators.isEmpty()) {
            explanation.append("Command contains standard operational patterns. Low risk.");
        } else {
            if (!matched.isEmpty()) {
                explanation.append("Matched ").append(matched.size()).append(" safety rule(s):\n");
                for (PatternRule r : matched) {
                    explanation.append("  • [").append(r.ruleId()).append("] ").append(r.name())
                               .append(" (").append(r.riskLevel().getLabel()).append("): ")
                               .append(r.reason()).append("\n");
                    if (r.saferAlternative() != null) {
                        explanation.append("    Alternative: ").append(r.saferAlternative()).append("\n");
                    }
                }
            }
            if (!detectedOperators.isEmpty()) {
                explanation.append("Detected shell control operators: ").append(String.join(", ", detectedOperators)).append("\n");
            }
        }

        CommandAnalysis analysis = new CommandAnalysis(
            trimmed,
            highest,
            matched,
            detectedOperators,
            requiresApproval,
            blocked,
            explanation.toString().trim()
        );

        synchronized (history) {
            history.add(analysis);
        }
        return analysis;
    }

    public String requestApprovalToken(String command) {
        return tokenManager.generateToken(command, 60); // 60s validity
    }

    public boolean validateToken(String token, String command) {
        return tokenManager.validateToken(token, command);
    }

    public GuardianExecutor.ExecutionResult executeSafe(String command, String approvalToken) {
        CommandAnalysis analysis = analyze(command);

        if (analysis.blocked()) {
            return new GuardianExecutor.ExecutionResult(
                -1, "", "Guardian Error: Critical command blocked by safety policy. Execution rejected.", 0, false
            );
        }

        if (analysis.requiresApproval()) {
            if (approvalToken == null || !validateToken(approvalToken, command)) {
                return new GuardianExecutor.ExecutionResult(
                    -1, "", "Guardian Error: Command requires cryptographic approval token. Request with 'guard approve <token>'.", 0, false
                );
            }
        }

        return executor.execute(command, settings.getGuardianTimeoutSeconds());
    }

    public List<CommandAnalysis> getHistory() {
        synchronized (history) {
            return new ArrayList<>(history);
        }
    }

    public void clearHistory() {
        synchronized (history) {
            history.clear();
        }
    }

    private List<String> findShellOperators(String command) {
        List<String> found = new ArrayList<>();
        String[] operators = { "&&", "||", "|&", ">>", ";", "|", ">", "<", "&" };
        for (String op : operators) {
            if (command.contains(op)) {
                found.add(op);
            }
        }
        return found;
    }
}
