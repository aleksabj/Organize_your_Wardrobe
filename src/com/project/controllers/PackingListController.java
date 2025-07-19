package com.project.controllers;

import com.project.database.DatabaseHelper;
import com.project.model.ClothingItem;
import com.project.model.PackingList;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for creating and displaying packing lists.
 */
public class PackingListController {
    private final VBox mainVBox;
    private final List<ClothingItem> wardrobe;
    private final MainController mainController;

    public PackingListController(VBox mainVBox, List<ClothingItem> wardrobe, MainController mainController) {
        this.mainVBox = mainVBox;
        this.wardrobe = wardrobe;
        this.mainController = mainController;
    }

    public void showPackingListsPage() {
        mainVBox.getChildren().clear();

        Label title = new Label("Packing Lists");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        FlowPane listFlowPane = new FlowPane();
        listFlowPane.setHgap(15);
        listFlowPane.setVgap(15);
        listFlowPane.setAlignment(Pos.CENTER);

        List<PackingList> lists = DatabaseHelper.loadPackingLists();
        for (PackingList list : lists) {
            VBox card = new VBox(5);
            card.setAlignment(Pos.CENTER);
            card.setPrefSize(100, 100);
            card.setStyle("-fx-border-color: gray; -fx-border-radius: 10px; -fx-padding: 10px; -fx-background-radius: 10px; -fx-background-color: #f0f0f0;");

            Label emojiLabel = new Label(list.getCoverEmoji() != null ? list.getCoverEmoji() : "🎒");
            emojiLabel.setStyle("-fx-font-size: 32px;");

            Label nameLabel = new Label(list.getName());
            nameLabel.setStyle("-fx-font-size: 14px;");

            card.getChildren().addAll(emojiLabel, nameLabel);

            card.setOnMouseClicked((MouseEvent e) -> showPackingListDetails(list));

            listFlowPane.getChildren().add(card);
        }

        Button addButton = new Button("+");
        addButton.setStyle("-fx-font-size: 24px;");
        addButton.setOnAction(e -> showPackingListCreator());

        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(evt -> mainController.displayWardrobe());

        VBox container = new VBox(20, title, listFlowPane, addButton, backButton);
        container.setAlignment(Pos.CENTER);
        mainVBox.getChildren().add(container);
    }

    public void showPackingListCreator() {
        mainVBox.getChildren().clear();

        Label titleLabel = new Label("Create New Packing List");
        titleLabel.setStyle("-fx-font-size: 20px;");

        TextField nameField = new TextField();
        nameField.setPromptText("Packing List Name");

        ComboBox<String> emojiPicker = new ComboBox<>();
        emojiPicker.getItems().addAll("🎒", "🏖️", "🎿", "🧳", "🌲", "🎒", "🚴", "🏕️", "🌍", "☀️", "❄️");
        emojiPicker.setPromptText("Choose a Cover Emoji");

        VBox itemCheckboxes = new VBox(5);
        List<CheckBox> checkBoxes = new ArrayList<>();
        for (int i = 0; i < wardrobe.size(); i++) {
            ClothingItem item = wardrobe.get(i);
            HBox itemBox = new HBox(10);
            itemBox.setAlignment(Pos.CENTER_LEFT);

            ImageView imageView = new ImageView(new Image(item.getImageFile().toURI().toString()));
            imageView.setFitWidth(80);
            imageView.setFitHeight(80);

            CheckBox checkBox = new CheckBox();
            checkBoxes.add(checkBox);

            itemBox.getChildren().addAll(imageView, checkBox);
            itemCheckboxes.getChildren().add(itemBox);
        }

        Label feedbackLabel = new Label();

        Button saveButton = new Button("Save Packing List");
        saveButton.setOnAction(evt -> {
            String name = nameField.getText().trim();
            String emoji = emojiPicker.getValue();

            if (name.isEmpty() || emoji == null) {
                feedbackLabel.setText("Name and emoji are required.");
                return;
            }

            PackingList list = new PackingList(name, emoji);
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isSelected()) {
                    list.addClothingItemId(i + 1);
                }
            }

            DatabaseHelper.savePackingList(list);
            feedbackLabel.setText("Packing list saved!");

            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> showPackingListsPage());
            pause.play();
        });

        Button backButton = new Button("Back to Lists");
        backButton.setOnAction(evt -> showPackingListsPage());

        VBox container = new VBox(15, titleLabel, nameField, emojiPicker, itemCheckboxes, saveButton, feedbackLabel, backButton);
        container.setAlignment(Pos.CENTER);
        mainVBox.getChildren().add(container);
    }

    private void showPackingListDetails(PackingList list) {
        mainVBox.getChildren().clear();

        Label title = new Label(list.getCoverEmoji() + " " + list.getName());
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        HBox imagesHBox = new HBox(10);
        imagesHBox.setAlignment(Pos.CENTER);

        for (int id : list.getClothingItemIds()) {
            if (id - 1 < wardrobe.size()) {
                ClothingItem item = wardrobe.get(id - 1); // assumes 1-indexed IDs
                ImageView imageView = new ImageView(new Image(item.getImageFile().toURI().toString()));
                imageView.setFitWidth(120);
                imageView.setFitHeight(120);
                imagesHBox.getChildren().add(imageView);
            }
        }

        Button backButton = new Button("Back to Lists");
        backButton.setOnAction(evt -> showPackingListsPage());

        VBox container = new VBox(20, title, imagesHBox, backButton);
        container.setAlignment(Pos.CENTER);
        mainVBox.getChildren().add(container);
    }
}
