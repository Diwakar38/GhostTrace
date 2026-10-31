package com.ghosttrace;

import com.ghosttrace.config.Settings;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

class SettingsTest {

    @Test
    void shouldInitializeWithDefaultValues(@TempDir Path tempDir) {
        Settings settings = new Settings(tempDir);

        assertEquals("GhostTrace", settings.getAppName());
        assertEquals("1.2.0", settings.getAppVersion());
        assertEquals("forgotten-machine", settings.getGhostProfile());
        assertEquals("haunted", settings.getHauntMode());
        assertTrue(settings.isAudioEnabled());
        assertEquals(0.80f, settings.getAudioMasterVolume(), 0.001f);
        assertTrue(settings.isGuardianEnabled());
        assertTrue(settings.isGuardianBlockHighRisk());
    }

    @Test
    void shouldClampAudioVolumeBounds(@TempDir Path tempDir) {
        Settings settings = new Settings(tempDir);

        settings.setAudioMasterVolume(1.5f);
        assertEquals(1.0f, settings.getAudioMasterVolume(), 0.001f);

        settings.setAudioMasterVolume(-0.5f);
        assertEquals(0.0f, settings.getAudioMasterVolume(), 0.001f);
    }

    @Test
    void shouldPersistAndReloadSettings(@TempDir Path tempDir) {
        Settings settings = new Settings(tempDir);
        settings.setGhostProfile("phantom-coder");
        settings.setHauntMode("focus");
        settings.setAudioMasterVolume(0.45f);
        settings.save();

        Settings reloaded = new Settings(tempDir);
        assertEquals("phantom-coder", reloaded.getGhostProfile());
        assertEquals("focus", reloaded.getHauntMode());
        assertEquals(0.45f, reloaded.getAudioMasterVolume(), 0.001f);
    }
}
