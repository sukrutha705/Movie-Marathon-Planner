package com.cineplan.controller;

import com.cineplan.model.Marathon;
import com.cineplan.model.Movie;
import com.cineplan.repository.MarathonRepository;
import com.cineplan.service.PDFExportService;
import com.cineplan.utils.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import java.io.File;
import java.util.List;
import java.util.Optional;

public class SavedMarathonsController {

    @FXML private ListView<Marathon> listMarathons;

    // Detail elements
    @FXML private Label lblMarathonName;
    @FXML private Label lblMarathonMeta;
    @FXML private Button btnExportPDF;
    
    @FXML private TableView<Movie> tableMovies;
    @FXML private TableColumn<Movie, String> colTitle;
    @FXML private TableColumn<Movie, String> colGenre;
    @FXML private TableColumn<Movie, Integer> colRuntime;
    @FXML private TableColumn<Movie, Double> colRating;

    private final MarathonRepository marathonRepository = new MarathonRepository();
    private final ObservableList<Marathon> marathonList = FXCollections.observableArrayList();
    private final ObservableList<Movie> movieDetailList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // TableView bindings
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colRuntime.setCellValueFactory(new PropertyValueFactory<>("runtime"));
        colRating.setCellValueFactory(new PropertyValueFactory<>("rating"));

        // Set custom ListView cells
        listMarathons.setCellFactory(lv -> new ListCell<Marathon>() {
            @Override
            protected void updateItem(Marathon item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getMarathonName() + "\n" + 
                            "Mood: " + item.getMood() + " • " + item.getTotalRuntime() + " min • Score: " + item.getTotalScore());
                    setStyle("-fx-text-fill: white; -fx-padding: 10px; -fx-background-color: transparent; -fx-border-color: #2D2D2D transparent transparent transparent;");
                }
            }
        });

        // Load list
        loadMarathons();

        // Listener for selections
        listMarathons.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                displayMarathonDetails(newSel);
            } else {
                clearDetails();
            }
        });
    }

    private void loadMarathons() {
        List<Marathon> dbMarathons = marathonRepository.getAllMarathons();
        marathonList.setAll(dbMarathons);
        listMarathons.setItems(marathonList);
    }

    private void displayMarathonDetails(Marathon marathon) {
        lblMarathonName.setText(marathon.getMarathonName());
        lblMarathonMeta.setText("Mood: " + marathon.getMood() + " | Total Runtime: " + marathon.getTotalRuntime() + 
                                " min | Total Score: " + marathon.getTotalScore() + " | Planned on: " + marathon.getCreatedAt());
        
        movieDetailList.setAll(marathon.getMovies());
        tableMovies.setItems(movieDetailList);
        tableMovies.setVisible(true);
        btnExportPDF.setVisible(true);
    }

    private void clearDetails() {
        lblMarathonName.setText("Select a Marathon");
        lblMarathonMeta.setText("Select a saved marathon from the left to view details.");
        tableMovies.setVisible(false);
        btnExportPDF.setVisible(false);
        movieDetailList.clear();
    }

    @FXML
    private void handleDeleteMarathon() {
        Marathon selected = listMarathons.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Selection Error", "Please select a saved marathon to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Marathon");
        alert.setHeaderText("Delete: " + selected.getMarathonName());
        alert.setContentText("Are you sure you want to permanently delete this marathon schedule?");
        
        // Dark style
        alert.getDialogPane().setStyle("-fx-background-color: #1F1F1F; -fx-text-fill: white;");
        alert.getDialogPane().lookup(".content.label").setStyle("-fx-text-fill: white;");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = marathonRepository.deleteMarathon(selected.getId());
            if (success) {
                AlertHelper.showInfo("Success", "Marathon plan deleted.");
                loadMarathons();
                clearDetails();
            } else {
                AlertHelper.showError("Database Error", "Failed to delete marathon plan.");
            }
        }
    }

    @FXML
    private void handleExportPDF() {
        Marathon selected = listMarathons.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Saved Marathon to PDF");
        fileChooser.setInitialFileName(selected.getMarathonName().replaceAll("[^a-zA-Z0-9.-]", "_") + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));

        File file = fileChooser.showSaveDialog(lblMarathonName.getScene().getWindow());
        if (file != null) {
            try {
                PDFExportService.exportMarathon(selected, file);
                AlertHelper.showInfo("Export Completed", "Marathon report successfully saved at:\n" + file.getAbsolutePath());
            } catch (Exception e) {
                AlertHelper.showError("Export Error", "Failed to export PDF:\n" + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
