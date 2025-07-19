package com.project.controllers;

import com.project.database.DatabaseHelper;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Map;

public class StatisticsController {
    private final VBox mainVBox;
    private final MainController mainController;

    public StatisticsController(VBox mainVBox, MainController mainController) {
        this.mainVBox = mainVBox;
        this.mainController = mainController;
    }

    public void showStatistics() {
        mainVBox.getChildren().clear();

        Label title = new Label("📊 Wardrobe Statistics");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        VBox contentBox = new VBox(30);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setStyle("-fx-padding: 30px;");

        // ==== Category Section ====
        VBox categorySection = new VBox(10);
        categorySection.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 20;");
        categorySection.setAlignment(Pos.TOP_LEFT);

        Label categoryTitle = new Label("👕 Items by Category");
        categoryTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox categoryBars = new VBox(10);
        Map<String, Integer> itemCounts = DatabaseHelper.getItemCountsByCategory();
        int max = itemCounts.values().stream().mapToInt(i -> i).max().orElse(1);

        for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            Label label = new Label(entry.getKey() + ":");
            label.setPrefWidth(100);

            Region bar = new Region();
            bar.setPrefHeight(16);
            bar.setPrefWidth(200.0 * entry.getValue() / max);
            bar.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 4;");

            Label value = new Label(String.valueOf(entry.getValue()));
            row.getChildren().addAll(label, bar, value);
            categoryBars.getChildren().add(row);
        }

        categorySection.getChildren().addAll(categoryTitle, categoryBars);

        // ==== Color Section ====
        VBox colorSection = new VBox(10);
        colorSection.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 20;");
        colorSection.setAlignment(Pos.TOP_LEFT);

        Label colorTitle = new Label("🎨 Colors Used");
        colorTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        FlowPane colorPane = new FlowPane(15, 15);
        colorPane.setPrefWrapLength(300);
        colorPane.setAlignment(Pos.CENTER_LEFT);

        Map<String, Integer> colorCounts = DatabaseHelper.getColorCounts();
        if (!colorCounts.isEmpty()) {
            for (Map.Entry<String, Integer> entry : colorCounts.entrySet()) {
                VBox colorBox = new VBox(5);
                colorBox.setAlignment(Pos.CENTER);

                Circle swatch = new Circle(15);
                try {
                    swatch.setFill(Color.web(entry.getKey()));
                    swatch.setStroke(Color.web("#cccccc")); //light gray border
                    swatch.setStrokeWidth(1.2);
                } catch (Exception e) {
                    swatch.setFill(Color.GRAY);
                }

                Label count = new Label(String.valueOf(entry.getValue()));
                count.setStyle("-fx-font-size: 12px;");

                colorBox.getChildren().addAll(swatch, count);
                colorPane.getChildren().add(colorBox);
            }
        } else {
            colorPane.getChildren().add(new Label("No color data available."));
        }

        colorSection.getChildren().addAll(colorTitle, colorPane);

        // ==== Packing Stats Section ====
        VBox packingSection = new VBox(10);
        packingSection.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 20;");
        packingSection.setAlignment(Pos.TOP_LEFT);

        Label packingTitle = new Label("🧳 Packing List Stats");
        packingTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        int total = DatabaseHelper.getTotalPackingLists();
        double avg = DatabaseHelper.getAverageItemsPerPackingList();

        Label totalLabel = new Label("📝 Total Lists: " + total);
        Label avgLabel = new Label("📦 Avg Items/List: " + String.format("%.2f", avg));

        packingSection.getChildren().addAll(packingTitle, totalLabel, avgLabel);

        // ==== Back Button ====
        Button back = new Button("Back to Main Menu");
        back.setOnAction(evt -> mainController.displayWardrobe());

        contentBox.getChildren().addAll(title, categorySection, colorSection, packingSection, back);
        mainVBox.getChildren().add(contentBox);
    }
}
