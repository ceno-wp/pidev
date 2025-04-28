package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import tn.esprit.models.Case;
import tn.esprit.services.CaseService;
import javafx.scene.layout.VBox;  // Add this at the top
import java.time.format.DateTimeFormatter;  // Add this for date formatting
import java.io.IOException;

public class AdminDashboardController {
    @FXML private ListView<Case> listView;
    @FXML private AnchorPane contentPane;
    @FXML private BarChart<String, Number> typeChart;
    private final CaseService caseService = new CaseService();


    @FXML
    public void initialize() {
        initializeChart();
    }


    @FXML
    private void handleDiscover() {
        loadPage("/HomePage.fxml");  // Replace with your actual home page FXML
    }

    private void loadPage(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            // Get the stage from any initialized UI component
            Stage stage = (Stage) contentPane.getScene().getWindow(); // Changed from listView to contentPane
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Failed to load page: " + fxmlPath);
        }
    }
    public void setCurrentUser(ManageUserController.User user) {
        // You can use this user data if needed
    }



    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void initializeChart() {
        try {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            caseService.getCaseTypeCounts().forEach((type, count) ->
                    series.getData().add(new XYChart.Data<>(type, count))
            );
            typeChart.getData().clear();
            typeChart.getData().add(series);
        } catch (Exception e) {
            showErrorAlert("Chart Error", "Failed to load chart data: " + e.getMessage());
        }
    }

    @FXML
    private void showOverview() {
        contentPane.getChildren().clear();
        initializeChart(); // Refresh data every time
        contentPane.getChildren().add(typeChart);
    }







    @FXML
    private void showAllCases() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminCases.fxml"));
            Parent casesView = loader.load();
            contentPane.getChildren().setAll(casesView);
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Failed to load cases view");
        }
    }


    @FXML
    private void ManageUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminManageUser.fxml"));
            Parent casesView = loader.load();
            contentPane.getChildren().setAll(casesView);
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Failed to load cases view");
        }
    }

    @FXML
    private void handleBack() {
        loadPage("/HomePage.fxml");  // Replace with your actual home page FXML
    }




}