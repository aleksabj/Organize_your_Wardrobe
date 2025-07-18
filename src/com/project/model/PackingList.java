package com.project.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a named packing list containing clothing item IDs.
 */
public class PackingList {
    private int id;
    private String name;
    private List<Integer> clothingItemIds;
    private String coverEmoji;

    // Constructor for creating a new list (no ID yet)
    public PackingList(String name) {
        this.name = name;
        this.clothingItemIds = new ArrayList<>();
        this.coverEmoji = ""; // default empty
    }

    // Overloaded constructor to include emoji
    public PackingList(String name, String coverEmoji) {
        this.name = name;
        this.coverEmoji = coverEmoji;
        this.clothingItemIds = new ArrayList<>();
    }

    // Constructor for loading from database
    public PackingList(int id, String name, List<Integer> clothingItemIds) {
        this.id = id;
        this.name = name;
        this.clothingItemIds = clothingItemIds;
        this.coverEmoji = "";
    }

    // Overloaded constructor with emoji (used when loading from DB)
    public PackingList(int id, String name, List<Integer> clothingItemIds, String coverEmoji) {
        this.id = id;
        this.name = name;
        this.clothingItemIds = clothingItemIds;
        this.coverEmoji = coverEmoji;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getClothingItemIds() {
        return clothingItemIds;
    }

    public String getCoverEmoji() {
        return coverEmoji;
    }

    public void setCoverEmoji(String emoji) {
        this.coverEmoji = emoji;
    }

    public void addClothingItemId(int itemId) {
        if (!clothingItemIds.contains(itemId)) {
            clothingItemIds.add(itemId);
        }
    }

    @Override
    public String toString() {
        return "PackingList{name='" + name + "', emoji='" + coverEmoji + "', items=" + clothingItemIds + '}';
    }
}
