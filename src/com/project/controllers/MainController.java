package com.project.controllers;

import com.project.database.DatabaseHelper;
import com.project.model.ClothingItem;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class MainController {
    @FXML //tied to a corresponding FXML layout file
    private VBox mainVBox; //the root VBox container in the fxml file
    private List<ClothingItem> wardrobe;

    @FXML
    public void initialize() {
        System.out.println("Welcome to the Virtual Wardrobe Organizer!");
    }

    @FXML
    public void handleStartOrganizing() {
        System.out.println("Start Organizing button clicked!");
        wardrobe = loadClothingItemsFromDatabase();
        displayWardrobe();
    }

    public void displayWardrobe() {
        mainVBox.getChildren().clear();

        Button viewWardrobeButton = new Button("View Your Wardrobe");
        viewWardrobeButton.setStyle("-fx-font-size: 20px; -fx-padding: 10px;");
        viewWardrobeButton.setOnAction(evt -> displayImages(null, null));

        Button createOutfitButton = new Button("Create an Outfit");
        createOutfitButton.setStyle("-fx-font-size: 20px; -fx-padding: 10px;");
        createOutfitButton.setOnAction(evt -> handleSuggestOutfit());

        Button checkWeather = new Button("Suggest Outfit for Weather");
        checkWeather.setStyle("-fx-font-size: 20px; -fx-padding: 10px;");
        checkWeather.setOnAction(evt -> handleSuggestOutfitForWeather());

        Button packingListButton = new Button("Packing List");
        packingListButton.setStyle("-fx-font-size: 20px; -fx-padding: 10px;");
        packingListButton.setOnAction(evt -> handlePackingList());

        Button statsButton = new Button("📊 Stats");
        statsButton.setStyle("-fx-font-size: 20px; -fx-padding: 10px;");
        statsButton.setOnAction(evt -> {
            StatisticsController statsController = new StatisticsController(mainVBox, this);
            statsController.showStatistics();
        });

        Button addClothingItemButton = new Button("+");
        addClothingItemButton.setStyle("-fx-font-size: 20px; -fx-padding: 10px;");
        addClothingItemButton.setOnAction(evt -> handleAddItem());

        HBox row1 = new HBox(10);
        row1.setAlignment(Pos.CENTER);
        row1.getChildren().addAll(viewWardrobeButton, createOutfitButton, checkWeather);

        HBox row2 = new HBox(10);
        row2.setAlignment(Pos.CENTER);
        row2.getChildren().addAll(packingListButton, statsButton, addClothingItemButton);

        VBox buttonLayout = new VBox(15);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.getChildren().addAll(row1, row2);

        mainVBox.getChildren().add(buttonLayout);
    }

    private void handlePackingList() {
        PackingListController controller = new PackingListController(mainVBox, wardrobe, this);
        controller.showPackingListsPage();
    }

    private List<ClothingItem> loadClothingItemsFromDatabase() {
        List<ClothingItem> items = new ArrayList<>();
        String sql = "SELECT * FROM clothing";

        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                File imageFile = new File(rs.getString("imagePath"));
                ClothingItem item = new ClothingItem(
                        imageFile,
                        rs.getString("type"),
                        List.of(rs.getString("color").split(","))
                );
                items.add(item);
            }

        } catch (SQLException e) {
            System.err.println("Error loading wardrobe: " + e.getMessage());
        }

        return items;
    }

    private void handleSuggestOutfit() {
        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(evt -> displayWardrobe());
        OutfitSuggestionController outfitSuggestionController = new OutfitSuggestionController(mainVBox, wardrobe, backButton);
        outfitSuggestionController.handleSuggestOutfit();
    }

    private void handleAddItem() {
        AddClothingController addClothingController = new AddClothingController(mainVBox, wardrobe, this);
        addClothingController.handleAddItem();
    }

    private void displayImages(String categoryFilter, List<String> colourFilter) {
        mainVBox.getChildren().clear();

        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER);

        ChoiceBox<String> categoryChoiceBox = new ChoiceBox<>();
        categoryChoiceBox.getItems().addAll(
                "Jeans", "Pants", "Shorts",
                "T-Shirt", "Skirt", "Light Jacket", "Winter Jacket",
                "Heavy Coat", "Sweater", "Shirt", "Dress",
                "Sneakers", "Sandals", "Boots",
                "Hat", "Belt", "Socks",
                "Scarf", "Gloves", "Bag", "Other Accessories"
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

        filterBox.getChildren().addAll(
                new Label("Category:"), categoryChoiceBox,
                new Label("Colour:"), colourChoiceBox,
                applyFilterButton
        );

        List<ClothingItem> filteredItems = wardrobe.stream()
                .filter(item -> (categoryFilter == null || item.getCategory().equals(categoryFilter)))
                .filter(item -> {
                    if (colourFilter == null || colourFilter.isEmpty()) {
                        return true;
                    }
                    List<String> selectedColorsHex = colourFilter.stream()
                            .map(this::mapColorNameToHex)
                            .toList();
                    return item.getColours().stream().anyMatch(selectedColorsHex::contains);
                })
                .toList();

        HBox imagesHBox = new HBox(10);
        imagesHBox.setAlignment(Pos.CENTER);

        for (ClothingItem item : filteredItems) {
            ImageView imageView = new ImageView(new Image(item.getImageFile().toURI().toString()));
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            imagesHBox.getChildren().add(imageView);
        }

        ScrollPane scrollPane = new ScrollPane(imagesHBox);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(evt -> displayWardrobe());

        container.getChildren().addAll(filterBox, scrollPane, backButton);
        mainVBox.getChildren().add(container);
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

    private void handleSuggestOutfitForWeather() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("City Input");
        dialog.setHeaderText("Enter your city");
        dialog.setContentText("City:");
        dialog.showAndWait().ifPresent(city -> {
            if (!city.trim().isEmpty()) {
                String weatherSummary = Weather.getWeatherSummary(city.trim());
                if (!weatherSummary.isEmpty()) {
                    displayWeatherSummaryAndOutfit(weatherSummary, city.trim());
                } else {
                    showError("The city is not found. You entered \"" + city + "\". Please try again.");
                }
            } else {
                showError("City name is invalid. Please try again.");
            }
        });
    }

    private void showError(String message) {
        Label errorLabel = new Label(message);
        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(evt -> displayWardrobe());
        mainVBox.getChildren().clear();
        mainVBox.getChildren().addAll(errorLabel, backButton);
    }

    private void displayWeatherSummaryAndOutfit(String weatherSummary, String city) {
        mainVBox.getChildren().clear();
        Label weatherLabel = new Label("Weather Summary: " + weatherSummary);
        weatherLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        String temperature = extractTemperatureFromSummary(weatherSummary);
        String outfitSuggestion = suggestOutfitBasedOnTemperature(temperature);
        Label outfitLabel = new Label("Based on this, we suggest you wear: " + outfitSuggestion);
        outfitLabel.setStyle("-fx-font-size: 14px;");

        if (temperature.equals("unavailable") || temperature.equals("-")) {
            displayImagesForWeather("-");
        } else {
            displayImagesForWeather(Double.parseDouble(temperature));
        }
        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(evt -> displayWardrobe());

        VBox suggestionBox = new VBox(10);
        suggestionBox.setAlignment(Pos.CENTER);
        suggestionBox.getChildren().addAll(weatherLabel, outfitLabel, backButton);
        mainVBox.getChildren().add(suggestionBox);
    }

    private String extractTemperatureFromSummary(String weatherSummary) {
        try {
            String searchString = "current temperature: ";
            int tempStartIndex = weatherSummary.indexOf(searchString) + searchString.length();
            int tempEndIndex = weatherSummary.indexOf("°C", tempStartIndex);
            if (tempStartIndex > 0 && tempEndIndex > tempStartIndex) {
                String tempStr = weatherSummary.substring(tempStartIndex, tempEndIndex).trim();
                Double.parseDouble(tempStr); // ensure it is a valid number
                return tempStr; // return the temperature value as a string
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "unavailable";
    }

    private String suggestOutfitBasedOnTemperature(String temperature) {
        try {
            double tempValue = Double.parseDouble(temperature); //validate and use the numeric value
            if (tempValue >= 30) {
                return "a T-shirt, Shorts, and Sandals";
            } else if (tempValue >= 20) {
                return "a T-shirt, Jeans, and Sneakers";
            } else if (tempValue >= 10) {
                return "a Sweater, Pants, and a Light Jacket";
            } else if (tempValue >= 0) {
                return "a Winter Jacket, Sweater, Boots, and Gloves";
            } else {
                return "a Heavy Coat, Winter Boots, Scarf, and Gloves";
            }
        } catch (NumberFormatException e) {
            return "Weather data unavailable, so dress comfortably for uncertain conditions";
        }
    }

    private void displayImagesForWeather(Object temperature) {
        mainVBox.getChildren().clear();

        VBox imagesContainer = new VBox(10);
        imagesContainer.setAlignment(Pos.CENTER);

        Label temperatureLabel = new Label("Temperature: " + temperature + (temperature.equals("-") ? "" : "°C"));
        temperatureLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        List<ClothingItem> filteredItems;
        if (temperature.equals("-")) {
            filteredItems = wardrobe; // no filtering for unavailable temperature
        } else {
            double tempValue = (double) temperature;
            filteredItems = wardrobe.stream()
                    .filter(item -> {
                        String category = item.getCategory();
                        if (tempValue >= 30) {
                            return List.of("T-Shirt", "Shorts", "Shirt", "Sandals", "Pants", "Dress", "Hat").contains(category);
                        } else if (tempValue >= 20) {
                            return List.of("T-Shirt", "Jeans", "Shirt", "Skirt", "Dress", "Sneakers", "Pants").contains(category);
                        } else if (tempValue >= 10) {
                            return List.of("Sweater", "Pants", "Light Jacket").contains(category);
                        } else if (tempValue >= 0) {
                            return List.of("Winter Jacket", "Sweater", "Boots", "Gloves").contains(category);
                        } else {
                            return List.of("Heavy Coat", "Boots", "Scarf", "Gloves").contains(category);
                        }
                    })
                    .toList();
        }

        HBox imagesHBox = new HBox(10);
        imagesHBox.setAlignment(Pos.CENTER);
        for (ClothingItem item : filteredItems) {
            ImageView imageView = new ImageView(new Image(item.getImageFile().toURI().toString()));
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            imagesHBox.getChildren().add(imageView);
        }

        if (filteredItems.isEmpty()) {
            Label noItemsLabel = new Label("No matching wardrobe items found for the current weather.");
            noItemsLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
            imagesHBox.getChildren().add(noItemsLabel);
        }

        ScrollPane scrollPane = new ScrollPane(imagesHBox);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        mainVBox.getChildren().addAll(temperatureLabel, scrollPane);
    }
}
