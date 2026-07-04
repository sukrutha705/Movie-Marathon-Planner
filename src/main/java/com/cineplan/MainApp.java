package com.cineplan;

import com.cineplan.database.DatabaseManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static MainApp instance;

    public static MainApp getInstance() {
        return instance;
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this;
        try {
                        // 1. Initialize SQLite Database Tables & Seed Sample Data
            DatabaseManager.initializeDatabase();


            // 2. Load the main window frame layout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cineplan/main_layout.fxml"));
            Parent root = loader.load();

            // 3. Setup and configure primary stage Scene
            Scene scene = new Scene(root, 1050, 700);
            primaryStage.setScene(scene);
            primaryStage.setTitle("CinePlan - Smart Movie Marathon Planner");
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(650);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Fatal Error during CinePlan startup:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
