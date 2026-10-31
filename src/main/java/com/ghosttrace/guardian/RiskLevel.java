package com.ghosttrace.guardian;

public enum RiskLevel {
    LOW(1, "Low Risk", "#00ff66"),
    MEDIUM(2, "Medium Risk", "#ffaa00"),
    HIGH(3, "High Risk", "#ff5555"),
    CRITICAL(4, "Critical Risk", "#ff0033");

    private final int severity;
    private final String label;
    private final String colorHex;

    RiskLevel(int severity, String label, String colorHex) {
        this.severity = severity;
        this.label = label;
        this.colorHex = colorHex;
    }

    public int getSeverity() {
        return severity;
    }

    public String getLabel() {
        return label;
    }

    public String getColorHex() {
        return colorHex;
    }
}
