package com.ghosttrace.memory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class ProjectScanner {

    private static final Set<String> IGNORED_DIRS = Set.of(
        ".git", ".svn", "target", "build", "node_modules", ".venv", "venv", ".idea", ".vscode", "dist", "out"
    );

    private static final Set<String> IGNORED_EXTENSIONS = Set.of(
        "db", "db-shm", "db-wal", "log", "class", "jar", "war"
    );

    public ProjectInfo scan(String name, Path projectRoot) {
        if (!Files.exists(projectRoot) || !Files.isDirectory(projectRoot)) {
            throw new IllegalArgumentException("Project directory does not exist: " + projectRoot);
        }

        AtomicInteger totalFiles = new AtomicInteger();
        AtomicLong totalLines = new AtomicLong();
        Set<String> languages = new HashSet<>();

        try (Stream<Path> paths = Files.walk(projectRoot)) {
            paths.filter(Files::isRegularFile).forEach(file -> {
                Path relative = projectRoot.relativize(file);
                boolean isIgnored = false;
                for (Path part : relative) {
                    if (IGNORED_DIRS.contains(part.toString())) {
                        isIgnored = true;
                        break;
                    }
                }
                String ext = getFileExtension(file.toString());
                if (IGNORED_EXTENSIONS.contains(ext)) {
                    isIgnored = true;
                }
                if (!isIgnored) {
                    totalFiles.incrementAndGet();
                    String lang = extensionToLanguage(ext);
                    if (lang != null) languages.add(lang);

                    // Count lines
                    try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
                        long lines = reader.lines().count();
                        totalLines.addAndGet(lines);
                    } catch (Exception ignored) {}
                }
            });
        } catch (Exception e) {
            System.err.println("Warning during project scan: " + e.getMessage());
        }

        String langsJoined = languages.isEmpty() ? "None detected" : String.join(", ", languages);
        return new ProjectInfo(
            0,
            name,
            projectRoot.toAbsolutePath().toString(),
            langsJoined,
            totalFiles.get(),
            totalLines.get(),
            Instant.now()
        );
    }

    private String getFileExtension(String name) {
        int idx = name.lastIndexOf('.');
        return idx > 0 ? name.substring(idx + 1).toLowerCase() : "";
    }

    private String extensionToLanguage(String ext) {
        return switch (ext) {
            case "java" -> "Java";
            case "py" -> "Python";
            case "js", "jsx", "ts", "tsx" -> "JavaScript/TypeScript";
            case "go" -> "Go";
            case "rs" -> "Rust";
            case "c", "h", "cpp", "hpp" -> "C/C++";
            case "sh", "bash" -> "Shell";
            case "sql" -> "SQL";
            case "html", "css" -> "Web (HTML/CSS)";
            case "json", "xml", "yaml", "yml" -> "Config/Data";
            default -> null;
        };
    }
}
