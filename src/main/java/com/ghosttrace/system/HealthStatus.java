package com.ghosttrace.system;

import java.util.Map;

public enum HealthStatus {
    OK("OK", "#00ff66"),
    WARN("WARN", "#ffaa00"),
    FAIL("FAIL", "#ff5555");

    private final String label;
    private final String colorHex;

    HealthStatus(String label, String colorHex) {
        this.label = label;
        this.colorHex = colorHex;
    }

    public String getLabel() {
        return label;
    }

    public String getColorHex() {
        return colorHex;
    }
}
