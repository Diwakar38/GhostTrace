package com.ghosttrace.haunt;

public enum HauntMode {
    NORMAL("normal", "Standard terminal assistance; subtle occasional atmospheric touches."),
    AMBIENT("ambient", "Balanced haunted experience; periodic whispers, audio events, and mood reactions."),
    HAUNTED("haunted", "Maximum supernatural immersion; frequent glitches, active horror director, spectral echoes."),
    FOCUS("focus", "Atmospheric and audio effects suppressed for deep programming sessions.");

    private final String value;
    private final String description;

    HauntMode(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static HauntMode fromString(String text) {
        if (text == null) return HAUNTED;
        String clean = text.trim().toLowerCase();
        for (HauntMode mode : values()) {
            if (mode.value.equals(clean) || mode.name().equalsIgnoreCase(clean)) {
                return mode;
            }
        }
        return HAUNTED;
    }
}
