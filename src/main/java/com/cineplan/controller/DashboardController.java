package com.cineplan.controller;

import com.cineplan.algorithm.KnapsackOptimizer;
import com.cineplan.model.Movie;
import com.cineplan.repository.MovieRepository;
import com.cineplan.service.MoodEngine;
import com.cineplan.utils.AlertHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML private TextField txtAvailableTime;
    @FXML private ComboBox<String> comboMood;
    @FXML private ComboBox<String> comboGenre;
    @FXML private FlowPane paneLanguages;

    @FXML private CheckBox chkEnglish;
    @FXML private CheckBox chkHindi;
    @FXML private CheckBox chkTelugu;
    @FXML private CheckBox chkJapanese;
    @FXML private CheckBox chkKorean;
    @FXML private CheckBox chkSpanish;
    @FXML private CheckBox chkFrench;
    @FXML private CheckBox chkGerman;

    private final MovieRepository movieRepository = new MovieRepository();

    @FXML
    public void initialize() {
        comboMood.setItems(FXCollections.observableArrayList(
            "Comedy Night",
            "Mind Bending",
            "Date Night",
            "Horror Marathon",
            "Fantasy Adventure",
            "Weekend Relaxation"
        ));
        comboMood.setValue("Weekend Relaxation");

        comboGenre.setItems(FXCollections.observableArrayList(
            "All Genres", "Action", "Comedy", "Sci-Fi",
            "Fantasy", "Horror", "Drama", "Animation"
        ));

        // Load user's saved favorite genre
        com.cineplan.repository.UserRepository userRepo = new com.cineplan.repository.UserRepository();
        com.cineplan.model.User user = userRepo.getUser();
        if (user != null && user.getFavoriteGenre() != null) {
            comboGenre.setValue(user.getFavoriteGenre());
        } else {
            comboGenre.setValue("All Genres");
        }
    }

    /** Collect all checked language checkboxes into a list. Empty list = no filter. */
    private List<String> getSelectedLanguages() {
        List<String> langs = new ArrayList<>();
        if (chkEnglish.isSelected())  langs.add("English");
        if (chkHindi.isSelected())    langs.add("Hindi");
        if (chkTelugu.isSelected())   langs.add("Telugu");
        if (chkJapanese.isSelected()) langs.add("Japanese");
        if (chkKorean.isSelected())   langs.add("Korean");
        if (chkSpanish.isSelected())  langs.add("Spanish");
        if (chkFrench.isSelected())   langs.add("French");
        if (chkGerman.isSelected())   langs.add("German");
        return langs;
    }

    @FXML
    private void handleGenerateMarathon() {
        String timeStr = txtAvailableTime.getText();
        if (timeStr == null || timeStr.trim().isEmpty()) {
            AlertHelper.showWarning("Input Error", "Please specify your available viewing time.");
            return;
        }

        int availableTime;
        try {
            availableTime = Integer.parseInt(timeStr.trim());
            if (availableTime <= 0) {
                AlertHelper.showWarning("Input Error", "Viewing time must be a positive integer.");
                return;
            }
            if (availableTime > 1440) {
                AlertHelper.showWarning("Input Error", "Viewing time cannot exceed 1440 minutes (24 hours).");
                return;
            }
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Input Error", "Please enter a valid number for available time.");
            return;
        }

        generateAndDisplay(availableTime, comboMood.getValue(), comboGenre.getValue(), getSelectedLanguages());
    }

    private void generateAndDisplay(int availableTime, String mood, String genre, List<String> languages) {
        List<Movie> allMovies = movieRepository.getAllMovies();
        if (allMovies.isEmpty()) {
            AlertHelper.showError("Database Error", "No movies found in the database.");
            return;
        }

        // 1. Apply Mood Engine score bonuses
        List<Movie> adjustedMovies = MoodEngine.applyMoodModifier(allMovies, mood);

        // 2. Filter by genre
        if (genre != null && !"All Genres".equalsIgnoreCase(genre)) {
            adjustedMovies = adjustedMovies.stream()
                .filter(m -> m.getGenre().equalsIgnoreCase(genre))
                .collect(Collectors.toList());
        }

        // 3. Filter by selected languages (if any checked)
        if (!languages.isEmpty()) {
            adjustedMovies = adjustedMovies.stream()
                .filter(m -> languages.contains(m.getLanguage()))
                .collect(Collectors.toList());
        }

        if (adjustedMovies.isEmpty()) {
            AlertHelper.showWarning("No Movies Found",
                "No movies found matching the selected genre and language filters. Try selecting 'All Genres' or unchecking some languages.");
            return;
        }

        // 4. Run 0/1 Knapsack Optimizer
        List<Movie> selected = KnapsackOptimizer.optimize(adjustedMovies, availableTime);

        if (selected.isEmpty()) {
            AlertHelper.showInfo("No Match Found",
                "No movies could fit into " + availableTime + " minutes with the current filters. Try increasing the time limit.");
            return;
        }

        // 5. Navigate to Results Screen
        ResultsController controller = MainController.getInstance().showViewAndGetController("results.fxml");
        if (controller != null) {
            controller.initData(selected, mood, availableTime);
        }
    }

    // ── Preset Surprise Me handlers ──────────────────────────────────────────

    @FXML private void handleMindBendingNight() {
        txtAvailableTime.setText("350");
        comboMood.setValue("Mind Bending");
        comboGenre.setValue("Sci-Fi");
        clearLanguages();
        handleGenerateMarathon();
    }

    @FXML private void handleOscarWinners() {
        txtAvailableTime.setText("320");
        comboMood.setValue("Weekend Relaxation");
        comboGenre.setValue("Drama");
        clearLanguages();
        handleGenerateMarathon();
    }

    @FXML private void handleHiddenGems() {
        txtAvailableTime.setText("280");
        comboMood.setValue("Date Night");
        comboGenre.setValue("Comedy");
        clearLanguages();
        handleGenerateMarathon();
    }

    @FXML private void handleAnimationMarathon() {
        txtAvailableTime.setText("240");
        comboMood.setValue("Fantasy Adventure");
        comboGenre.setValue("Animation");
        clearLanguages();
        handleGenerateMarathon();
    }

    @FXML private void handleWorldCinema() {
        txtAvailableTime.setText("360");
        comboMood.setValue("Weekend Relaxation");
        comboGenre.setValue("All Genres");
        // Select non-English languages
        clearLanguages();
        chkHindi.setSelected(true);
        chkTelugu.setSelected(true);
        chkJapanese.setSelected(true);
        chkKorean.setSelected(true);
        chkSpanish.setSelected(true);
        chkFrench.setSelected(true);
        chkGerman.setSelected(true);
        handleGenerateMarathon();
    }

    private void clearLanguages() {
        chkEnglish.setSelected(false);
        chkHindi.setSelected(false);
        chkTelugu.setSelected(false);
        chkJapanese.setSelected(false);
        chkKorean.setSelected(false);
        chkSpanish.setSelected(false);
        chkFrench.setSelected(false);
        chkGerman.setSelected(false);
    }
}
