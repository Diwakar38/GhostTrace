package com.ghosttrace;

import com.ghosttrace.config.Settings;
import com.ghosttrace.memory.MemoryEngine;
import com.ghosttrace.memory.MemoryItem;
import com.ghosttrace.memory.ProjectInfo;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

class MemoryEngineTest {

    @Test
    void shouldPersistAndSearchMemoriesInSQLite(@TempDir Path tempDir) {
        Settings settings = new Settings(tempDir);
        MemoryEngine engine = new MemoryEngine(settings);

        assertTrue(engine.saveMemory("prod_endpoint", "https://api.ghost.internal", "CONFIG", "prod,api", 2));
        assertTrue(engine.saveMemory("redis_port", "6379", "CONFIG", "redis", 1));

        assertEquals(2, engine.getMemoryCount());

        List<MemoryItem> found = engine.searchMemories("ghost");
        assertEquals(1, found.size());
        assertEquals("prod_endpoint", found.get(0).key());
        assertEquals("https://api.ghost.internal", found.get(0).content());

        assertTrue(engine.deleteMemory("redis_port"));
        assertEquals(1, engine.getMemoryCount());

        engine.clearMemories();
        assertEquals(0, engine.getMemoryCount());
    }

    @Test
    void shouldScanProjectDirectoryAndCountLines(@TempDir Path tempDir) throws Exception {
        Path sub = tempDir.resolve("src");
        Files.createDirectories(sub);
        Files.writeString(sub.resolve("App.java"), "public class App {\n    // line 2\n    // line 3\n}\n");
        Files.writeString(sub.resolve("script.py"), "print('hello')\n");

        Settings settings = new Settings(tempDir);
        MemoryEngine engine = new MemoryEngine(settings);

        ProjectInfo info = engine.scanAndRegisterProject("TestApp", tempDir.toString());
        assertNotNull(info);
        assertEquals("TestApp", info.name());
        assertEquals(2, info.totalFiles());
        assertTrue(info.totalLines() >= 5);
        assertTrue(info.languages().contains("Java"));
        assertTrue(info.languages().contains("Python"));
    }
}
