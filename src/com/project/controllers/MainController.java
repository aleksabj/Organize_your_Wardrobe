package com.project.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.ScrollPane;
import com.project.model.ClothingItem;

/**
 * controller class for handling the main interactions in the Virtual Wardrobe Organizer
 */

public class MainController {
    @FXML //tied to a corresponding FXML layout file
    private VBox mainVBox; //the root VBox container in the fxml file

    private final List<ClothingItem> wardrobe = new ArrayList<>();

    @FXML
    public void initialize() {
        System.out.println("Welcome to the Virtual Wardrobe Organizer!");
    }

    @FXML
    public void handleStartOrganizing() {
        System.out.println("Start Organizing button clicked!");
        displayWardrobe();
    }

    /**
     * displays the main wardrobe options on the screen
     */
    public void displayWardrobe() {
        // clear the main VBox
        mainVBox.getChildren().clear();
        //buttons on the main page
        Button viewWardrobeButton = new Button("View Your Wardrobe");
        viewWardrobeButton.setStyle("-fx-font-size: 20px; -fx-padding: 10px;");

        Button addClothingItemButton = new Button("+");
        addClothingItemButton.setStyle("-fx-font-size: 20px; -fx-padding: 10px;");

        Button createOutfitButton = new Button("Create an Outfit");
        createOutfitButton.setStyle("-fx-font-size: 20px; -fx-padding: 10px;");
        createOutfitButton.setOnAction(evt -> handleSuggestOutfit());

        Button checkWeather = new Button("Suggest Outfit for Weather");
        checkWeather.setStyle("-fx-font-size: 20px; -fx-padding: 10px;");
        checkWeather.setOnAction(evt -> handleSuggestOutfitForWeather());

        //buttons placed horizontally
        HBox wardrobeOptions = new HBox(10);
        wardrobeOptions.setAlignment(Pos.CENTER);
        wardrobeOptions.getChildren().addAll(viewWardrobeButton, createOutfitButton, checkWeather, addClothingItemButton);

        //add HBox to the main VBox
        mainVBox.getChildren().add(wardrobeOptions); //add buttons to main UI
        addClothingItemButton.setOnAction(evt -> handleAddItem()); //assign actions foe each button
        viewWardrobeButton.setOnAction(evt -> displayImages(null, null)); //show wardrobe without filters
    }

    private void handleSuggestOutfit() {
        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(evt -> displayWardrobe());
        OutfitSuggestionController outfitSuggestionController = new OutfitSuggestionController(mainVBox, wardrobe, backButton);
        outfitSuggestionController.handleSuggestOutfit(); // Delegates suggesting outfit functionality.
    }

    private void handleAddItem() {
        AddClothingController addClothingController = new AddClothingController(mainVBox, wardrobe, this);
        addClothingController.handleAddItem(); // Delegates adding clothing functionality.
    }

    /**
     *displays images of clothing items in the wardrobe, optionally filtered by category and colour
     * @param categoryFilter the category to filter by, or null for no filter
     * @param colourFilter the colors to filter by, or null for no filter
     */

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

        //filtering logic
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

        //display images in an HBox
        HBox imagesHBox = new HBox(10);
        imagesHBox.setAlignment(Pos.CENTER);

        for (ClothingItem item : filteredItems) { //Loops through the filtered clothing items.
            ImageView imageView = new ImageView(new Image(item.getImageFile().toURI().toString())); //Converts the item's image file URI to a string and creates an image object.
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            imagesHBox.getChildren().add(imageView);
        }

        //wrap the HBox in a ScrollPane
        ScrollPane scrollPane = new ScrollPane(imagesHBox);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        //add back button
        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(evt -> displayWardrobe());

        //add all components to the main container
        container.getChildren().addAll(filterBox, scrollPane, backButton);
        mainVBox.getChildren().add(container);
    }


    /**
     * Maps a color name to its corresponding hex code
     * @param colorName the name of the color
     * @return the hex code of the color
     */

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
                // fetch weather summary from the weather class
                String weatherSummary = Weather.getWeatherSummary(city.trim());
                if (!weatherSummary.isEmpty()) {
                    // City found, display weather summary and outfit
                    displayWeatherSummaryAndOutfit(weatherSummary, city.trim());
                } else {
                    //city was not found
                    Label errorLabel = new Label("The city is not found. You entered \"" + city + "\", maybe you miswrote it. Check and try once again.");
                    Button backButton = new Button("Back to Main Menu");
                    backButton.setOnAction(evt -> displayWardrobe());
                    mainVBox.getChildren().clear();
                    mainVBox.getChildren().addAll(errorLabel, backButton);
                }
            } else {
                // empty city name
                Label errorLabel = new Label("City name is invalid. Please try again.");
                Button backButton = new Button("Back to Main Menu");
                backButton.setOnAction(evt -> displayWardrobe());
                mainVBox.getChildren().clear();
                mainVBox.getChildren().addAll(errorLabel, backButton);
            }
        });
    }

    private void displayWeatherSummaryAndOutfit(String weatherSummary, String city) {
        mainVBox.getChildren().clear();
        // display the full weather summary
        Label weatherLabel = new Label("Weather Summary: " + weatherSummary);
        weatherLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        // extract temperature as string
        String temperature = extractTemperatureFromSummary(weatherSummary);
        // suggest outfit based on temperature
        String outfitSuggestion = suggestOutfitBasedOnTemperature(temperature);
        // display the outfit suggestion
        Label outfitLabel = new Label("Based on this, we suggest you wear: " + outfitSuggestion);
        outfitLabel.setStyle("-fx-font-size: 14px;");

        // show items in the wardrobe based on temperature
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
    /**
     *extracts the temperature from the weather summary
     * @param weatherSummary the weather summary string
     * @return the temperature as a string
     */
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

    /**
     * Suggests an outfit based on the temperature
     * @param temperature the temperature as a string
     * @return the suggested outfit
     */

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

    /**
     * displays images of clothes that are good for the given temperature.
     * @param temperature the temperature as an object (can be a double or a string)
     */
    private void displayImagesForWeather(Object temperature) {
        mainVBox.getChildren().clear();

        VBox imagesContainer = new VBox(10);
        imagesContainer.setAlignment(Pos.CENTER);

        Label temperatureLabel;
        if (temperature.equals("-")) {
            temperatureLabel = new Label("Temperature: -");
        } else {
            temperatureLabel = new Label("Temperature: " + temperature + "°C");
        }
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
                            return category.equals("T-Shirt") || category.equals("Shorts") || category.equals("Shirt") || category.equals("Sandals") || category.equals("Pants") || category.equals("Dress") || category.equals("Hat");
                        } else if (tempValue >= 20) {
                            return category.equals("T-Shirt") || category.equals("Jeans") || category.equals("Shirt") || category.equals("Skirt") || category.equals("Dress") || category.equals("Sneakers") || category.equals("Pants");
                        } else if (tempValue >= 10) {
                            return category.equals("Sweater") || category.equals("Pants") || category.equals("Light Jacket");
                        } else if (tempValue >= 0) {
                            return category.equals("Winter Jacket") || category.equals("Sweater") || category.equals("Boots") || category.equals("Gloves");
                        } else {
                            return category.equals("Heavy Coat") || category.equals("Boots") || category.equals("Scarf") || category.equals("Gloves");
                        }
                    })
                    .toList();
        }

        HBox imagesHBox = new HBox(10);
        imagesHBox.setAlignment(Pos.CENTER);
        //Loops through filtered clothing items and displays their images in an HBox
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