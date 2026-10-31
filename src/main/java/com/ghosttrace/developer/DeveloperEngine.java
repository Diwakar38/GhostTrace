package com.ghosttrace.developer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DeveloperEngine {

    private static final Pattern JAVA_PATTERN = Pattern.compile("(?m)^\\s*(public|private|protected)?\\s*(class|interface|enum|record)\\s+\\w+");
    private static final Pattern PYTHON_PATTERN = Pattern.compile("(?m)^\\s*def\\s+\\w+\\s*\\(|import\\s+\\w+|from\\s+\\w+\\s+import");
    private static final Pattern JS_PATTERN = Pattern.compile("(?m)const\\s+\\w+\\s*=|let\\s+\\w+\\s*=|function\\s+\\w*\\(|=>\\s*\\{");
    private static final Pattern GO_PATTERN = Pattern.compile("(?m)package\\s+\\w+|func\\s+(\\(\\w+\\s+\\*?\\w+\\)\\s+)?\\w+\\(");
    private static final Pattern RUST_PATTERN = Pattern.compile("(?m)fn\\s+\\w+\\s*\\(|let\\s+mut\\s+|impl\\s+\\w+");
    private static final Pattern SHELL_PATTERN = Pattern.compile("(?m)^#!/bin/(bash|sh)|echo\\s+|export\\s+\\w+=");

    public String detectLanguage(String code) {
        if (code == null || code.isBlank()) return "Unknown";
        if (JAVA_PATTERN.matcher(code).find()) return "Java";
        if (PYTHON_PATTERN.matcher(code).find()) return "Python";
        if (RUST_PATTERN.matcher(code).find()) return "Rust";
        if (GO_PATTERN.matcher(code).find()) return "Go";
        if (JS_PATTERN.matcher(code).find()) return "JavaScript/TypeScript";
        if (SHELL_PATTERN.matcher(code).find()) return "Shell";
        return "Generic";
    }

    public CodeAnalysisResult analyzeCode(String code) {
        if (code == null || code.isBlank()) {
            return new CodeAnalysisResult("Unknown", 0, 0, List.of(), List.of(), List.of("No code provided for analysis."));
        }

        String lang = detectLanguage(code);
        String[] lines = code.split("\r?\n");
        int lineCount = lines.length;

        List<String> issues = new ArrayList<>();
        List<String> securityRisks = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        int complexity = 1;

        // Static heuristics
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            // Complexity indicators
            if (line.matches(".*\\b(if|else if|for|while|case|catch)\\b.*")) {
                complexity++;
            }

            // Empty catch blocks / Exception swallowing
            if (line.matches(".*catch\\s*\\([^)]+\\)\\s*\\{\\s*\\}.*") ||
                (line.contains("catch") && i + 1 < lines.length && lines[i + 1].trim().equals("}"))) {
                issues.add("Line " + (i + 1) + ": Swallowed exception in empty catch block. Logs or recovery are required.");
            }

            // Hardcoded credentials
            if (line.matches("(?i).*\\b(password|secret|api_key|apikey|token)\\b\\s*[:=]\\s*(\"|')[^\"']{4,}(\"|').*")) {
                securityRisks.add("Line " + (i + 1) + ": Potential hardcoded credential or secret detected.");
            }

            // Raw SQL string concatenation
            if (line.matches("(?i).*select\\s+.*from\\s+.*\\+.*") || line.matches("(?i).*executequery\\s*\\(.*\\+.*\\).*")) {
                securityRisks.add("Line " + (i + 1) + ": Possible SQL injection through string concatenation. Use parameterized queries.");
            }

            // Java specific: unclosed streams / printStackTrace
            if ("Java".equals(lang)) {
                if (line.contains(".printStackTrace()")) {
                    issues.add("Line " + (i + 1) + ": Uses printStackTrace() instead of a structured logger.");
                }
                if (line.matches(".*new\\s+(FileInputStream|FileOutputStream|FileReader|FileWriter)\\b.*") && !line.contains("try")) {
                    suggestions.add("Line " + (i + 1) + ": Ensure stream resource is wrapped in try-with-resources.");
                }
            }
        }

        if (complexity > 15) {
            suggestions.add("High cyclomatic complexity (" + complexity + "). Consider decomposing into smaller subroutines.");
        }
        if (lineCount > 100) {
            suggestions.add("File exceeds 100 lines. Review modular separation.");
        }

        return new CodeAnalysisResult(lang, lineCount, complexity, issues, securityRisks, suggestions);
    }

    public String handleIntent(DeveloperIntent intent, String codeSnippet) {
        CodeAnalysisResult analysis = analyzeCode(codeSnippet);
        StringBuilder sb = new StringBuilder();

        switch (intent) {
            case ANALYZE -> {
                sb.append("=== STATIC CODE ANALYSIS REPORT ===\n");
                sb.append("  Detected Language  : ").append(analysis.language()).append("\n");
                sb.append("  Total Lines        : ").append(analysis.lineCount()).append("\n");
                sb.append("  Complexity Score   : ").append(analysis.complexityScore()).append("\n");
                sb.append("  Potential Issues   : ").append(analysis.potentialIssues().size()).append("\n");
                for (String issue : analysis.potentialIssues()) {
                    sb.append("    • ").append(issue).append("\n");
                }
                sb.append("  Security Risks     : ").append(analysis.securityRisks().size()).append("\n");
                for (String sec : analysis.securityRisks()) {
                    sb.append("    ⚠ ").append(sec).append("\n");
                }
                sb.append("  Suggestions        : ").append(analysis.suggestions().size()).append("\n");
                for (String sugg : analysis.suggestions()) {
                    sb.append("    💡 ").append(sugg).append("\n");
                }
            }
            case REVIEW -> {
                sb.append("=== SPECTRAL CODE REVIEW ===\n");
                sb.append("Language: ").append(analysis.language()).append("\n");
                if (!analysis.hasIssues()) {
                    sb.append("Clean structure detected. No blatant security hazards or swallowed exceptions.\n");
                    sb.append("Recommendations: Maintain rigorous unit test coverage and assert invariants at boundaries.\n");
                } else {
                    sb.append("Review Findings:\n");
                    for (String risk : analysis.securityRisks()) sb.append("  [CRITICAL] ").append(risk).append("\n");
                    for (String iss : analysis.potentialIssues()) sb.append("  [WARNING]  ").append(iss).append("\n");
                }
            }
            case DEBUG -> {
                sb.append("=== DEBUG TRIAGE ===\n");
                sb.append("Tracing runtime path for language: ").append(analysis.language()).append("\n");
                sb.append("Verify invariants:\n");
                sb.append("  1. Check inputs and dereferences before execution.\n");
                sb.append("  2. Examine state mutations in loops and conditional branches.\n");
                sb.append("  3. Ensure exception handlers capture root cause diagnostics.\n");
            }
            case TESTS -> {
                sb.append("=== UNIT TEST TEMPLATE (").append(analysis.language()).append(") ===\n");
                if ("Java".equals(analysis.language())) {
                    sb.append("""
                        @Test
                        void shouldExecuteSafelyUnderNominalInputs() {
                            // Given: valid state
                            
                            // When: invoked
                            
                            // Then: assert invariants
                        }
                        
                        @Test
                        void shouldThrowExpectedExceptionUnderNullBoundary() {
                            // assertThrows(NullPointerException.class, () -> ...);
                        }
                        """);
                } else {
                    sb.append("Defines test assertions for: valid input, null/empty edge cases, boundary conditions.\n");
                }
            }
            default -> {
                sb.append("=== DEVELOPER INTELLIGENCE (").append(intent.getCommand().toUpperCase()).append(") ===\n");
                sb.append("Target Language: ").append(analysis.language()).append(" (").append(analysis.lineCount()).append(" lines)\n");
                sb.append("Ready to assist. Detail your request for deeper code generation or refactoring.\n");
            }
        }

        return sb.toString().trim();
    }
}
