package com.ghosttrace.system;

import com.ghosttrace.config.Settings;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.LinkedHashMap;
import java.util.Map;

public class SystemEngine {

    private final Settings settings;
    private final RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
    private final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
    private final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

    public SystemEngine(Settings settings) {
        this.settings = settings;
    }

    public SystemTelemetry getTelemetry() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemoryMb = (totalMemory - freeMemory) / (1024 * 1024);
        long maxMemoryMb = runtime.maxMemory() / (1024 * 1024);

        long sysTotalMb = 0;
        long sysFreeMb = 0;
        double cpuPercent = 0.0;

        try {
            if (osBean instanceof com.sun.management.OperatingSystemMXBean sunOs) {
                sysTotalMb = sunOs.getTotalMemorySize() / (1024 * 1024);
                sysFreeMb = sunOs.getFreeMemorySize() / (1024 * 1024);
                double load = sunOs.getCpuLoad();
                if (load >= 0) {
                    cpuPercent = Math.round(load * 1000.0) / 10.0;
                }
            }
        } catch (Throwable ignored) {}

        File root = new File(".");
        long diskFreeGb = root.getUsableSpace() / (1024 * 1024 * 1024);
        long diskTotalGb = root.getTotalSpace() / (1024 * 1024 * 1024);

        long uptimeSec = runtimeBean.getUptime() / 1000;
        int activeThreads = threadBean.getThreadCount();

        return new SystemTelemetry(
            cpuPercent,
            usedMemoryMb,
            maxMemoryMb,
            sysFreeMb,
            sysTotalMb,
            diskFreeGb,
            diskTotalGb,
            activeThreads,
            System.getProperty("os.name", "Unknown"),
            System.getProperty("os.arch", "Unknown"),
            System.getProperty("java.version", "Unknown"),
            uptimeSec
        );
    }

    public HealthReport getHealthReport(boolean isAudioAvailable, int memoryCount) {
        SystemTelemetry telem = getTelemetry();
        Map<String, HealthReport.SubsystemHealth> subsystems = new LinkedHashMap<>();

        // 1. Guardian Subsystem
        if (settings.isGuardianEnabled()) {
            subsystems.put("guardian", new HealthReport.SubsystemHealth(HealthStatus.OK, "Active (block_high_risk=" + settings.isGuardianBlockHighRisk() + ")"));
        } else {
            subsystems.put("guardian", new HealthReport.SubsystemHealth(HealthStatus.WARN, "Disabled"));
        }

        // 2. Memory Subsystem
        subsystems.put("memory", new HealthReport.SubsystemHealth(HealthStatus.OK, "Active (" + memoryCount + " items in SQLite)"));

        // 3. Audio Subsystem
        if (!settings.isAudioEnabled()) {
            subsystems.put("audio", new HealthReport.SubsystemHealth(HealthStatus.WARN, "Disabled in settings"));
        } else if (settings.isAudioMuted()) {
            subsystems.put("audio", new HealthReport.SubsystemHealth(HealthStatus.WARN, "Muted"));
        } else if (isAudioAvailable) {
            subsystems.put("audio", new HealthReport.SubsystemHealth(HealthStatus.OK, "Available & active"));
        } else {
            subsystems.put("audio", new HealthReport.SubsystemHealth(HealthStatus.WARN, "Hardware unavailable"));
        }

        // 4. AI Subsystem
        subsystems.put("ai", new HealthReport.SubsystemHealth(HealthStatus.OK, "Configured: " + settings.getAiEndpoint() + " (" + settings.getAiModel() + ")"));

        // 5. System Subsystem
        if (telem.diskFreeGb() < 2) {
            subsystems.put("system", new HealthReport.SubsystemHealth(HealthStatus.WARN, "Low disk space (<2 GB)"));
        } else {
            subsystems.put("system", new HealthReport.SubsystemHealth(HealthStatus.OK, "Nominal (CPU: " + telem.cpuUsagePercent() + "%, RAM: " + telem.jvmMemoryUsedMb() + "MB)"));
        }

        HealthStatus overall = HealthStatus.OK;
        for (HealthReport.SubsystemHealth sub : subsystems.values()) {
            if (sub.status() == HealthStatus.FAIL) {
                overall = HealthStatus.FAIL;
                break;
            } else if (sub.status() == HealthStatus.WARN && overall != HealthStatus.FAIL) {
                overall = HealthStatus.WARN;
            }
        }

        return new HealthReport(overall, subsystems, telem);
    }
}
