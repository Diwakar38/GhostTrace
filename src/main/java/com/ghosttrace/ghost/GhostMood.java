package com.ghosttrace.ghost;

public enum GhostMood {
    NEUTRAL("neutral", "Dormant balance; standard baseline processing."),
    CURIOUS("curious", "Analyzing intricate code structures or novel inquiries."),
    PLAYFUL("playful", "Finding amusement in technical puzzles and bug hunts."),
    HAUNTED("haunted", "Subtle spectral resonance; atmospheric latency and glitch artifacts."),
    AGITATED("agitated", "Disrupted by recurring errors, aborted commands, or malformed inputs."),
    VIGILANT("vigilant", "Heightened awareness triggered by risky or destructive operations.");

    private final String value;
    private final String description;

    GhostMood(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
