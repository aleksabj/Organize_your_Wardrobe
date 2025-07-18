package com.project.model;

/**
 * Represents a connection between a clothing item and a packing list.
 */
public class PackingListItem {
    private int listId;
    private int clothingItemId;

    public PackingListItem(int listId, int clothingItemId) {
        this.listId = listId;
        this.clothingItemId = clothingItemId;
    }

    public int getListId() {
        return listId;
    }

    public int getClothingItemId() {
        return clothingItemId;
    }
}
