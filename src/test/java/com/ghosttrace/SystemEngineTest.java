package com.ghosttrace;

import com.ghosttrace.config.Settings;
import com.ghosttrace.system.HealthReport;
import com.ghosttrace.system.HealthStatus;
import com.ghosttrace.system.SystemEngine;
import com.ghosttrace.system.SystemTelemetry;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

class SystemEngineTest {

    @Test
    void shouldCollectHostTelemetry(@TempDir Path tempDir) {
        Settings settings = new Settings(tempDir);
        SystemEngine engine = new SystemEngine(settings);

        SystemTelemetry telem = engine.getTelemetry();
        assertNotNull(telem);
        assertNotNull(telem.osName());
        assertNotNull(telem.javaVersion());
        assertTrue(telem.jvmMemoryMaxMb() > 0);
        assertTrue(telem.activeThreads() > 0);
    }

    @Test
    void shouldGenerateHealthReportAcrossSubsystems(@TempDir Path tempDir) {
        Settings settings = new Settings(tempDir);
        SystemEngine engine = new SystemEngine(settings);

        HealthReport report = engine.getHealthReport(true, 5);
        assertNotNull(report);
        assertEquals(HealthStatus.OK, report.overallStatus());
        assertTrue(report.subsystems().containsKey("guardian"));
        assertTrue(report.subsystems().containsKey("memory"));
        assertTrue(report.subsystems().containsKey("audio"));
        assertTrue(report.subsystems().containsKey("ai"));
        assertTrue(report.subsystems().containsKey("system"));
    }
}
