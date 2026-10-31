package com.ghosttrace.memory;

import com.ghosttrace.config.Settings;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MemoryEngine {

    private final Settings settings;
    private final MemoryDatabase database;
    private final MemoryRepository repository;
    private final ProjectScanner scanner;
    private ProjectInfo activeProject;

    public MemoryEngine(Settings settings) {
        this.settings = settings;
        this.database = new MemoryDatabase(settings.getDbPath());
        this.repository = new MemoryRepository(this.database);
        this.scanner = new ProjectScanner();
    }

    public boolean saveMemory(String key, String content, String category, String tags, int importance) {
        return repository.save(key, content, category, tags, importance);
    }

    public List<MemoryItem> listMemories(int limit) {
        return repository.list(limit);
    }

    public List<MemoryItem> searchMemories(String query) {
        return repository.search(query);
    }

    public boolean deleteMemory(String key) {
        return repository.delete(key);
    }

    public void clearMemories() {
        repository.clear();
    }

    public int getMemoryCount() {
        return repository.count();
    }

    public ProjectInfo scanAndRegisterProject(String name, String dirPath) {
        Path path = Paths.get(dirPath).toAbsolutePath().normalize();
        ProjectInfo info = scanner.scan(name, path);
        this.activeProject = info;
        return info;
    }

    public ProjectInfo getActiveProject() {
        return activeProject;
    }

    public String buildContextForAI() {
        StringBuilder sb = new StringBuilder();
        if (activeProject != null) {
            sb.append("Active Project: ").append(activeProject.name())
              .append(" (").append(activeProject.languages()).append(", ")
              .append(activeProject.totalFiles()).append(" files, ")
              .append(activeProject.totalLines()).append(" lines)\n");
        }
        List<MemoryItem> topMemories = repository.list(5);
        if (!topMemories.isEmpty()) {
            sb.append("Persistent Developer Context:\n");
            for (MemoryItem mem : topMemories) {
                sb.append("  • [").append(mem.key()).append("]: ").append(mem.content()).append("\n");
            }
        }
        return sb.toString().trim();
    }
}
