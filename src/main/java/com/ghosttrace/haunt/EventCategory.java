package com.ghosttrace.haunt;

public enum EventCategory {
    GHOST_GREETING("ghost_greeting"),
    IDLE_WHISPER("idle_whisper"),
    MOOD_SHIFT("mood_shift"),
    GLITCH_MANIFESTATION("glitch_manifestation"),
    TERMINAL_FLICKER("terminal_flicker"),
    SPECTRAL_ECHO("spectral_echo"),
    LATE_NIGHT_HORROR("late_night_horror"),
    SECURITY_BREACH("security_breach"),
    SPECIAL_EVENT("special_event");

    private final String value;

    EventCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
