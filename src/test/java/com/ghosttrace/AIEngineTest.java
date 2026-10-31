package com.ghosttrace;

import com.ghosttrace.ai.AIEngine;
import com.ghosttrace.ai.InferenceResult;
import com.ghosttrace.config.Settings;
import com.ghosttrace.ghost.GhostEngine;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

class AIEngineTest {

    @Test
    void shouldRespondViaSpectralBrainWhenOffline(@TempDir Path tempDir) {
        Settings settings = new Settings(tempDir);
        GhostEngine ghostEngine = new GhostEngine(settings);
        AIEngine aiEngine = new AIEngine(settings, ghostEngine);

        InferenceResult result = aiEngine.ask("How to fix NullPointerException?", "");
        assertNotNull(result);
        assertNotNull(result.content());
        assertFalse(result.content().isBlank());
        assertTrue(result.isOfflineFallback());
        assertTrue(result.content().contains("dereference") || result.content().contains("Objects.requireNonNull"));
    }

    @Test
    void shouldMaintainTurnHistory(@TempDir Path tempDir) {
        Settings settings = new Settings(tempDir);
        GhostEngine ghostEngine = new GhostEngine(settings);
        AIEngine aiEngine = new AIEngine(settings, ghostEngine);

        assertEquals(0, aiEngine.getHistoryTurnCount());

        aiEngine.ask("Hello terminal ghost", "");
        assertEquals(1, aiEngine.getHistoryTurnCount());

        aiEngine.ask("Tell me about git rebase", "");
        assertEquals(2, aiEngine.getHistoryTurnCount());

        aiEngine.clearHistory();
        assertEquals(0, aiEngine.getHistoryTurnCount());
    }
}
