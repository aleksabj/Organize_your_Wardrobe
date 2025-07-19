package com.project.database;

import com.project.model.PackingList;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


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


    // --- Statistics Queries ---

    public static int getTotalPackingLists() {
        String sql = "SELECT COUNT(*) FROM packing_lists";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total packing lists: " + e.getMessage());
        }
        return 0;
    }

    public static double getAverageItemsPerPackingList() {
        String sql = """
        SELECT AVG(item_count) FROM (
            SELECT COUNT(*) AS item_count 
            FROM packing_list_items 
            GROUP BY list_id
        );
    """;
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error calculating average items per packing list: " + e.getMessage());
        }
        return 0;
    }


    public static String getMostCommonColor() {
        String sql = """
        SELECT color, COUNT(*) AS count
        FROM clothing
        GROUP BY color
        ORDER BY count DESC
        LIMIT 1;
    """;
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("color");
            }
        } catch (SQLException e) {
            System.err.println("Error getting most common color: " + e.getMessage());
        }
        return "N/A";
    }


    // Count clothing items by category/type
    public static Map<String, Integer> getItemCountsByCategory() {
        Map<String, Integer> counts = new HashMap<>();
        String sql = "SELECT type, COUNT(*) as count FROM clothing GROUP BY type";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                counts.put(rs.getString("type"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.err.println("Error counting items by category: " + e.getMessage());
        }

        return counts;
    }

    // Count clothing items by color
    public static Map<String, Integer> getColorCounts() {
        Map<String, Integer> counts = new HashMap<>();
        String sql = "SELECT color FROM clothing";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String[] colors = rs.getString("color").split(",");
                for (String color : colors) {
                    color = color.trim();
                    counts.put(color, counts.getOrDefault(color, 0) + 1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting colors: " + e.getMessage());
        }

        return counts;
    }


}
