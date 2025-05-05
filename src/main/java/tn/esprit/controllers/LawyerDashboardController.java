package tn.esprit.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import tn.esprit.models.Case;
import tn.esprit.utils.SessionManager;

import java.io.IOException;

public class LawyerDashboardController {
    @FXML private ListView<Case> listView;
    @FXML private BorderPane contentPane;
    private Integer chatTargetId;

    @FXML
    private void handleDiscover() {
        loadPage("/HomePage.fxml");  // Replace with your actual home page FXML
    }

    public void setCurrentUser(ManageUserController.User user) {
        // You can use this user data if needed
    }

    private String chatTargetName;

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
    private void showOverview() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Clientdash.fxml"));
            Parent casesView = loader.load();
            contentPane.getChildren().setAll(casesView);
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Failed to load cases view");
        }
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
    private void handleSignOut() {
        SessionManager.logout();
        loadPage("/login.fxml");
    }




    @FXML
    private void showMyCases() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LawyerCases.fxml"));
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

