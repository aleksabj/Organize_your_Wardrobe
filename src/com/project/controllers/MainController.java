package com.project.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainController {
    @FXML
    private VBox mainVBox; //the root VBox container in the fxml file
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
        Button createOutfitButton = new Button("Create an Outfit");
        Button addClothingItemButton = new Button("+");
        Button checkWeather = new Button("Suggest Outfit for Weather");
        //buttons placed horizontally
        HBox wardrobeOptions = new HBox(10);
        wardrobeOptions.setAlignment(Pos.CENTER);
        wardrobeOptions.getChildren().addAll(viewWardrobeButton, createOutfitButton, addClothingItemButton, checkWeather);

        //add HBox to the main VBox
        mainVBox.getChildren().add(wardrobeOptions);

    }

}
