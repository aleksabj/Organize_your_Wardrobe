package com.project.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Virtual Wardrobe Organizer");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/project/views/MainView.fxml"));
            VBox root = loader.load();
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setTitle("Virtual Wardrobe Organizer");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
