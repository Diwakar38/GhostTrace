package com.ghosttrace.developer;

public enum DeveloperIntent {
    EXPLAIN("explain", "Provide clear technical explanation of code architecture or syntax."),
    DEBUG("debug", "Diagnose error traces, exceptions, and unexpected runtime states."),
    REVIEW("review", "Perform code review focusing on style, safety, and defensive patterns."),
    GENERATE("generate", "Generate idiomatic implementation code for a specified task."),
    REFACTOR("refactor", "Suggest cleaner, more modular, or more performant structures."),
    TESTS("tests", "Generate comprehensive unit and integration test fixtures."),
    ANALYZE("analyze", "Static heuristics analysis for risk, complexity, and resource leaks."),
    LANGUAGE("language", "Detect source language and provide idiomatic conventions.");

    private final String command;
    private final String description;

    DeveloperIntent(String command, String description) {
        this.command = command;
        this.description = description;
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }

    public static DeveloperIntent fromString(String text) {
        if (text == null) return EXPLAIN;
        String clean = text.trim().toLowerCase();
        for (DeveloperIntent intent : values()) {
            if (intent.command.equals(clean) || intent.name().equalsIgnoreCase(clean)) {
                return intent;
            }
        }
        return EXPLAIN;
    }
}
