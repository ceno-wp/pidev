package tn.esprit.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import tn.esprit.models.Case;
import tn.esprit.models.Claim;
import tn.esprit.services.ClaimService;
import tn.esprit.utils.SessionManager;

import java.io.IOException;

public class ClientDashboardController {
    @FXML private ListView<Case> listView;
    @FXML private BorderPane contentPane;
    @FXML private ListView<Claim> claimsListView;
    private Integer chatTargetId;
    private String chatTargetName;
    private ChatController chatController;
    @FXML private AreaChart<String, Number> areaChart;
    @FXML private ScatterChart<String, Number> scatterChart;

    public void setChatTarget(int partnerId, String partnerName) {
        this.chatTargetId = partnerId;
        this.chatTargetName = partnerName;
        showMessages();
    }

    @FXML
    private void showMessages() {
        try {
            // Load chat window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chat.fxml"));
            Parent chatRoot = loader.load();

            // Get controller and initialize
            ChatController chatController = loader.getController();
            if (chatTargetId != null) {
                chatController.initializeConversation(chatTargetId, chatTargetName);
            }

            // Create new window
            Stage chatStage = new Stage();
            chatStage.setTitle("Chat Messages");
            chatStage.setScene(new Scene(chatRoot));
            chatStage.show();

        } catch (IOException e) {
            showErrorAlert("Error", "Failed to open chat window");
        }
    }

    @FXML
    private void handleSignOut() {
        SessionManager.logout();
        loadPage("/login.fxml");
    }







    @FXML
    private void showClaims() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientClaims.fxml"));
            Parent claimsView = loader.load();
            contentPane.getChildren().setAll(claimsView);

            ClientClaimsController controller = loader.getController();
            controller.initialize();
        } catch (IOException e) {
            showErrorAlert("Navigation Error", e.getMessage());
        }
    }
    @FXML
    private void handleDiscover() {
        loadPage("/HomePage.fxml");  // Replace with your actual home page FXML
    }

    public void setCurrentUser(ManageUserController.User user) {
        // If you need to process user data
    }







    @FXML
    public void initialize() {
        initializeCharts();
    }

    private void initializeCharts() {
        // Area Chart Data
        XYChart.Series<String, Number> areaSeries = new XYChart.Series<>();
        areaSeries.setName("Cases Overview");
        areaSeries.getData().addAll(
                new XYChart.Data<>("Jan", 12),
                new XYChart.Data<>("Feb", 15),
                new XYChart.Data<>("Mar", 9),
                new XYChart.Data<>("Apr", 18)
        );
        areaChart.getData().add(areaSeries);

        // Scatter Chart Data
        XYChart.Series<String, Number> scatterSeries = new XYChart.Series<>();
        scatterSeries.setName("Case Resolution Time");
        scatterSeries.getData().addAll(
                new XYChart.Data<>("Simple Cases", 5),
                new XYChart.Data<>("Complex Cases", 15),
                new XYChart.Data<>("Appeals", 25)
        );
        scatterChart.getData().add(scatterSeries);

        // Style the charts
        areaChart.setStyle("-fx-background-color: #FEF3E2; -fx-padding: 15;");
        scatterChart.setStyle("-fx-background-color: #FEF3E2; -fx-padding: 15;");
    }

    @FXML
    private void showOverview() {
        // Clear and reinitialize the charts
        contentPane.getChildren().clear();
        initializeCharts();

        // Create a container for the charts
        GridPane chartsContainer = new GridPane();
        chartsContainer.add(areaChart, 0, 0);
        chartsContainer.add(scatterChart, 1, 0);
        chartsContainer.setHgap(15);
        chartsContainer.setVgap(15);
        chartsContainer.setPadding(new Insets(15));

        // Add to content pane
        contentPane.setCenter(chartsContainer);
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

    @FXML
    private void showMyCases() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ClientCases.fxml"));
            Parent casesView = loader.load();
            contentPane.getChildren().setAll(casesView);
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Failed to load cases view");
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBack() {
        loadPage("/HomePage.fxml");  // Replace with your actual home page FXML
    }






}
