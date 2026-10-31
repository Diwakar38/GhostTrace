package com.ghosttrace;

import com.ghosttrace.config.Settings;
import com.ghosttrace.ghost.GhostEngine;
import com.ghosttrace.haunt.EventCategory;
import com.ghosttrace.haunt.HauntEngine;
import com.ghosttrace.haunt.HauntMode;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

class HauntEngineTest {

    @Test
    void shouldRespectFocusModeSuppression(@TempDir Path tempDir) {
        Settings settings = new Settings(tempDir);
        GhostEngine ghostEngine = new GhostEngine(settings);
        HauntEngine hauntEngine = new HauntEngine(settings, ghostEngine);

        hauntEngine.setMode(HauntMode.FOCUS);
        assertTrue(hauntEngine.isAtmosphereSuppressed());
        assertNull(hauntEngine.triggerEvent(EventCategory.IDLE_WHISPER));

        hauntEngine.setMode(HauntMode.HAUNTED);
        assertFalse(hauntEngine.isAtmosphereSuppressed());
    }

    @Test
    void shouldTriggerAtmosphericEventAndEnforceCooldown(@TempDir Path tempDir) {
        Settings settings = new Settings(tempDir);
        GhostEngine ghostEngine = new GhostEngine(settings);
        HauntEngine hauntEngine = new HauntEngine(settings, ghostEngine);

        hauntEngine.setMode(HauntMode.HAUNTED);
        String event = hauntEngine.triggerEvent(EventCategory.IDLE_WHISPER);
        assertNotNull(event);
        assertTrue(event.contains("░▒▓"));

        // Immediate subsequent trigger should be blocked by cooldown
        String immediateSecond = hauntEngine.triggerEvent(EventCategory.IDLE_WHISPER);
        assertNull(immediateSecond);
    }
}
