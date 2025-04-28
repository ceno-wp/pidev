package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import tn.esprit.controllers.ManageUserController;
import tn.esprit.utils.SessionManager;

public class HomePageController {
    @FXML private AnchorPane contentPane;
    @FXML
    private ListView<Case> listView;


    @FXML
    private void handlePostCase() {
        loadPage("/Post_Case.fxml");
    }

    @FXML
    private void handleDiscover() {
        loadPage("/HomePage.fxml");  // Replace with your actual home page FXML
    }

    @FXML
    private void handleCases() {
        ManageUserController.User currentUser = SessionManager.getCurrentUser();

        if (currentUser == null) {
            showErrorAlert("Access Error", "No user logged in");
            return;
        }

        // Check roles directly from the user's roles string
        String roles = currentUser.getRole().toUpperCase();

        if (roles.contains("ROLE_CLIENT")) {
            loadPage("/Cases2.fxml");
        } else if (roles.contains("ROLE_LAWYER")) {
            loadPage("/Cases.fxml");
        } else {
            // Default to admin dashboard
            loadPage("/Cases.fxml");
        }

    }




    @FXML
    private void handleSettings() {
        ManageUserController.User currentUser = SessionManager.getCurrentUser();

        if (currentUser == null) {
            showErrorAlert("Access Error", "No user logged in");
            return;
        }

        // Check roles directly from the user's roles string
        String roles = currentUser.getRole().toUpperCase();

        if (roles.contains("ROLE_CLIENT")) {
            loadPage("/ClientDashboard.fxml");
        } else if (roles.contains("ROLE_LAWYER")) {
            loadPage("/LawyerDashboard.fxml");
        } else {
            // Default to admin dashboard
            loadPage("/AdminDashboard.fxml");
        }



    }

    public void setCurrentUser(ManageUserController.User user) {
        // You can use this user data if needed
    }



    private void loadPage(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) listView.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Failed to load page: " + fxmlPath);
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

    }

    @FXML
    private void handleSignout() {
        SessionManager.logout();
        loadPage("/login.fxml");
    }


}
