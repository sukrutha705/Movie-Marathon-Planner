package com.cineplan.controller;

import com.cineplan.model.Movie;
import com.cineplan.repository.MovieRepository;
import com.cineplan.utils.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Optional;

public class MovieManagerController {

    // ── TableView ─────────────────────────────────────────────────────────────
    @FXML private TableView<Movie> tableMovies;
    @FXML private TableColumn<Movie, Integer> colId;
    @FXML private TableColumn<Movie, String>  colTitle;
    @FXML private TableColumn<Movie, String>  colGenre;
    @FXML private TableColumn<Movie, String>  colLanguage;
    @FXML private TableColumn<Movie, Integer> colRuntime;
    @FXML private TableColumn<Movie, Double>  colRating;
    @FXML private TableColumn<Movie, Integer> colScore;

    // ── Search/filter bar ─────────────────────────────────────────────────────
    @FXML private TextField    txtSearch;
    @FXML private ComboBox<String> comboFilterGenre;
    @FXML private ComboBox<String> comboFilterLanguage;

    // ── Add/Edit form ─────────────────────────────────────────────────────────
    @FXML private Label        lblFormTitle;
    @FXML private TextField    txtTitle;
    @FXML private ComboBox<String> comboGenre;
    @FXML private ComboBox<String> comboLanguage;
    @FXML private TextField    txtRuntime;
    @FXML private TextField    txtRating;
    @FXML private TextField    txtScore;
    @FXML private TextField    txtPosterUrl;
    @FXML private TextField    txtWatchUrl;
    @FXML private TextArea     txtDescription;

    private final MovieRepository movieRepository = new MovieRepository();
    private final ObservableList<Movie> movieList = FXCollections.observableArrayList();
    private Movie selectedMovie = null;

    private static final List<String> GENRES    = List.of("Action","Comedy","Sci-Fi","Fantasy","Horror","Drama","Animation");
    private static final List<String> LANGUAGES = List.of("English","Hindi","Telugu","Japanese","Korean","Spanish","French","German");

    @FXML
    public void initialize() {
        // Table columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colLanguage.setCellValueFactory(new PropertyValueFactory<>("language"));
        colRuntime.setCellValueFactory(new PropertyValueFactory<>("runtime"));
        colRating.setCellValueFactory(new PropertyValueFactory<>("rating"));
        colScore.setCellValueFactory(new PropertyValueFactory<>("entertainmentScore"));

        // Search filters
        comboFilterGenre.setItems(FXCollections.observableArrayList(
            "All Genres", "Action", "Comedy", "Sci-Fi", "Fantasy", "Horror", "Drama", "Animation"
        ));
        comboFilterGenre.setValue("All Genres");

        comboFilterLanguage.setItems(FXCollections.observableArrayList(
            "All Languages", "English", "Hindi", "Telugu", "Japanese", "Korean", "Spanish", "French", "German"
        ));
        comboFilterLanguage.setValue("All Languages");

        // Form dropdowns
        comboGenre.setItems(FXCollections.observableArrayList(GENRES));
        comboLanguage.setItems(FXCollections.observableArrayList(LANGUAGES));
        comboLanguage.setValue("English");

        loadMovies();

        tableMovies.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSel, newSel) -> { if (newSel != null) populateForm(newSel); }
        );
    }

    private void loadMovies() {
        movieList.setAll(movieRepository.getAllMovies());
        tableMovies.setItems(movieList);
    }

    private void populateForm(Movie movie) {
        this.selectedMovie = movie;
        lblFormTitle.setText("Edit Movie (ID: " + movie.getId() + ")");
        txtTitle.setText(movie.getTitle());
        comboGenre.setValue(movie.getGenre());
        comboLanguage.setValue(movie.getLanguage() != null ? movie.getLanguage() : "English");
        txtRuntime.setText(String.valueOf(movie.getRuntime()));
        txtRating.setText(String.valueOf(movie.getRating()));
        txtScore.setText(String.valueOf(movie.getEntertainmentScore()));
        txtPosterUrl.setText(movie.getPosterUrl() == null ? "" : movie.getPosterUrl());
        txtWatchUrl.setText(movie.getWatchUrl() == null ? "" : movie.getWatchUrl());
        txtDescription.setText(movie.getDescription() == null ? "" : movie.getDescription());
    }

    @FXML
    private void handleSearch() {
        String query = txtSearch.getText();
        String genreFilter = comboFilterGenre.getValue();
        String langFilter  = comboFilterLanguage.getValue();

        List<Movie> results = movieRepository.searchMoviesWithLanguage(query, genreFilter, langFilter);
        movieList.setAll(results);
    }

    @FXML
    private void handleResetSearch() {
        txtSearch.clear();
        comboFilterGenre.setValue("All Genres");
        comboFilterLanguage.setValue("All Languages");
        loadMovies();
    }

    @FXML
    private void handleDeleteSelected() {
        Movie selected = tableMovies.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Selection Error", "Please select a movie from the table to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete: " + selected.getTitle());
        alert.setContentText("Are you sure you want to permanently delete this movie?");
        alert.getDialogPane().setStyle("-fx-background-color: #1F1F1F;");
        alert.getDialogPane().lookup(".content.label").setStyle("-fx-text-fill: white;");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = movieRepository.deleteMovie(selected.getId());
            if (success) {
                AlertHelper.showInfo("Success", "Movie deleted.");
                handleClearForm();
                loadMovies();
            } else {
                AlertHelper.showError("Database Error", "Failed to delete movie.");
            }
        }
    }

    @FXML
    private void handleClearForm() {
        this.selectedMovie = null;
        lblFormTitle.setText("Add New Movie");
        txtTitle.clear();
        comboGenre.setValue(null);
        comboLanguage.setValue("English");
        txtRuntime.clear();
        txtRating.clear();
        txtScore.clear();
        txtPosterUrl.clear();
        txtWatchUrl.clear();
        txtDescription.clear();
        tableMovies.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleSaveMovie() {
        String title       = txtTitle.getText();
        String genre       = comboGenre.getValue();
        String language    = comboLanguage.getValue();
        String runtimeStr  = txtRuntime.getText();
        String ratingStr   = txtRating.getText();
        String scoreStr    = txtScore.getText();
        String posterUrl   = txtPosterUrl.getText();
        String watchUrl    = txtWatchUrl.getText();
        String description = txtDescription.getText();

        // Validation
        if (title == null || title.trim().isEmpty()) {
            AlertHelper.showWarning("Validation", "Title is required."); return;
        }
        if (genre == null) {
            AlertHelper.showWarning("Validation", "Genre is required."); return;
        }

        int runtime;
        try {
            runtime = Integer.parseInt(runtimeStr.trim());
            if (runtime <= 0) { AlertHelper.showWarning("Validation", "Runtime must be positive."); return; }
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Validation", "Enter a valid runtime (minutes)."); return;
        }

        double rating;
        try {
            rating = Double.parseDouble(ratingStr.trim());
            if (rating < 0 || rating > 10) { AlertHelper.showWarning("Validation", "Rating must be 0-10."); return; }
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Validation", "Enter a valid rating."); return;
        }

        int score;
        try {
            score = Integer.parseInt(scoreStr.trim());
            if (score < 1 || score > 10) { AlertHelper.showWarning("Validation", "Score must be 1-10."); return; }
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Validation", "Enter a valid integer score."); return;
        }

        if (selectedMovie == null) {
            // Add mode
            Movie newMovie = new Movie(title, genre, runtime, rating, score, posterUrl, description, watchUrl,
                    language != null ? language : "English");
            boolean success = movieRepository.addMovie(newMovie);
            if (success) {
                AlertHelper.showInfo("Success", "Movie added successfully.");
                handleClearForm(); loadMovies();
            } else {
                AlertHelper.showError("Database Error", "Failed to insert movie.");
            }
        } else {
            // Edit mode
            selectedMovie.setTitle(title);
            selectedMovie.setGenre(genre);
            selectedMovie.setLanguage(language != null ? language : "English");
            selectedMovie.setRuntime(runtime);
            selectedMovie.setRating(rating);
            selectedMovie.setEntertainmentScore(score);
            selectedMovie.setPosterUrl(posterUrl);
            selectedMovie.setWatchUrl(watchUrl);
            selectedMovie.setDescription(description);

            boolean success = movieRepository.updateMovie(selectedMovie);
            if (success) {
                AlertHelper.showInfo("Success", "Movie updated successfully.");
                handleClearForm(); loadMovies();
            } else {
                AlertHelper.showError("Database Error", "Failed to update movie.");
            }
        }
    }
}
