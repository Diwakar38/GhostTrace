package com.ghosttrace.system;

import java.util.Map;

public record HealthReport(
    HealthStatus overallStatus,
    Map<String, SubsystemHealth> subsystems,
    SystemTelemetry telemetry
) {
    public record SubsystemHealth(HealthStatus status, String message) {}
}
