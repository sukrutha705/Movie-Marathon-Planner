package com.cineplan.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;

public class AlertHelper {

    public static void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        // Custom styling for dialogs to match the dark theme
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #1F1F1F; -fx-text-fill: white;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        if (dialogPane.lookup(".header-panel") != null) {
            dialogPane.lookup(".header-panel").setStyle("-fx-background-color: #151515; -fx-text-fill: white;");
        }
        
        alert.showAndWait();
    }

    public static void showInfo(String title, String content) {
        showAlert(Alert.AlertType.INFORMATION, title, null, content);
    }

    public static void showWarning(String title, String content) {
        showAlert(Alert.AlertType.WARNING, title, null, content);
    }

    public static void showError(String title, String content) {
        showAlert(Alert.AlertType.ERROR, title, null, content);
    }

    public static void showAchievementUnlock(String achievementName, String description) {
        String title = "🏆 Achievement Unlocked!";
        String header = "Congratulations! You unlocked a new badge!";
        String content = "★ " + achievementName.toUpperCase() + "\n" + description;
        showAlert(Alert.AlertType.INFORMATION, title, header, content);
    }
}
