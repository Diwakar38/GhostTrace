package com.ghosttrace.memory;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MemoryDatabase {

    private final String jdbcUrl;

    public MemoryDatabase(Path dbPath) {
        this.jdbcUrl = "jdbc:sqlite:" + dbPath.toAbsolutePath();
        initializeSchema();
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl);
    }

    private void initializeSchema() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Enable WAL mode for high concurrency & reliability
            stmt.execute("PRAGMA journal_mode=WAL;");
            stmt.execute("PRAGMA synchronous=NORMAL;");

            // Memories table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS memories (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    key TEXT NOT NULL UNIQUE,
                    content TEXT NOT NULL,
                    category TEXT NOT NULL DEFAULT 'NOTE',
                    tags TEXT DEFAULT '',
                    importance INTEGER DEFAULT 1,
                    created_at INTEGER NOT NULL
                );
                """);

            // Projects table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS projects (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE,
                    path TEXT NOT NULL,
                    languages TEXT DEFAULT '',
                    total_files INTEGER DEFAULT 0,
                    total_lines INTEGER DEFAULT 0,
                    scanned_at INTEGER NOT NULL
                );
                """);

        } catch (SQLException e) {
            System.err.println("Warning: Could not initialize SQLite schema: " + e.getMessage());
        }
    }
}
