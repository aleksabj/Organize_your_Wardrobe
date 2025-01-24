package com.project.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class MainController {
    @FXML
    private VBox mainBox; //the root VBox container in the fxml file
    @FXML
    public void initialize(){
        System.out.println("Welcome to the Virtual Wardrobe Organizer!");
    }
    @FXML
    public void handleStartOrganizing() {
        System.out.println("Start Organizing button clicked!");
        //TO DO: organizing logic
    }

}
