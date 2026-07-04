package com.cineplan.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:cineplan.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /** Initialize DB on first app start. Creates tables if missing and runs migrations. */
    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Create movies table if it does not exist (basic columns already present)
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS movies (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title TEXT," +
                    "genre TEXT," +
                    "runtime INTEGER," +
                    "rating REAL," +
                    "entertainment_score INTEGER," +
                    "poster_url TEXT," +
                    "description TEXT," +
                    "watch_url TEXT," +
                    "language TEXT" +
                    ")");

            // ==== MIGRATIONS: add new columns if they don't exist ====
            addColumnIfMissing(conn, "movies", "imdb_id", "TEXT");
            addColumnIfMissing(conn, "movies", "imdb_url", "TEXT");
            addColumnIfMissing(conn, "movies", "release_year", "INTEGER");
            addColumnIfMissing(conn, "movies", "country", "TEXT");
            addColumnIfMissing(conn, "movies", "director", "TEXT");
            addColumnIfMissing(conn, "movies", "cast", "TEXT");
            addColumnIfMissing(conn, "movies", "streaming_platform", "TEXT");
            addColumnIfMissing(conn, "movies", "poster_local_path", "TEXT");
            addColumnIfMissing(conn, "movies", "backdrop_path", "TEXT");
            addColumnIfMissing(conn, "movies", "trailer_url", "TEXT");
            addColumnIfMissing(conn, "movies", "popularity_score", "REAL");
            addColumnIfMissing(conn, "movies", "franchise", "TEXT");
            addColumnIfMissing(conn, "movies", "mood_tags", "TEXT");

            // ==== INDEXES ====
            createIndexIfMissing(conn, "idx_movies_title", "movies", "title");
            createIndexIfMissing(conn, "idx_movies_genre", "movies", "genre");
            createIndexIfMissing(conn, "idx_movies_language", "movies", "language");
            createIndexIfMissing(conn, "idx_movies_year", "movies", "release_year");
            createIndexIfMissing(conn, "idx_movies_popularity", "movies", "popularity_score");

            // Seed data if table is empty (the previous implementation already seeded a small set)
            seedIfEmpty(conn);
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
        }
    }

    private static void addColumnIfMissing(Connection conn, String table, String column, String type) throws SQLException {
        if (!columnExists(conn, table, column)) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + column + " " + type);
                System.out.println("Added column " + column + " to " + table);
            }
        }
    }

    private static boolean columnExists(Connection conn, String table, String column) throws SQLException {
        String query = "PRAGMA table_info(" + table + ")";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String name = rs.getString("name");
                if (column.equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void createIndexIfMissing(Connection conn, String indexName, String table, String column) throws SQLException {
        String check = "SELECT name FROM sqlite_master WHERE type='index' AND name='" + indexName + "'";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(check)) {
            if (!rs.next()) {
                try (Statement create = conn.createStatement()) {
                    create.executeUpdate("CREATE INDEX IF NOT EXISTS " + indexName + " ON " + table + "(" + column + ")");
                    System.out.println("Created index " + indexName);
                }
            }
        }
    }

    private static void seedIfEmpty(Connection conn) throws SQLException {
        String countSql = "SELECT COUNT(*) AS cnt FROM movies";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt("cnt") == 0) {
                // Insert a few placeholder rows – real seeding will be done by TMDbSeeder later.
                String insert = "INSERT INTO movies (title, genre, runtime, rating, entertainment_score, poster_url, description, watch_url, language) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insert)) {
                    // Example entry – you can add more if you like.
                    pstmt.setString(1, "Sample Movie");
                    pstmt.setString(2, "Drama");
                    pstmt.setInt(3, 120);
                    pstmt.setDouble(4, 7.5);
                    pstmt.setInt(5, 8);
                    pstmt.setString(6, null);
                    pstmt.setString(7, "A placeholder movie used for initial DB creation.");
                    pstmt.setString(8, null);
                    pstmt.setString(9, "English");
                    pstmt.executeUpdate();
                }
            }
        }
    }
}
