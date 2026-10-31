package com.ghosttrace;

import com.ghosttrace.config.Settings;
import com.ghosttrace.ghost.GhostEngine;
import com.ghosttrace.ghost.GhostMood;
import com.ghosttrace.ghost.GhostProfileId;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

class GhostEngineTest {

    @Test
    void shouldSwitchProfilesCorrectly(@TempDir Path tempDir) {
        Settings settings = new Settings(tempDir);
        GhostEngine engine = new GhostEngine(settings);

        assertEquals(GhostProfileId.FORGOTTEN_MACHINE, engine.getProfile().id());

        engine.setProfile("phantom-coder");
        assertEquals(GhostProfileId.PHANTOM_CODER, engine.getProfile().id());
        assertEquals("Phantom Coder", engine.getProfile().name());

        engine.setProfile("guardian-spirit");
        assertEquals(GhostProfileId.GUARDIAN_SPIRIT, engine.getProfile().id());
    }

    @Test
    void shouldGenerateThematicGreetings(@TempDir Path tempDir) {
        Settings settings = new Settings(tempDir);
        GhostEngine engine = new GhostEngine(settings);

        String greeting = engine.generateGreeting();
        assertNotNull(greeting);
        assertFalse(greeting.isBlank());
    }

    @Test
    void shouldTransitionMoodOnHighRiskCommand(@TempDir Path tempDir) {
        Settings settings = new Settings(tempDir);
        GhostEngine engine = new GhostEngine(settings);

        engine.setMood(GhostMood.NEUTRAL);
        engine.onCommandExecuted(false, true); // High risk command
        assertEquals(GhostMood.VIGILANT, engine.getMood());

        engine.onCommandExecuted(false, false); // Failed ordinary command
        assertEquals(GhostMood.AGITATED, engine.getMood());
    }

    @Test
    void shouldDecorateResponseWithSpectralNuances(@TempDir Path tempDir) {
        Settings settings = new Settings(tempDir);
        GhostEngine engine = new GhostEngine(settings);
        engine.setMood(GhostMood.VIGILANT);

        String decorated = engine.decorateResponse("Process completed.");
        assertNotNull(decorated);
        assertTrue(decorated.contains("Process completed."));
    }
}
