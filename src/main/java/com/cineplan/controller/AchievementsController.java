package com.cineplan.controller;

import com.cineplan.model.Achievement;
import com.cineplan.repository.AchievementRepository;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class AchievementsController {

    @FXML private VBox containerAchievements;

    private final AchievementRepository achievementRepository = new AchievementRepository();

    @FXML
    public void initialize() {
        loadAchievements();
    }

    private void loadAchievements() {
        containerAchievements.getChildren().clear();
        List<Achievement> achievements = achievementRepository.getAllAchievements();

        for (Achievement ach : achievements) {
            containerAchievements.getChildren().add(createAchievementRow(ach));
        }
    }

    private HBox createAchievementRow(Achievement ach) {
        HBox row = new HBox();
        row.getStyleClass().add("achievement-card");
        if (!ach.isUnlocked()) {
            row.getStyleClass().add("achievement-card-locked");
        }
        row.setAlignment(Pos.CENTER_LEFT);
        row.setSpacing(20);

        // Badge graphic: a circle with an emoji
        VBox badgeCircle = new VBox();
        badgeCircle.getStyleClass().add("achievement-badge-circle");
        if (!ach.isUnlocked()) {
            badgeCircle.getStyleClass().add("achievement-badge-circle-locked");
        }
        badgeCircle.setAlignment(Pos.CENTER);

        Label badgeLabel = new Label(ach.isUnlocked() ? "🏆" : "🔒");
        badgeLabel.setStyle("-fx-font-size: 20px;");
        badgeCircle.getChildren().add(badgeLabel);

        // Text details
        VBox details = new VBox();
        details.setSpacing(5);
        HBox.setHgrow(details, Priority.ALWAYS);

        Label title = new Label(ach.getName());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label desc = new Label(ach.getDescription());
        desc.setStyle("-fx-font-size: 13px; -fx-text-fill: #AAAAAA;");
        desc.setWrapText(true);

        details.getChildren().addAll(title, desc);

        // Unlocked / Locked status text
        Label status = new Label(ach.isUnlocked() ? "UNLOCKED" : "LOCKED");
        if (ach.isUnlocked()) {
            status.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #E50914;");
        } else {
            status.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #555555;");
        }

        row.getChildren().addAll(badgeCircle, details, status);
        return row;
    }
}
