package tn.esprit.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import tn.esprit.models.Case;
import tn.esprit.models.Claim;
import tn.esprit.services.ClaimService;

import java.io.IOException;

public class ClientDashboardController {
    @FXML private ListView<Case> listView;
    @FXML private BorderPane contentPane;
    @FXML private ListView<Claim> claimsListView;
    private Integer chatTargetId;
    private String chatTargetName;
    private ChatController chatController;

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
    private void showOverview() {
        contentPane.getChildren().clear();
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
