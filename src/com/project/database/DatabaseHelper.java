package com.project.database;

import com.project.model.PackingList;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for managing SQLite database connection and initialization.
 */
public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:wardrobe.db";

    public static Connection connect() throws SQLException {
        File dbFile = new File("wardrobe.db");
        System.out.println("Using database at: " + dbFile.getAbsolutePath());
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS clothing (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    type TEXT NOT NULL,
                    color TEXT NOT NULL,
                    size TEXT,
                    weatherSuitability TEXT,
                    imagePath TEXT
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS packing_lists (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    coverEmoji TEXT
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS packing_list_items (
                    list_id INTEGER NOT NULL,
                    clothing_item_id INTEGER NOT NULL,
                    FOREIGN KEY(list_id) REFERENCES packing_lists(id),
                    FOREIGN KEY(clothing_item_id) REFERENCES clothing(id)
                );
            """);

            System.out.println("Database initialized (clothing + packing list tables).");
        } catch (SQLException e) {
            System.err.println("DB Init Failed: " + e.getMessage());
        }
    }

    // --- Packing List DB Operations ---

    public static void savePackingList(PackingList list) {
        try (Connection conn = connect()) {
            conn.setAutoCommit(false);

            String insertListSQL = "INSERT INTO packing_lists (name, coverEmoji) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertListSQL, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, list.getName());
                ps.setString(2, list.getCoverEmoji());
                ps.executeUpdate();
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int listId = generatedKeys.getInt(1);

                    // insert clothing item links
                    String insertItemSQL = "INSERT INTO packing_list_items (list_id, clothing_item_id) VALUES (?, ?)";
                    try (PreparedStatement itemStmt = conn.prepareStatement(insertItemSQL)) {
                        for (int itemId : list.getClothingItemIds()) {
                            itemStmt.setInt(1, listId);
                            itemStmt.setInt(2, itemId);
                            itemStmt.addBatch();
                        }
                        itemStmt.executeBatch();
                    }
                }
            }

            conn.commit();
            System.out.println("Packing list saved to DB.");
        } catch (SQLException e) {
            System.err.println("Error saving packing list: " + e.getMessage());
        }
    }

    public static List<PackingList> loadPackingLists() {
        List<PackingList> lists = new ArrayList<>();
        try (Connection conn = connect()) {
            String queryLists = "SELECT * FROM packing_lists";
            try (PreparedStatement ps = conn.prepareStatement(queryLists)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int listId = rs.getInt("id");
                    String name = rs.getString("name");
                    String emoji = rs.getString("coverEmoji");

                    List<Integer> itemIds = new ArrayList<>();
                    try (PreparedStatement itemStmt = conn.prepareStatement("SELECT clothing_item_id FROM packing_list_items WHERE list_id = ?")) {
                        itemStmt.setInt(1, listId);
                        ResultSet itemRs = itemStmt.executeQuery();
                        while (itemRs.next()) {
                            itemIds.add(itemRs.getInt("clothing_item_id"));
                        }
                    }

                    lists.add(new PackingList(listId, name, itemIds, emoji));
                }
            }
            System.out.println("Loaded " + lists.size() + " packing list(s) from DB.");
        } catch (SQLException e) {
            System.err.println("Error loading packing lists: " + e.getMessage());
        }

        return lists;
    }
}
