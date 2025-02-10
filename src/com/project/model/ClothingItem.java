package com.project.model;

import java.io.File;
import java.util.List;

/**
 * Class representing a clothing item in the wardrobe
 */
public class ClothingItem {
    private File imageFile;
    private String category;
    private List<String> colours;

    /**
     * Constructor for ClothingItem
     *
     * @param imageFile the image file of the clothing item
     * @param category the category of the clothing item
     * @param colours the list of colours of the clothing item
     */
    public ClothingItem(File imageFile, String category, List<String> colours) {
        this.imageFile = imageFile;
        this.category = category;
        this.colours = colours;
    }
    public File getImageFile() {
        return imageFile;
    }
    public String getCategory() {
        return category;
    }
    public List<String> getColours() {
        return colours;
    }

    @Override
    public String toString() {
        return "Category: " + category + ", Colours: " + colours + ", Image: " + imageFile.getName();
    }
}
