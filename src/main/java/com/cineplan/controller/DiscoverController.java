package com.cineplan.controller;

import com.cineplan.model.Movie;
import com.cineplan.repository.MovieRepository;
import com.cineplan.utils.AlertHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class DiscoverController {

    @FXML private TextField txtSearchQuery;
    @FXML private Label lblStatus;
    @FXML private FlowPane flowResults;

    private final MovieRepository movieRepository = new MovieRepository();
    private static final String API_KEY = "thewdb";

    @FXML
    public void initialize() {
        lblStatus.setText("Enter a movie title above to search the global IMDb catalog.");
    }

    @FXML
    private void handleSearch() {
        String query = txtSearchQuery.getText();
        if (query == null || query.trim().isEmpty()) {
            AlertHelper.showWarning("Search Query Empty", "Please type a movie title to search.");
            return;
        }

        lblStatus.setText("Searching global database...");
        flowResults.getChildren().clear();

        new Thread(() -> {
            try {
                String encodedQuery = URLEncoder.encode(query.trim(), "UTF-8");
                String urlString = "https://www.omdbapi.com/?apikey=" + API_KEY + "&s=" + encodedQuery + "&type=movie";
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(4000);
                connection.setReadTimeout(4000);

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                        StringBuilder response = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        String json = response.toString();
                        List<SearchResult> results = parseSearchResults(json);
                        
                        Platform.runLater(() -> {
                            if (results.isEmpty()) {
                                lblStatus.setText("No movies found. Try another search query.");
                            } else {
                                lblStatus.setText("Found " + results.size() + " matches. Click 'Import' to add to your CinePlan library.");
                                int delay = 0;
                                for (SearchResult res : results) {
                                    VBox card = createResultCard(res);
                                    card.setOpacity(0.0);
                                    flowResults.getChildren().add(card);
                                    
                                    javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(javafx.util.Duration.millis(300), card);
                                    fade.setToValue(1.0);
                                    fade.setDelay(javafx.util.Duration.millis(delay * 60));
                                    fade.play();
                                    delay++;
                                }
                            }
                        });
                    }
                } else {
                    Platform.runLater(() -> lblStatus.setText("Search failed. OMDb server returned code: " + responseCode));
                }
            } catch (Exception e) {
                Platform.runLater(() -> lblStatus.setText("Error connecting to search API: " + e.getMessage()));
            }
        }).start();
    }

    private VBox createResultCard(SearchResult res) {
        VBox card = new VBox(8);
        card.getStyleClass().add("movie-card");
        card.setPrefWidth(180);
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.TOP_CENTER);

        // Poster image
        StackPane posterContainer = new StackPane();
        posterContainer.setPrefSize(160, 200);

        boolean posterLoaded = false;
        if (res.poster != null && !res.poster.trim().isEmpty() && !"N/A".equalsIgnoreCase(res.poster)) {
            try {
                Image img = new Image(res.poster, 160, 200, true, true, true);
                if (!img.isError()) {
                    ImageView view = new ImageView(img);
                    view.setFitWidth(160);
                    view.setFitHeight(200);
                    posterContainer.getChildren().add(view);
                    posterLoaded = true;
                }
            } catch (Exception ignored) {}
        }

        if (!posterLoaded) {
            Label fallback = new Label(res.title);
            fallback.setWrapText(true);
            fallback.setAlignment(Pos.CENTER);
            fallback.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px; -fx-text-alignment: center; -fx-padding: 8px;");
            posterContainer.setStyle("-fx-background-color: #2b2b2b; -fx-background-radius: 6px;");
            posterContainer.getChildren().add(fallback);
        }

        // Title and Year
        Label titleLabel = new Label(res.title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: white;");
        titleLabel.setWrapText(true);
        titleLabel.setPrefHeight(34);
        titleLabel.setAlignment(Pos.CENTER);

        Label yearLabel = new Label(res.year);
        yearLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 11px;");

        // Import Button
        Button btnImport = new Button("📥 Import Movie");
        btnImport.setPrefWidth(160);
        btnImport.setStyle(
            "-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-weight: bold; " +
            "-fx-background-radius: 4px; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 6px 0;"
        );

        btnImport.setOnAction(e -> {
            btnImport.setDisable(true);
            btnImport.setText("Importing...");
            importMovieDetails(res, btnImport);
        });

        // Hover effect for import button
        btnImport.setOnMouseEntered(e -> {
            if (!btnImport.isDisable()) {
                btnImport.setStyle(
                    "-fx-background-color: #FF1A1A; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-background-radius: 4px; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 6px 0;"
                );
            }
        });
        btnImport.setOnMouseExited(e -> {
            if (!btnImport.isDisable()) {
                btnImport.setStyle(
                    "-fx-background-color: #E50914; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-background-radius: 4px; -fx-cursor: hand; -fx-font-size: 11px; -fx-padding: 6px 0;"
                );
            }
        });

        card.getChildren().addAll(posterContainer, titleLabel, yearLabel, btnImport);
        return card;
    }

    private void importMovieDetails(SearchResult res, Button btnImport) {
        new Thread(() -> {
            try {
                String urlString = "https://www.omdbapi.com/?apikey=" + API_KEY + "&i=" + res.imdbID;
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(4000);
                connection.setReadTimeout(4000);

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                        StringBuilder response = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        String json = response.toString();
                        
                        String responseStatus = extractValue(json, "Response");
                        if ("True".equalsIgnoreCase(responseStatus)) {
                            // Extract detailed fields
                            String runtimeStr = extractValue(json, "Runtime");
                            String genreStr = extractValue(json, "Genre");
                            String ratingStr = extractValue(json, "imdbRating");
                            String languageStr = extractValue(json, "Language");
                            String plot = extractValue(json, "Plot");
                            String posterUrl = extractValue(json, "Poster");

                            // Convert values to CinePlan standards
                            int runtime = parseRuntime(runtimeStr);
                            String genre = parseGenre(genreStr);
                            String language = parseLanguage(languageStr);
                            double rating = parseRating(ratingStr);
                            int entertainmentScore = (int) Math.round(rating);
                            if (entertainmentScore < 1) entertainmentScore = 1;
                            if (entertainmentScore > 10) entertainmentScore = 10;

                            String watchUrl = "https://www.imdb.com/title/" + res.imdbID + "/";
                            if (plot == null || plot.equalsIgnoreCase("N/A")) {
                                plot = "No description available.";
                            }

                            Movie newMovie = new Movie(res.title, genre, runtime, rating, entertainmentScore,
                                    posterUrl, plot, watchUrl, language);

                            boolean added = movieRepository.addMovie(newMovie);
                            
                            Platform.runLater(() -> {
                                if (added) {
                                    btnImport.setText("✓ Imported");
                                    btnImport.setStyle(
                                        "-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-font-weight: bold; " +
                                        "-fx-background-radius: 4px; -fx-font-size: 11px; -fx-padding: 6px 0;"
                                    );
                                    AlertHelper.showInfo("Movie Imported", "'" + res.title + "' has been successfully added to your CinePlan library.");
                                } else {
                                    btnImport.setDisable(false);
                                    btnImport.setText("📥 Import Movie");
                                    AlertHelper.showError("Import Failed", "Could not save the movie to the database.");
                                }
                            });
                        } else {
                            Platform.runLater(() -> {
                                btnImport.setDisable(false);
                                btnImport.setText("📥 Import Movie");
                                AlertHelper.showError("Import Failed", "OMDb API did not return detailed info.");
                            });
                        }
                    }
                } else {
                    Platform.runLater(() -> {
                        btnImport.setDisable(false);
                        btnImport.setText("📥 Import Movie");
                        AlertHelper.showError("Import Failed", "Details API call returned error code: " + responseCode);
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> {
                    btnImport.setDisable(false);
                    btnImport.setText("📥 Import Movie");
                    AlertHelper.showError("Import Error", "Connection failed: " + e.getMessage());
                });
            }
        }).start();
    }

    private List<SearchResult> parseSearchResults(String json) {
        List<SearchResult> list = new ArrayList<>();
        String[] parts = json.split("\\{\"Title\":\"");
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            int titleEnd = part.indexOf("\",");
            if (titleEnd == -1) continue;
            String title = part.substring(0, titleEnd);

            String year = extractField(part, "Year");
            String imdbID = extractField(part, "imdbID");
            String poster = extractField(part, "Poster");
            list.add(new SearchResult(title, year, imdbID, poster));
        }
        return list;
    }

    private String extractField(String part, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = part.indexOf(pattern);
        if (start == -1) return "N/A";
        start += pattern.length();
        int end = part.indexOf("\"", start);
        if (end == -1) return "N/A";
        return part.substring(start, end);
    }

    private String extractValue(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start == -1) return null;
        start += pattern.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return null;
        return json.substring(start, end);
    }

    private int parseRuntime(String runtimeStr) {
        if (runtimeStr == null) return 120;
        try {
            String clean = runtimeStr.replaceAll("[^0-9]", "");
            return Integer.parseInt(clean);
        } catch (Exception e) {
            return 120;
        }
    }

    private String parseGenre(String genreStr) {
        if (genreStr == null) return "Drama";
        String[] genres = genreStr.split(",");
        for (String g : genres) {
            String clean = g.trim();
            // Match against standard genres
            if (clean.equalsIgnoreCase("Action") || clean.equalsIgnoreCase("Comedy") ||
                clean.equalsIgnoreCase("Sci-Fi") || clean.equalsIgnoreCase("Fantasy") ||
                clean.equalsIgnoreCase("Horror") || clean.equalsIgnoreCase("Drama") ||
                clean.equalsIgnoreCase("Animation")) {
                return clean;
            }
        }
        return "Drama"; // Fallback
    }

    private String parseLanguage(String languageStr) {
        if (languageStr == null) return "English";
        String[] langs = languageStr.split(",");
        for (String l : langs) {
            String clean = l.trim();
            if (clean.equalsIgnoreCase("English") || clean.equalsIgnoreCase("Hindi") ||
                clean.equalsIgnoreCase("Telugu") || clean.equalsIgnoreCase("Japanese") ||
                clean.equalsIgnoreCase("Korean") || clean.equalsIgnoreCase("Spanish") ||
                clean.equalsIgnoreCase("French") || clean.equalsIgnoreCase("German")) {
                return clean;
            }
        }
        return "English"; // Fallback
    }

    private double parseRating(String ratingStr) {
        if (ratingStr == null) return 7.0;
        try {
            return Double.parseDouble(ratingStr.trim());
        } catch (Exception e) {
            return 7.0;
        }
    }

    private static class SearchResult {
        String title;
        String year;
        String imdbID;
        String poster;

        SearchResult(String title, String year, String imdbID, String poster) {
            this.title = title;
            this.year = year;
            this.imdbID = imdbID;
            this.poster = poster;
        }
    }
}
