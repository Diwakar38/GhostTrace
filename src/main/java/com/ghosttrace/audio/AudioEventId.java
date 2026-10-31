package com.ghosttrace.audio;

public enum AudioEventId {
    // SFX
    STARTUP("sfx/startup.wav", "Startup sound effect"),
    SHUTDOWN("sfx/shutdown.wav", "Shutdown sound effect"),
    NOTIFICATION_PING("sfx/notification_ping.wav", "Notification ping"),
    NOTIFICATION_ALERT("sfx/notification_alert.wav", "Notification alert"),

    // Haunt
    MOOD_SHIFT("haunt/mood_shift.wav", "Mood shift resonance"),
    KEYSTROKE_HAUNT("haunt/keystroke_haunt.wav", "Haunted keystroke click"),
    GUARDIAN_INTERCEPT("haunt/guardian_intercept.wav", "Guardian intercept warning"),
    GUARDIAN_BLOCKED("haunt/guardian_blocked.wav", "Guardian blocked alert"),
    GUARDIAN_APPROVED("haunt/guardian_approved.wav", "Guardian approved chime"),
    CODING_SUCCESS("haunt/coding_success.wav", "Coding success resolution"),
    APP_ERROR("haunt/app_error.wav", "Application error buzz"),

    // Glitch
    STATIC_BURST("glitch/static_burst.wav", "Static burst distortion"),
    DIGITAL_GLITCH("glitch/digital_glitch.wav", "Digital glitch chirp"),
    CORRUPTION_ARTIFACT("glitch/corruption_artifact.wav", "Corruption artifact hum"),

    // Ambient
    DISTANT_THUNDER("ambient/distant_thunder.wav", "Distant thunder rumble"),
    DARK_HUM("ambient/dark_hum.wav", "Deep dark hum drone"),
    COLD_WIND("ambient/cold_wind.wav", "Cold wind sweep"),

    // Whisper
    SPECTRAL_WHISPER("whisper/spectral_whisper.wav", "Spectral whisper breath"),
    EERIE_MURMUR("whisper/eerie_murmur.wav", "Eerie murmur"),
    CHILLING_BREATH("whisper/chilling_breath.wav", "Chilling breath");

    private final String relativePath;
    private final String description;

    AudioEventId(String relativePath, String description) {
        this.relativePath = relativePath;
        this.description = description;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public String getDescription() {
        return description;
    }
}
