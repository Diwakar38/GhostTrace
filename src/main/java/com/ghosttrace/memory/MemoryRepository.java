package com.ghosttrace.memory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MemoryRepository {

    private final MemoryDatabase database;

    public MemoryRepository(MemoryDatabase database) {
        this.database = database;
    }

    public boolean save(String key, String content, String category, String tags, int importance) {
        String sql = """
            INSERT INTO memories (key, content, category, tags, importance, created_at)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT(key) DO UPDATE SET
                content = excluded.content,
                category = excluded.category,
                tags = excluded.tags,
                importance = excluded.importance,
                created_at = excluded.created_at;
            """;

        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, key.trim());
            stmt.setString(2, content.trim());
            stmt.setString(3, category != null ? category.trim() : "NOTE");
            stmt.setString(4, tags != null ? tags.trim() : "");
            stmt.setInt(5, importance);
            stmt.setLong(6, Instant.now().getEpochSecond());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Database error saving memory: " + e.getMessage());
            return false;
        }
    }

    public List<MemoryItem> list(int limit) {
        String sql = "SELECT id, key, content, category, tags, importance, created_at FROM memories ORDER BY created_at DESC LIMIT ?";
        List<MemoryItem> list = new ArrayList<>();

        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error listing memories: " + e.getMessage());
        }
        return list;
    }

    public List<MemoryItem> search(String query) {
        String sql = "SELECT id, key, content, category, tags, importance, created_at FROM memories WHERE key LIKE ? OR content LIKE ? OR tags LIKE ? ORDER BY importance DESC, created_at DESC LIMIT 50";
        List<MemoryItem> list = new ArrayList<>();
        String pattern = "%" + query.trim() + "%";

        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error searching memories: " + e.getMessage());
        }
        return list;
    }

    public boolean delete(String key) {
        String sql = "DELETE FROM memories WHERE key = ?";
        try (Connection conn = database.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, key.trim());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database error deleting memory: " + e.getMessage());
            return false;
        }
    }

    public void clear() {
        try (Connection conn = database.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM memories;");
        } catch (SQLException e) {
            System.err.println("Database error clearing memories: " + e.getMessage());
        }
    }

    public int count() {
        try (Connection conn = database.getConnection(); Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM memories")) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ignored) {}
        return 0;
    }

    private MemoryItem mapRow(ResultSet rs) throws SQLException {
        return new MemoryItem(
            rs.getLong("id"),
            rs.getString("key"),
            rs.getString("content"),
            rs.getString("category"),
            rs.getString("tags"),
            rs.getInt("importance"),
            Instant.ofEpochSecond(rs.getLong("created_at"))
        );
    }
}
