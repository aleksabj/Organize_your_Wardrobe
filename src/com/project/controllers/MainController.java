package com.project.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.project.model.ClothingItem;

public class MainController {
    @FXML
    private VBox mainVBox; //the root VBox container in the fxml file

    private final List<ClothingItem> wardrobe = new ArrayList<>();

    @FXML
    public void initialize(){
        System.out.println("Welcome to the Virtual Wardrobe Organizer!");
    }
    @FXML
    public void handleStartOrganizing() {
        System.out.println("Start Organizing button clicked!");
        displayWardrobe();
    }
    public void displayWardrobe() {
        //clear the main Vbox
        mainVBox.getChildren().clear();
        //buttons on the main page
        Button viewWardrobeButton = new Button("View Your Wardrobe");
        viewWardrobeButton.setStyle("-fx-font-size: 20px; -fx-padding: 10px;");

        Button addClothingItemButton = new Button("+");
        addClothingItemButton.setStyle("-fx-font-size: 20px; -fx-padding: 10px;");

        Button createOutfitButton = new Button("Create an Outfit");
        createOutfitButton.setStyle("-fx-font-size: 20px; -fx-padding: 10px;");

        Button checkWeather = new Button("Suggest Outfit for Weather");
        checkWeather.setStyle("-fx-font-size: 20px; -fx-padding: 10px;");

        //buttons placed horizontally
        HBox wardrobeOptions = new HBox(10);
        wardrobeOptions.setAlignment(Pos.CENTER);
        wardrobeOptions.getChildren().addAll(viewWardrobeButton, createOutfitButton, addClothingItemButton, checkWeather);

        //add HBox to the main VBox
        mainVBox.getChildren().add(wardrobeOptions);
        addClothingItemButton.setOnAction(evt -> handleAddItem());
        viewWardrobeButton.setOnAction(evt -> displayImages(null, null)); //show wardrobe without filters
    }

    private void handleAddItem() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.jpeg"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        Window window = mainVBox.getScene().getWindow(); //get curr window
        File selectedFile = fileChooser.showOpenDialog(window); //open file dialog
        if (selectedFile != null) {
            ChoiceBox<String> categoryChoiceBox = new ChoiceBox<>();
            categoryChoiceBox.getItems().addAll(
                            "Jeans", "Pants", "Shorts",
                                 "T-Shirt", "Skirt", "Jacket",
                                 "Sweater", "Shirt", "Dress",
                                 "Shoes", "Hats", "Belts",
                                 "Socks", "Scarves", "Gloves",
                                 "Bags", "Other Accessories"
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
                displayWardrobe();
            });
            //update UI
            mainVBox.getChildren().clear();
            mainVBox.getChildren().addAll(new Label("File: " +  selectedFile.getName()), new Label("Selected Category:"), categoryChoiceBox, colorLabel, colourOptions, chosenColoursLabel, saveButton);
        }
    }
    private void displayImages(String categoryFilter, List<String> colourFilter) {
        mainVBox.getChildren().clear();

        VBox imagesContainer = new VBox(10);
        imagesContainer.setAlignment(Pos.CENTER);
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER);

        ChoiceBox<String> categoryChoiceBox = new ChoiceBox<>();
        categoryChoiceBox.getItems().addAll(
                "Jeans", "Pants", "Shorts",
                "T-Shirt", "Skirt", "Jacket",
                "Sweater", "Shirt", "Dress",
                "Shoes", "Hats", "Belts",
                "Socks", "Scarves", "Gloves",
                "Bags", "Other Accessories"
        );
        categoryChoiceBox.setValue("All Categories");

        ChoiceBox<String> colourChoiceBox = new ChoiceBox<>();
        colourChoiceBox.getItems().add("All Colours");
        colourChoiceBox.getItems().addAll(
                "Red", "Green", "Blue", "Yellow", "Orange",
                "Purple", "White", "Black", "Gray", "Pink",
                "Light Blue", "Gold", "Dark Red", "Sea Green",
                "Steel Blue", "Indigo", "Goldenrod", "Brown"
        );
        colourChoiceBox.setValue("All Colours");

        Button applyFilterButton = new Button("Apply Filter");
        applyFilterButton.setOnAction(evt -> {
            String selectedCategory = categoryChoiceBox.getValue();
            String selectedColour = colourChoiceBox.getValue();
            displayImages(
                    selectedCategory.equals("All Categories") ? null : selectedCategory,
                    selectedColour.equals("All Colours") ? null : List.of(selectedColour)
            );
        });

        filterBox.getChildren().addAll(new Label("Category:"), categoryChoiceBox, new Label("Colour:"), colourChoiceBox, applyFilterButton);

        //filtering logic
        List<ClothingItem> filteredItems = wardrobe.stream()
                .filter(item -> (categoryFilter == null || item.getCategory().equals(categoryFilter))) //filter by category
                .filter(item -> {
                    if (colourFilter == null || colourFilter.isEmpty()) {
                        return true; //no color filter applied
                    }
                    //map the user-selected color names to the corresponding hex codes
                    List<String> selectedColorsHex = colourFilter.stream()
                            .map(this::mapColorNameToHex)
                            .collect(Collectors.toList());
                    //check if any of the item's colors match the selected hex codes
                    return item.getColours().stream().anyMatch(selectedColorsHex::contains);
                })
                .collect(Collectors.toList());

        for (ClothingItem item : filteredItems) {
            ImageView imageView = new ImageView(new Image(item.getImageFile().toURI().toString()));
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            imagesContainer.getChildren().add(imageView);
        }

        mainVBox.getChildren().addAll(filterBox, imagesContainer);
    }

    private String mapColorNameToHex(String colorName) {
        String[] colorNames = {
                "Red", "Green", "Blue", "Yellow", "Orange",
                "Purple", "White", "Black", "Gray", "Pink",
                "Light Blue", "Gold", "Dark Red", "Sea Green",
                "Steel Blue", "Indigo", "Goldenrod", "Brown"
        };
        String[] hexCodes = {
                "#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#FFA500",
                "#800080", "#FFFFFF", "#000000", "#808080", "#FFC0CB",
                "#ADD8E6", "#FFD700", "#8B0000", "#2E8B57", "#4682B4",
                "#4B0082", "#DAA520", "#A52A2A"
        };

        for (int i = 0; i < colorNames.length; i++) {
            if (colorNames[i].equalsIgnoreCase(colorName)) {
                return hexCodes[i];
            }
        }
        return null;
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
