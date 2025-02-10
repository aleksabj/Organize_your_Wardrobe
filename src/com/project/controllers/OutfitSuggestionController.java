package com.project.controllers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import com.project.model.ClothingItem;

import java.util.*;
import java.util.stream.Collectors;

public class OutfitSuggestionController {
    private final VBox mainVBox;
    private final List<ClothingItem> wardrobe;
    private final Button backButton;

    private static final Map<String, String> COLOR_NAME_TO_HEX = Map.ofEntries(
            Map.entry("Red", "#FF0000"),
            Map.entry("Green", "#00FF00"),
            Map.entry("Blue", "#0000FF"),
            Map.entry("Yellow", "#FFFF00"),
            Map.entry("Orange", "#FFA500"),
            Map.entry("Purple", "#800080"),
            Map.entry("White", "#FFFFFF"),
            Map.entry("Black", "#000000"),
            Map.entry("Gray", "#808080"),
            Map.entry("Pink", "#FFC0CB"),
            Map.entry("Light Blue", "#ADD8E6"),
            Map.entry("Gold", "#FFD700"),
            Map.entry("Dark Red", "#8B0000"),
            Map.entry("Sea Green", "#2E8B57"),
            Map.entry("Steel Blue", "#4682B4"),
            Map.entry("Indigo", "#4B0082"),
            Map.entry("Goldenrod", "#DAA520"),
            Map.entry("Brown", "#A52A2A")
    );

    private static final Map<String, Map<String, List<String>>> COLOR_COMBINATIONS = Map.ofEntries(
            Map.entry("Red", Map.of(
                    "Complementary", List.of("#00FF00"),
                    "Analogous", List.of("#FF0000", "#FFA500", "#FFFF00"),
                    "Monochromatic", List.of("#FF0000", "#8B0000", "#FFC0CB")
            )),
            Map.entry("Green", Map.of(
                    "Complementary", List.of("#FF0000"),
                    "Analogous", List.of("#00FF00", "#2E8B57", "#ADD8E6"),
                    "Monochromatic", List.of("#00FF00", "#2E8B57", "#ADD8E6")
            )),
            Map.entry("Blue", Map.of(
                    "Complementary", List.of("#FFA500"),
                    "Analogous", List.of("#0000FF", "#4682B4", "#4B0082"),
                    "Monochromatic", List.of("#0000FF", "#4682B4", "#4B0082")
            )),
            Map.entry("Yellow", Map.of(
                    "Complementary", List.of("#800080"),
                    "Analogous", List.of("#FFFF00", "#FFD700", "#FFA500"),
                    "Monochromatic", List.of("#FFFF00", "#FFD700", "#FFA500")
            )),
            Map.entry("Orange", Map.of(
                    "Complementary", List.of("#0000FF"),
                    "Analogous", List.of("#FFA500", "#FF0000", "#FFD700"),
                    "Monochromatic", List.of("#FFA500", "#FFD700", "#FF0000")
            )),
            Map.entry("Purple", Map.of(
                    "Complementary", List.of("#FFFF00"),
                    "Analogous", List.of("#800080", "#4B0082", "#FF0000"),
                    "Monochromatic", List.of("#800080", "#4B0082", "#FF0000")
            )),
            Map.entry("White", Map.of(
                    "Complementary", List.of("#000000"),
                    "Analogous", List.of("#FFFFFF", "#ADD8E6", "#FFC0CB"),
                    "Monochromatic", List.of("#FFFFFF", "#ADD8E6", "#FFC0CB")
            )),
            Map.entry("Black", Map.of(
                    "Complementary", List.of("#FFFFFF"),
                    "Analogous", List.of("#000000", "#808080", "#8B0000"),
                    "Monochromatic", List.of("#000000", "#808080", "#8B0000")
            )),
            Map.entry("Gray", Map.of(
                    "Complementary", List.of("#FFC0CB"),
                    "Analogous", List.of("#808080", "#000000", "#FFFFFF"),
                    "Monochromatic", List.of("#808080", "#000000", "#FFFFFF")
            )),
            Map.entry("Pink", Map.of(
                    "Complementary", List.of("#808080"),
                    "Analogous", List.of("#FFC0CB", "#FF0000", "#FFD700"),
                    "Monochromatic", List.of("#FFC0CB", "#FF0000", "#FFD700")
            )),
            Map.entry("Light Blue", Map.of(
                    "Complementary", List.of("#FFD700"),
                    "Analogous", List.of("#ADD8E6", "#4682B4", "#0000FF"),
                    "Monochromatic", List.of("#ADD8E6", "#4682B4", "#0000FF")
            )),
            Map.entry("Gold", Map.of(
                    "Complementary", List.of("#ADD8E6"),
                    "Analogous", List.of("#FFD700", "#FFFF00", "#FFA500"),
                    "Monochromatic", List.of("#FFD700", "#FFFF00", "#FFA500")
            )),
            Map.entry("Dark Red", Map.of(
                    "Complementary", List.of("#2E8B57"),
                    "Analogous", List.of("#8B0000", "#A52A2A", "#FF0000"),
                    "Monochromatic", List.of("#8B0000", "#A52A2A", "#FF0000")
            )),
            Map.entry("Sea Green", Map.of(
                    "Complementary", List.of("#8B0000"),
                    "Analogous", List.of("#2E8B57", "#00FF00", "#ADD8E6"),
                    "Monochromatic", List.of("#2E8B57", "#00FF00", "#ADD8E6")
            )),
            Map.entry("Steel Blue", Map.of(
                    "Complementary", List.of("#DAA520"),
                    "Analogous", List.of("#4682B4", "#ADD8E6", "#0000FF"),
                    "Monochromatic", List.of("#4682B4", "#ADD8E6", "#0000FF")
            )),
            Map.entry("Indigo", Map.of(
                    "Complementary", List.of("#A52A2A"),
                    "Analogous", List.of("#4B0082", "#800080", "#0000FF"),
                    "Monochromatic", List.of("#4B0082", "#800080", "#0000FF")
            )),
            Map.entry("Goldenrod", Map.of(
                    "Complementary", List.of("#4682B4"),
                    "Analogous", List.of("#DAA520", "#FFD700", "#FFA500"),
                    "Monochromatic", List.of("#DAA520", "#FFD700", "#FFA500")
            )),
            Map.entry("Brown", Map.of(
                    "Complementary", List.of("#4B0082"),
                    "Analogous", List.of("#A52A2A", "#8B0000", "#DAA520"),
                    "Monochromatic", List.of("#A52A2A", "#8B0000", "#DAA520")
            ))
    );

    public OutfitSuggestionController(VBox mainVBox, List<ClothingItem> wardrobe, Button backButton) {
        this.mainVBox = mainVBox;
        this.wardrobe = wardrobe;
        this.backButton = backButton;

        System.out.println("Wardrobe initialized with " + wardrobe.size() + " items.");
        for (ClothingItem item : wardrobe) {
            System.out.println("Item: " + item.getCategory() + ", Colors: " + item.getColours());
        }
    }

    public void handleSuggestOutfit() {
        mainVBox.getChildren().clear();

        Label titleLabel = new Label("What would you like to wear today?");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-padding: 10px;");

        ChoiceBox<String> itemChoiceBox = createItemChoiceBox();
        ChoiceBox<String> colorChoiceBox = createColorChoiceBox();
        ChoiceBox<String> combinationChoiceBox = createCombinationChoiceBox();

        Button generateButton = new Button("Generate Outfit");
        generateButton.setOnAction(evt -> generateOutfit(itemChoiceBox.getValue(), colorChoiceBox.getValue(), combinationChoiceBox.getValue()));

        VBox optionsBox = new VBox(10);
        optionsBox.setAlignment(Pos.CENTER);
        optionsBox.getChildren().addAll(
                new Label("Choose the item you want to wear:"), itemChoiceBox,
                new Label("Choose the colour you want to wear:"), colorChoiceBox,
                new Label("Choose the color combination type:"), combinationChoiceBox
        );

        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        VBox.setMargin(generateButton, new Insets(40, 0, 0, 0));
        buttonBox.getChildren().addAll(generateButton, backButton);

        mainVBox.setAlignment(Pos.CENTER);
        mainVBox.getChildren().addAll(titleLabel, optionsBox, buttonBox);
    }

    private ChoiceBox<String> createItemChoiceBox() {
        ChoiceBox<String> itemChoiceBox = new ChoiceBox<>();
        itemChoiceBox.getItems().addAll(
                "Jeans", "Pants", "Shorts", "T-Shirt", "Skirt", "Light Jacket", "Winter Jacket",
                "Heavy Coat", "Sweater", "Shirt", "Dress", "Sneakers", "Sandals", "Boots",
                "Hat", "Belt", "Socks", "Scarf", "Gloves", "Bag", "Other Accessories"
        );
        itemChoiceBox.setValue("Jeans");
        return itemChoiceBox;
    }

    private ChoiceBox<String> createColorChoiceBox() {
        ChoiceBox<String> colorChoiceBox = new ChoiceBox<>();
        colorChoiceBox.getItems().addAll(
                "Red", "Green", "Blue", "Yellow", "Orange", "Purple", "White", "Black",
                "Gray", "Pink", "Light Blue", "Gold", "Dark Red", "Sea Green",
                "Steel Blue", "Indigo", "Goldenrod", "Brown"
        );
        colorChoiceBox.setValue("Red");
        return colorChoiceBox;
    }

    private ChoiceBox<String> createCombinationChoiceBox() {
        ChoiceBox<String> combinationChoiceBox = new ChoiceBox<>();
        combinationChoiceBox.getItems().addAll(
                "Complementary", "Analogous", "Monochromatic"
        );
        combinationChoiceBox.setValue("Complementary");
        return combinationChoiceBox;
    }

    private void generateOutfit(String selectedItem, String selectedColor, String combinationType) {
        mainVBox.getChildren().clear();

        //convert the color to uppercase hex code for matching (case-insensitive)
        String selectedColorHex = COLOR_NAME_TO_HEX.getOrDefault(selectedColor, selectedColor).toUpperCase();

        List<ClothingItem> matchingItems = wardrobe.stream()
                .filter(item -> item.getCategory().equalsIgnoreCase(selectedItem))
                .filter(item -> item.getColours().stream()
                        .map(String::toUpperCase)
                        .anyMatch(color -> color.equals(selectedColorHex)))
                .collect(Collectors.toList());

        if (matchingItems.isEmpty()) {
            Label noItemsLabel = new Label("No matching items found in your wardrobe.");
            noItemsLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
            mainVBox.getChildren().addAll(noItemsLabel, backButton);
            return;
        }

        Label resultLabel = new Label("You chose " + selectedItem + ". In your wardrobe, there are:");
        resultLabel.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
        mainVBox.getChildren().add(resultLabel);

        HBox matchingItemsHBox = new HBox(10);
        matchingItemsHBox.setAlignment(Pos.CENTER);

        for (ClothingItem item : matchingItems) {
            ImageView imageView = new ImageView(new Image(item.getImageFile().toURI().toString()));
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            matchingItemsHBox.getChildren().add(imageView);
        }

        ScrollPane matchingItemsScrollPane = new ScrollPane(matchingItemsHBox);
        matchingItemsScrollPane.setFitToHeight(true);
        matchingItemsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        matchingItemsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        mainVBox.getChildren().add(matchingItemsScrollPane);

        List<ClothingItem> suggestedItems = suggestComplementaryItems(selectedItem, selectedColor, combinationType, matchingItems);
        if (!suggestedItems.isEmpty()) {
            Label suggestionLabel = new Label("Suggested complementary items:");
            suggestionLabel.setStyle("-fx-font-size: 16px; -fx-padding: 10px;");
            mainVBox.getChildren().add(suggestionLabel);

            HBox suggestedItemsHBox = new HBox(10);
            suggestedItemsHBox.setAlignment(Pos.CENTER);

            for (ClothingItem item : suggestedItems) {
                ImageView imageView = new ImageView(new Image(item.getImageFile().toURI().toString()));
                imageView.setFitWidth(150);
                imageView.setFitHeight(150);
                suggestedItemsHBox.getChildren().add(imageView);
            }

            ScrollPane suggestedItemsScrollPane = new ScrollPane(suggestedItemsHBox);
            suggestedItemsScrollPane.setFitToHeight(true);
            suggestedItemsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            suggestedItemsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            mainVBox.getChildren().add(suggestedItemsScrollPane);
        } else {
            System.out.println("No complementary items found.");
        }

        mainVBox.getChildren().add(backButton);
    }

    private List<ClothingItem> suggestComplementaryItems(String selectedItem, String selectedColor, String combinationType, List<ClothingItem> matchingItems) {
        List<ClothingItem> suggestedItems = new ArrayList<>();

        List<String> colorHexes = COLOR_COMBINATIONS.getOrDefault(selectedColor, Collections.emptyMap())
                .getOrDefault(combinationType, Collections.emptyList());

        System.out.println("Selected Color: " + selectedColor);
        System.out.println("Color Hexes: " + colorHexes);

        for (ClothingItem item : wardrobe) {
            if (!matchingItems.contains(item) && item.getColours().stream()
                    .map(String::toUpperCase)
                    .anyMatch(colorHexes::contains)) {
                suggestedItems.add(item);
                System.out.println("Suggested Item: " + item.getCategory() + ", Colors: " + item.getColours());
            }
        }
        return suggestedItems;
    }
}