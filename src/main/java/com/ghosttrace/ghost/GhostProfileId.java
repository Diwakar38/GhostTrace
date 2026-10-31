package com.ghosttrace.ghost;

public enum GhostProfileId {
    FORGOTTEN_MACHINE("forgotten-machine"),
    PHANTOM_CODER("phantom-coder"),
    GUARDIAN_SPIRIT("guardian-spirit");

    private final String value;

    GhostProfileId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static GhostProfileId fromString(String text) {
        if (text == null) return FORGOTTEN_MACHINE;
        String clean = text.trim().toLowerCase().replace("_", "-");
        for (GhostProfileId id : values()) {
            if (id.value.equals(clean) || id.name().equalsIgnoreCase(clean.replace("-", "_"))) {
                return id;
            }
        }
        return FORGOTTEN_MACHINE;
    }
}
