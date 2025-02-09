package com.project.controllers;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import com.project.model.ClothingItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddClothingController {
    private final VBox mainVBox;
    private final List<ClothingItem> wardrobe;

    public AddClothingController(VBox mainVBox, List<ClothingItem> wardrobe) {
        this.mainVBox = mainVBox;
        this.wardrobe = wardrobe;
    }

    public void handleAddItem() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.jpeg"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        Window window = mainVBox.getScene().getWindow(); //get curr window
        File selectedFile = fileChooser.showOpenDialog(window); //open file dialog
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
            categoryChoiceBox.setValue("Jeans"); //default category
            categoryChoiceBox.getSelectionModel().selectFirst();
            Label colorLabel = new Label("Choose the colours:");
            Label chosenColoursLabel = new Label("Selected colours: None");
            HBox colourOptions = new HBox(10);
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
                wardrobe.add(new ClothingItem(selectedFile, category, selectedColours));
                chosenColoursLabel.setText("Clothing item saved successfully.");
                MainController mainController = new MainController();
                mainController.displayWardrobe();
            });
            //update UI
            mainVBox.getChildren().clear();
            mainVBox.getChildren().addAll(new Label("File: " +  selectedFile.getName()), new Label("Selected Category:"), categoryChoiceBox, colorLabel, colourOptions, chosenColoursLabel, saveButton);
        }
    }

    //helper method to find the index of the colour
    private int findColourIndex(String[] colours, String colour) {
        for (int i = 0; i < colours.length; i++) {
            if (colours[i].equals(colour)) {
                return i;
            }
        }
        return -1; //not found
    }
}