package tn.esprit.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.models.Case;
import tn.esprit.services.CaseService;
import javafx.fxml.FXML;
import tn.esprit.controllers.ManageUserController;
import tn.esprit.utils.SessionManager;

import java.io.IOException;
import java.util.List;

public class PostCaseController {
    @FXML private TextField titleField;
    @FXML private TextField locationField;
    @FXML private ComboBox<String> typeDropdown;
    @FXML private ComboBox<String> visibilityDropdown;
    @FXML private TextArea descriptionArea;
    @FXML private TextField telephoneField;

    private CaseService caseService = new CaseService();

    private ManageUserController.User currentUser;

    @FXML
    public void initialize() {
        // Check authentication and authorization
        currentUser = SessionManager.getCurrentUser();

        if (currentUser == null) {
            showErrorAndRedirect("Authentication Required", "Please login first");
            return;
        }

        if (!currentUser.getRole().toUpperCase().contains("ROLE_CLIENT")) {
            showErrorAndRedirect("Access Denied", "Only clients can post cases");
            return;
        }

        // Initialize form if authorized
        typeDropdown.getItems().addAll("CIVIL", "CRIMINAL", "FAMILY", "IMMIGRATION");
        visibilityDropdown.getItems().addAll("PUBLIC", "UNLISTED");
    }

    @FXML
    private void handleSubmit() {
        // Validate user is still authenticated
        if (currentUser == null || !currentUser.getRole().toUpperCase().contains("ROLE_CLIENT")) {
            showErrorAlert(List.of("Session expired or insufficient privileges"));
            return;
        }

        Case newCase = new Case(
                currentUser.getId(), // Use logged-in client's ID
                titleField.getText().trim(),
                locationField.getText().trim(),
                typeDropdown.getValue(),
                visibilityDropdown.getValue(),
                descriptionArea.getText().trim(),
                telephoneField.getText().trim()
        );

        List<String> errors = caseService.saveCase(newCase);

        if(errors.isEmpty()) {
            navigateToCasesPage();
        } else {
            showErrorAlert(errors);
        }
    }

    private void navigateToCasesPage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Cases2.fxml"));
            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("All Cases");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String navigationError, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Errors");
        alert.setHeaderText("Please fix the following issues:");
        alert.setContentText("idk");
        alert.showAndWait();
    }

    private void showErrorAlert(List<String> errors) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Errors");
        alert.setHeaderText("Please fix the following issues:");
        alert.setContentText(String.join("\n• ", errors));
        alert.showAndWait();
    }

    private void redirectToHome() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/HomePage.fxml"));
            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to redirect");
        }
    }

    private void showErrorAndRedirect(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        redirectToHome();
    }


    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Cases2.fxml"));
            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}