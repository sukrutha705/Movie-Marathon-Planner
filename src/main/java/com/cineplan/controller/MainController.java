package com.cineplan.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class MainController {
    
    @FXML private BorderPane contentPane;
    @FXML private Button btnDashboard;
    @FXML private Button btnMovieManager;
    @FXML private Button btnDiscover;
    @FXML private Button btnSavedMarathons;
    @FXML private Button btnQuiz;
    @FXML private Button btnAchievements;
    @FXML private Button btnAnalytics;

    private static MainController instance;

    public static MainController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        instance = this;
        // Default startup screen is Dashboard
        showView("dashboard.fxml");
    }

    public void showView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cineplan/" + fxmlFile));
            Parent view = loader.load();
            contentPane.setCenter(view);
            updateActiveButton(fxmlFile);

            // Apply a beautiful Fade-in & Slide-up animation to the new view
            view.setOpacity(0.0);
            view.setTranslateY(15);
            
            javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(javafx.util.Duration.millis(350), view);
            fade.setToValue(1.0);
            
            javafx.animation.TranslateTransition slide = new javafx.animation.TranslateTransition(javafx.util.Duration.millis(350), view);
            slide.setToY(0);
            
            javafx.animation.ParallelTransition transition = new javafx.animation.ParallelTransition(fade, slide);
            transition.play();
        } catch (IOException e) {
            System.err.println("Failed to load FXML view: " + fxmlFile);
            e.printStackTrace();
        }
    }

    public <T> T showViewAndGetController(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/cineplan/" + fxmlFile));
            Parent view = loader.load();
            contentPane.setCenter(view);
            updateActiveButton(fxmlFile);

            // Apply a beautiful Fade-in & Slide-up animation to the new view
            view.setOpacity(0.0);
            view.setTranslateY(15);
            
            javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(javafx.util.Duration.millis(350), view);
            fade.setToValue(1.0);
            
            javafx.animation.TranslateTransition slide = new javafx.animation.TranslateTransition(javafx.util.Duration.millis(350), view);
            slide.setToY(0);
            
            javafx.animation.ParallelTransition transition = new javafx.animation.ParallelTransition(fade, slide);
            transition.play();

            return loader.getController();
        } catch (IOException e) {
            System.err.println("Failed to load FXML view and get controller: " + fxmlFile);
            e.printStackTrace();
            return null;
        }
    }

    private void updateActiveButton(String fxmlFile) {
        btnDashboard.getStyleClass().remove("nav-button-active");
        btnMovieManager.getStyleClass().remove("nav-button-active");
        btnDiscover.getStyleClass().remove("nav-button-active");
        btnSavedMarathons.getStyleClass().remove("nav-button-active");
        btnQuiz.getStyleClass().remove("nav-button-active");
        btnAchievements.getStyleClass().remove("nav-button-active");
        btnAnalytics.getStyleClass().remove("nav-button-active");
 
        if (fxmlFile.equals("dashboard.fxml")) {
            btnDashboard.getStyleClass().add("nav-button-active");
        } else if (fxmlFile.equals("movie_manager.fxml")) {
            btnMovieManager.getStyleClass().add("nav-button-active");
        } else if (fxmlFile.equals("discover.fxml")) {
            btnDiscover.getStyleClass().add("nav-button-active");
        } else if (fxmlFile.equals("saved_marathons.fxml")) {
            btnSavedMarathons.getStyleClass().add("nav-button-active");
        } else if (fxmlFile.equals("quiz.fxml")) {
            btnQuiz.getStyleClass().add("nav-button-active");
        } else if (fxmlFile.equals("achievements.fxml")) {
            btnAchievements.getStyleClass().add("nav-button-active");
        } else if (fxmlFile.equals("analytics.fxml")) {
            btnAnalytics.getStyleClass().add("nav-button-active");
        }
    }

    @FXML private void handleShowDashboard() { showView("dashboard.fxml"); }
    @FXML private void handleShowMovieManager() { showView("movie_manager.fxml"); }
    @FXML private void handleShowDiscover() { showView("discover.fxml"); }
    @FXML private void handleShowSavedMarathons() { showView("saved_marathons.fxml"); }
    @FXML private void handleShowQuiz() { showView("quiz.fxml"); }
    @FXML private void handleShowAchievements() { showView("achievements.fxml"); }
    @FXML private void handleShowAnalytics() { showView("analytics.fxml"); }
}
