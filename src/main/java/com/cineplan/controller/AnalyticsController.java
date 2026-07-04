package com.cineplan.controller;

import com.cineplan.model.Marathon;
import com.cineplan.model.Movie;
import com.cineplan.repository.MarathonRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyticsController {

    @FXML private Label lblTotalPlannedMovies;
    @FXML private Label lblAvgRuntime;
    @FXML private Label lblTopGenre;
    @FXML private Label lblTopMood;

    @FXML private PieChart chartGenreDistribution;
    @FXML private BarChart<String, Number> chartMoodUsage;

    private final MarathonRepository marathonRepository = new MarathonRepository();

    @FXML
    public void initialize() {
        loadAnalyticsData();
    }

    private void loadAnalyticsData() {
        List<Marathon> marathons = marathonRepository.getAllMarathons();
        
        if (marathons.isEmpty()) {
            lblTotalPlannedMovies.setText("0");
            lblAvgRuntime.setText("0 min");
            lblTopGenre.setText("N/A");
            lblTopMood.setText("N/A");
            chartGenreDistribution.setData(FXCollections.observableArrayList());
            chartMoodUsage.setData(FXCollections.observableArrayList());
            return;
        }

        int totalMovies = 0;
        int totalRuntime = 0;
        Map<String, Integer> genreCounts = new HashMap<>();
        Map<String, Integer> moodCounts = new HashMap<>();

        for (Marathon m : marathons) {
            moodCounts.put(m.getMood(), moodCounts.getOrDefault(m.getMood(), 0) + 1);

            for (Movie movie : m.getMovies()) {
                totalMovies++;
                totalRuntime += movie.getRuntime();
                genreCounts.put(movie.getGenre(), genreCounts.getOrDefault(movie.getGenre(), 0) + 1);
            }
        }

        // 1. Total Movies Planned
        lblTotalPlannedMovies.setText(String.valueOf(totalMovies));

        // 2. Average Runtime
        int avgRuntime = totalMovies > 0 ? (totalRuntime / totalMovies) : 0;
        lblAvgRuntime.setText(avgRuntime + " min");

        // 3. Most Selected Genre
        String topGenre = "N/A";
        int maxGenreCount = -1;
        for (Map.Entry<String, Integer> entry : genreCounts.entrySet()) {
            if (entry.getValue() > maxGenreCount) {
                maxGenreCount = entry.getValue();
                topGenre = entry.getKey();
            }
        }
        lblTopGenre.setText(topGenre);

        // 4. Most Used Mood
        String topMood = "N/A";
        int maxMoodCount = -1;
        for (Map.Entry<String, Integer> entry : moodCounts.entrySet()) {
            if (entry.getValue() > maxMoodCount) {
                maxMoodCount = entry.getValue();
                topMood = entry.getKey();
            }
        }
        lblTopMood.setText(topMood);

        // 5. Genre Distribution PieChart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : genreCounts.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        chartGenreDistribution.setData(pieChartData);

        // 6. Mood Usage BarChart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<String, Integer> entry : moodCounts.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        
        ObservableList<XYChart.Series<String, Number>> barChartData = FXCollections.observableArrayList();
        barChartData.add(series);
        chartMoodUsage.setData(barChartData);
    }
}
