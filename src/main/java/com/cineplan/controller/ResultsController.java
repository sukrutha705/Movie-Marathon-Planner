package com.cineplan.controller;

import com.cineplan.MainApp;
import com.cineplan.model.Marathon;
import com.cineplan.model.Movie;
import com.cineplan.repository.MarathonRepository;
import com.cineplan.repository.AchievementRepository;
import com.cineplan.service.AchievementService;
import com.cineplan.service.PDFExportService;
import com.cineplan.utils.AlertHelper;
import com.cineplan.model.Achievement;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ResultsController {

    @FXML private Label lblSubHeader;
    @FXML private Label lblTotalRuntime;
    @FXML private Label lblTotalScore;
    @FXML private Label lblMood;
    @FXML private Label lblRatingClass;
    @FXML private FlowPane flowMovies;

    private List<Movie> selectedMovies;
    private String selectedMood;
    private int capacityTime;

    private final MarathonRepository marathonRepository = new MarathonRepository();
    private final AchievementRepository achievementRepository = new AchievementRepository();
    private final AchievementService achievementService = new AchievementService(achievementRepository);

    public void initData(List<Movie> movies, String mood, int timeLimit) {
        this.selectedMovies = movies;
        this.selectedMood = mood;
        this.capacityTime = timeLimit;

        int totalRuntime = movies.stream().mapToInt(Movie::getRuntime).sum();
        int totalScore = movies.stream().mapToInt(Movie::getEntertainmentScore).sum();

        lblTotalRuntime.setText(totalRuntime + " min");
        lblTotalScore.setText(totalScore + " pts");
        lblMood.setText(mood);

        // Marathon class badge
        String ratingClass;
        String colorStyle;
        if (totalScore < 20) {
            ratingClass = "Good";
            colorStyle = "-fx-text-fill: #999999;";
        } else if (totalScore < 30) {
            ratingClass = "Excellent";
            colorStyle = "-fx-text-fill: #4CAF50;";
        } else if (totalScore < 40) {
            ratingClass = "Legendary";
            colorStyle = "-fx-text-fill: #9C27B0;";
        } else {
            ratingClass = "Cinema God";
            colorStyle = "-fx-text-fill: #FFA000; -fx-font-weight: 900;";
        }
        lblRatingClass.setText(ratingClass);
        lblRatingClass.setStyle(colorStyle);
        lblSubHeader.setText("Optimized schedule: " + movies.size() + " movies fitting within " + capacityTime + " minutes.");

        // Render movie cards
        flowMovies.getChildren().clear();
        int delay = 0;
        for (Movie m : movies) {
            VBox card = createMovieCard(m);
            card.setOpacity(0.0);
            flowMovies.getChildren().add(card);
            
            javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(javafx.util.Duration.millis(300), card);
            fade.setToValue(1.0);
            fade.setDelay(javafx.util.Duration.millis(delay * 75));
            fade.play();
            delay++;
        }
    }

    private VBox createMovieCard(Movie m) {
        VBox card = new VBox(8);
        card.getStyleClass().add("movie-card");
        card.setPrefWidth(190);
        card.setPadding(new Insets(12));

        // ── Poster ────────────────────────────────────────────────────────
        StackPane posterContainer = new StackPane();
        posterContainer.setPrefSize(166, 180);

        boolean loaded = false;
        if (m.getPosterUrl() != null && !m.getPosterUrl().trim().isEmpty()) {
            try {
                Image img = new Image(m.getPosterUrl(), 166, 180, true, true, true);
                if (!img.isError()) {
                    ImageView view = new ImageView(img);
                    view.setFitWidth(166);
                    view.setFitHeight(180);
                    posterContainer.getChildren().add(view);
                    loaded = true;
                }
            } catch (Exception ignored) {}
        }

        if (!loaded) {
            Label titleFallback = new Label(m.getTitle());
            titleFallback.setWrapText(true);
            titleFallback.setAlignment(Pos.CENTER);
            titleFallback.setStyle("-fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 12px; -fx-text-alignment: center; -fx-padding: 10px;");
            String bg = genreColor(m.getGenre());
            posterContainer.setStyle(bg + " -fx-background-radius: 6px;");
            posterContainer.getChildren().add(titleFallback);
        }

        // ── Title ─────────────────────────────────────────────────────────
        Label title = new Label(m.getTitle());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #FFFFFF;");
        title.setWrapText(true);

        // ── Badges row ────────────────────────────────────────────────────
        HBox badges = new HBox(6);
        badges.setAlignment(Pos.CENTER_LEFT);

        Label badgeGenre = new Label(m.getGenre());
        badgeGenre.getStyleClass().add("badge");
        badgeGenre.setStyle("-fx-font-size: 9px; -fx-padding: 1px 5px;");

        Label badgeRating = new Label("★" + String.format("%.1f", m.getRating()));
        badgeRating.getStyleClass().addAll("badge", "badge-rating");
        badgeRating.setStyle("-fx-font-size: 9px; -fx-padding: 1px 5px;");

        Label badgeLang = new Label(m.getLanguage() != null ? m.getLanguage() : "EN");
        badgeLang.setStyle("-fx-background-color: #333355; -fx-text-fill: #AAAAFF; -fx-background-radius: 3px; -fx-font-size: 9px; -fx-padding: 1px 5px;");

        badges.getChildren().addAll(badgeGenre, badgeRating, badgeLang);

        // ── Runtime & Score ───────────────────────────────────────────────
        Label meta = new Label(m.getRuntime() + " min  •  Score: " + m.getEntertainmentScore());
        meta.setStyle("-fx-text-fill: #888888; -fx-font-size: 10px;");

        // ── Watch Now button ──────────────────────────────────────────────
        boolean hasUrl = m.getWatchUrl() != null && !m.getWatchUrl().trim().isEmpty();
        Button btnWatch = new Button(hasUrl ? "▶  Watch Now ↗" : "No Link Available");
        btnWatch.setPrefWidth(166);
        btnWatch.setDisable(!hasUrl);
        if (hasUrl) {
            btnWatch.setStyle(
                "-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-background-radius: 4px; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 6px 0;"
            );
            btnWatch.setOnAction(e -> {
                try {
                    String url = m.getWatchUrl();
                    if (java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                        java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
                    } else {
                        MainApp.getInstance().getHostServices().showDocument(url);
                    }
                } catch (Exception ex) {
                    try {
                        MainApp.getInstance().getHostServices().showDocument(m.getWatchUrl());
                    } catch (Exception ex2) {
                        AlertHelper.showError("Browser Error", "Could not open the URL:\n" + m.getWatchUrl());
                    }
                }
            });
            // Hover effect
            btnWatch.setOnMouseEntered(e -> btnWatch.setStyle(
                "-fx-background-color: #FF1A1A; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-background-radius: 4px; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 6px 0;"
            ));
            btnWatch.setOnMouseExited(e -> btnWatch.setStyle(
                "-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-background-radius: 4px; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 6px 0;"
            ));
        } else {
            btnWatch.setStyle(
                "-fx-background-color: #2A2A2A; -fx-text-fill: #555555; -fx-background-radius: 4px; " +
                "-fx-font-size: 11px; -fx-padding: 6px 0;"
            );
        }

        card.getChildren().addAll(posterContainer, title, badges, meta, btnWatch);
        return card;
    }

    private String genreColor(String genre) {
        if (genre == null) return "-fx-background-color: #333333;";
        switch (genre.toLowerCase()) {
            case "action":    return "-fx-background-color: #B81D24;";
            case "comedy":    return "-fx-background-color: #E28F00;";
            case "sci-fi":    return "-fx-background-color: #0F62FE;";
            case "fantasy":   return "-fx-background-color: #8A3FFC;";
            case "horror":    return "-fx-background-color: #198038;";
            case "drama":     return "-fx-background-color: #007D79;";
            case "animation": return "-fx-background-color: #FF7EB6;";
            default:          return "-fx-background-color: #333333;";
        }
    }

    @FXML
    private void handleSavePlan() {
        if (selectedMovies == null || selectedMovies.isEmpty()) return;

        String defaultName = selectedMood + " Marathon (" + selectedMovies.size() + " Movies)";
        TextInputDialog dialog = new TextInputDialog(defaultName);
        dialog.setTitle("Save Marathon Schedule");
        dialog.setHeaderText("Choose a name for your saved movie marathon:");
        dialog.setContentText("Marathon Name:");
        dialog.getDialogPane().setStyle("-fx-background-color: #1F1F1F;");
        dialog.getDialogPane().lookup(".content.label").setStyle("-fx-text-fill: white;");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String name = result.get().trim();
            int totalRuntime = selectedMovies.stream().mapToInt(Movie::getRuntime).sum();
            int totalScore   = selectedMovies.stream().mapToInt(Movie::getEntertainmentScore).sum();
            String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            Marathon m = new Marathon(name, selectedMood, totalRuntime, totalScore, createdAt);
            m.setMovies(selectedMovies);

            boolean saved = marathonRepository.saveMarathon(m);
            if (saved) {
                AlertHelper.showInfo("Success", "Marathon plan saved successfully!");
                List<Achievement> unlocked = achievementService.checkAndUnlockMarathonAchievements(m);
                for (Achievement ach : unlocked) {
                    AlertHelper.showAchievementUnlock(ach.getName(), ach.getDescription());
                }
            } else {
                AlertHelper.showError("Database Error", "Failed to save marathon.");
            }
        }
    }

    @FXML
    private void handleExportPDF() {
        if (selectedMovies == null || selectedMovies.isEmpty()) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF Report");
        fileChooser.setInitialFileName("marathon_report.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));
        File file = fileChooser.showSaveDialog(lblSubHeader.getScene().getWindow());

        if (file != null) {
            try {
                int totalRuntime = selectedMovies.stream().mapToInt(Movie::getRuntime).sum();
                int totalScore   = selectedMovies.stream().mapToInt(Movie::getEntertainmentScore).sum();
                String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                Marathon marathon = new Marathon("Temporary Marathon", selectedMood, totalRuntime, totalScore, now);
                marathon.setMovies(selectedMovies);
                PDFExportService.exportMarathon(marathon, file);
                AlertHelper.showInfo("Export Done", "PDF saved to:\n" + file.getAbsolutePath());
            } catch (Exception e) {
                AlertHelper.showError("Export Error", "PDF generation failed:\n" + e.getMessage());
            }
        }
    }

    @FXML
    private void handleBack() {
        MainController.getInstance().showView("dashboard.fxml");
    }
}
