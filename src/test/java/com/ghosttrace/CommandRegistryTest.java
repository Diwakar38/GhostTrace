package com.ghosttrace;

import com.ghosttrace.ai.AIEngine;
import com.ghosttrace.audio.AudioManager;
import com.ghosttrace.audio.HorrorAudioDirector;
import com.ghosttrace.config.Settings;
import com.ghosttrace.developer.DeveloperEngine;
import com.ghosttrace.ghost.GhostEngine;
import com.ghosttrace.guardian.GuardianEngine;
import com.ghosttrace.haunt.HauntEngine;
import com.ghosttrace.memory.MemoryEngine;
import com.ghosttrace.system.SystemEngine;
import com.ghosttrace.terminal.CommandContext;
import com.ghosttrace.terminal.CommandRegistry;
import com.ghosttrace.terminal.CommandResult;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

class CommandRegistryTest {

    private CommandContext context;
    private CommandRegistry registry;

    @BeforeEach
    void setup(@TempDir Path tempDir) {
        Settings settings = new Settings(tempDir);
        GhostEngine ghostEngine = new GhostEngine(settings);
        GuardianEngine guardianEngine = new GuardianEngine(settings);
        MemoryEngine memoryEngine = new MemoryEngine(settings);
        AIEngine aiEngine = new AIEngine(settings, ghostEngine);
        DeveloperEngine developerEngine = new DeveloperEngine();
        SystemEngine systemEngine = new SystemEngine(settings);
        HauntEngine hauntEngine = new HauntEngine(settings, ghostEngine);
        AudioManager audioManager = new AudioManager(settings);
        HorrorAudioDirector horrorDirector = new HorrorAudioDirector(settings, audioManager);

        context = new CommandContext(
            settings,
            ghostEngine,
            guardianEngine,
            memoryEngine,
            aiEngine,
            developerEngine,
            systemEngine,
            hauntEngine,
            audioManager,
            horrorDirector
        );
        registry = new CommandRegistry();
    }

    @Test
    void shouldDispatchHelpCommand() {
        CommandResult result = registry.dispatch(context, "help");
        assertTrue(result.success());
        assertTrue(result.output().contains("GHOSTTRACE COMMAND REGISTRY"));
    }

    @Test
    void shouldDispatchVersionCommand() {
        CommandResult result = registry.dispatch(context, "version");
        assertTrue(result.success());
        assertTrue(result.output().contains("1.2.0"));
    }

    @Test
    void shouldDispatchStatusCommand() {
        CommandResult result = registry.dispatch(context, "status");
        assertTrue(result.success());
        assertTrue(result.output().contains("GHOSTTRACE STATUS REPORT"));
    }

    @Test
    void shouldDispatchClearCommand() {
        CommandResult result = registry.dispatch(context, "clear");
        assertTrue(result.success());
        assertTrue(result.shouldClear());
    }

    @Test
    void shouldDispatchExitCommand() {
        CommandResult result = registry.dispatch(context, "exit");
        assertTrue(result.success());
        assertTrue(result.shouldExit());
    }

    @Test
    void shouldDispatchGhostProfileSwitch() {
        CommandResult result = registry.dispatch(context, "ghost set phantom-coder");
        assertTrue(result.success());
        assertEquals("Phantom Coder", context.ghostEngine().getProfile().name());
    }

    @Test
    void shouldDispatchGuardianAnalysis() {
        CommandResult result = registry.dispatch(context, "guard analyze rm -rf /");
        assertTrue(result.success());
        assertTrue(result.output().contains("Critical Risk"));
    }

    @Test
    void shouldDispatchMemoryCrud() {
        CommandResult save = registry.dispatch(context, "memory save port 8080");
        assertTrue(save.success());

        CommandResult list = registry.dispatch(context, "memory list");
        assertTrue(list.success());
        assertTrue(list.output().contains("port"));
    }

    @Test
    void shouldDispatchAudioVolume() {
        CommandResult vol = registry.dispatch(context, "audio volume 50");
        assertTrue(vol.success());
        assertEquals(0.50f, context.audioManager().getMasterVolume(), 0.01f);
    }
}
