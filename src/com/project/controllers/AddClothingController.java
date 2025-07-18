package com.project.controllers;

import com.project.database.DatabaseHelper;
import com.project.model.ClothingItem;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.geometry.Pos;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class for adding a new item to the wardrobe
 */
public class AddClothingController {
    private final VBox mainVBox; //its reference cannot change after initialization
    private final List<ClothingItem> wardrobe;
    private final MainController mainController;

    public AddClothingController(VBox mainVBox, List<ClothingItem> wardrobe, MainController mainController) {
        this.mainVBox = mainVBox;
        this.wardrobe = wardrobe;
        this.mainController = mainController;
    }

    public void handleAddItem() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.jpeg"));
        fileChooser.setInitialDirectory(new File("clothes"));
        Window window = mainVBox.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(window); //Stores the file the user selects
        if (selectedFile != null) {
            ChoiceBox<String> categoryChoiceBox = new ChoiceBox<>();
            categoryChoiceBox.getItems().addAll(
                    "Jeans", "Pants", "Shorts",
                    "T-Shirt", "Skirt", "Light Jacket", "Winter Jacket",
                    "Heavy Coat", "Sweater", "Shirt", "Dress",
                    "Sneakers", "Sandals", "Boots",
                    "Hat", "Belt", "Socks",
                    "Scarf", "Gloves", "Bag", "Other Accessories"
            );
            categoryChoiceBox.setValue("Jeans");
            categoryChoiceBox.getSelectionModel().selectFirst();
            Label colorLabel = new Label("Choose the colours:");
            Label chosenColoursLabel = new Label("Selected colours: None");
            HBox colourOptions = new HBox(10);
            colourOptions.setAlignment(Pos.CENTER); // Center the color buttons
            List<String> selectedColours = new ArrayList<>();
            String[] colours = {
                    "#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#FFA500", "#800080",
                    "#FFFFFF", "#000000", "#808080", "#FFC0CB", "#ADD8E6", "#FFD700",
                    "#8B0000", "#2E8B57", "#4682B4", "#4B0082", "#DAA520", "#A52A2A"
            };
            String[] colourNames = {
                    "Red", "Green", "Blue", "Yellow", "Orange", "Purple", "White", "Black",
                    "Gray", "Pink", "Light Blue", "Gold", "Dark Red", "Sea Green",
                    "Steel Blue", "Indigo", "Goldenrod", "Brown"
            };
            for (int i = 0; i < colours.length; i++) {
                final int index = i;
                Button colourButton = new Button();
                colourButton.setStyle("-fx-background-color: " + colours[index] + "; -fx-min-width: 30px; -fx-min-height: 30px;");
                colourButton.setOnAction(evt -> {
                    if (selectedColours.contains(colours[index])) {
                        selectedColours.remove(colours[index]);
                    } else {
                        selectedColours.add(colours[index]);
                    }
                    chosenColoursLabel.setText("Selected colours: " + (selectedColours.isEmpty() ? "None" : String.join(", ", selectedColours.stream().map(c -> colourNames[findColourIndex(colours, c)]).toList())));
                });
                colourOptions.getChildren().add(colourButton);
            }
            Button saveButton = new Button("Save");
            saveButton.setOnAction(evt -> {
                String category = categoryChoiceBox.getValue();
                if (selectedColours.isEmpty() || category == null || category.trim().isEmpty()) {
                    chosenColoursLabel.setText("Please select a valid category and at least one colour");
                    return;
                }
                ClothingItem item = new ClothingItem(selectedFile, category, selectedColours);
                wardrobe.add(item);
                saveClothingItemToDatabase(item);
                Label successLabel = new Label("Saved successfully!");
                mainVBox.getChildren().add(successLabel);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        javafx.application.Platform.runLater(() -> mainController.displayWardrobe());
                    }
                }, 500);
            });

            ImageView imageView = new ImageView(new Image(selectedFile.toURI().toString()));
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            mainVBox.getChildren().clear();
            mainVBox.getChildren().addAll(imageView, new Label("Selected Category:"), categoryChoiceBox, colorLabel, colourOptions, chosenColoursLabel, saveButton);
        }
    }

    private int findColourIndex(String[] colours, String colour) {
        for (int i = 0; i < colours.length; i++) {
            if (colours[i].equals(colour)) {
                return i;
            }
        }
        return -1;
    }

    private void saveClothingItemToDatabase(ClothingItem item) {
        String sql = "INSERT INTO clothing (type, color, size, weatherSuitability, imagePath) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, item.getCategory());
            stmt.setString(2, String.join(",", item.getColours()));
            stmt.setString(3, "M"); // default size
            stmt.setString(4, "general"); // placeholder weatherSuitability
            stmt.setString(5, item.getImageFile().getAbsolutePath());
            stmt.executeUpdate();
            System.out.println("Clothing item with imagePath saved to DB.");
        } catch (SQLException e) {
            System.err.println("Error saving clothing: " + e.getMessage());
        }
    }
}
