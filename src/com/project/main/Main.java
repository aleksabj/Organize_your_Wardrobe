package com.project.main;

import com.project.database.DatabaseHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Main class for the Virtual Wardrobe Organizer application.
 * This class initializes and starts the JavaFX application
 */
public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        DatabaseHelper.initializeDatabase(); // Initialize DB
        primaryStage.setTitle("Virtual Wardrobe Organizer");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/project/views/MainView.fxml"));
            VBox root = loader.load();
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    /**
     * main method to launch the JavaFX application
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("\u2705 SQLite JDBC loaded successfully!");
        } catch (ClassNotFoundException e) {
            System.err.println("\u274C Failed to load SQLite JDBC.");
        }
        launch(args);
    }
}
