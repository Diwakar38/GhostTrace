package com.ghosttrace.system;

public record SystemTelemetry(
    double cpuUsagePercent,
    long jvmMemoryUsedMb,
    long jvmMemoryMaxMb,
    long systemMemoryFreeMb,
    long systemMemoryTotalMb,
    long diskFreeGb,
    long diskTotalGb,
    int activeThreads,
    String osName,
    String osArch,
    String javaVersion,
    long uptimeSeconds
) {}
