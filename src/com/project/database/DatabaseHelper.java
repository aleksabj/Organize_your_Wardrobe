package com.project.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Helper class for managing SQLite database connection and initialization.
 */
public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:wardrobe.db";

    /**
     * Establishes and returns a connection to the SQLite database
     * Also prints the path being used for debugging
     */
    public static Connection connect() throws SQLException {
        File dbFile = new File("wardrobe.db");
        System.out.println("Using database at: " + dbFile.getAbsolutePath());
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * Initializes the database by forcibly recreating the clothing table
     * Ensures schema is always correct during development
     */
    public static void initializeDatabase() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            String sql = """
            CREATE TABLE IF NOT EXISTS clothing (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                type TEXT NOT NULL,
                color TEXT NOT NULL,
                size TEXT,
                weatherSuitability TEXT,
                imagePath TEXT
            );
        """;

            stmt.execute(sql);
            System.out.println("Database initialized (persistent).");
        } catch (SQLException e) {
            System.err.println("DB Init Failed: " + e.getMessage());
        }
    }

}
