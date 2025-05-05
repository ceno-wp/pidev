package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import tn.esprit.models.Case;
import javafx.fxml.FXMLLoader;
import tn.esprit.models.Claim;
import tn.esprit.services.ClaimService;
import tn.esprit.utils.MyDataBase;
import tn.esprit.utils.SessionManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CaseDetailsController {
    @FXML private Label titleLabel;
    @FXML private Label typeLabel;
    @FXML private Label locationLabel;
    @FXML private Label dateLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label phoneLabel;

    private final ClaimService claimService = new ClaimService();
    private Case currentCase;

    public void initData(Case caseObj) {
        this.currentCase = caseObj;  // Initialize currentCase

        if (currentCase == null) {
            showErrorAlert("Error", "No case data provided");
            return;
        }

        // Initialize UI components
        titleLabel.setText(currentCase.getTitle() + "    ");
        typeLabel.setText(currentCase.getType() + "    ");
        locationLabel.setText(currentCase.getLocation() + "    ");
        dateLabel.setText(currentCase.getCreatedAt().toLocalDate().toString() + "    ");
        descriptionLabel.setText(currentCase.getDescription());
        phoneLabel.setText("  " + currentCase.getTelephone());
        // After login
        System.out.println("Logged in with role: " + SessionManager.getCurrentUser().getRole());
    }

    @FXML
    private void handleClaim() {
        if (currentCase == null) {
            showErrorAlert("Error", "No case selected");
            return;
        }

        ManageUserController.User currentUser = SessionManager.getCurrentUser();

        if (currentUser == null) {
            showErrorAlert("Permission Denied", "No user logged in");
            return;
        }

        // Fix role check for array-based roles
        String roles = currentUser.getRole().toUpperCase().replaceAll("[\\[\\]\"]", "");
        if (!roles.contains("ROLE_LAWYER")) {
            showErrorAlert("Permission Denied", "Only lawyers can claim cases");
            return;
        }

        try {
            Claim claim = new Claim(
                    currentCase.getId(),
                    currentUser.getId(),
                    currentCase.getUserId()
            );

            claimService.createClaim(claim);
            showSuccessAlert("Claim Submitted", "Your claim request has been sent to the client");

        } catch (SQLException e) {
            showErrorAlert("Database Error", "Failed to submit claim: " + e.getMessage());
        }
    }

    @FXML
    private void handleMessage() {
        try {
            int caseOwnerId = currentCase.getUserId();
            String caseOwnerName = findUserNameById(caseOwnerId);
            String userRole = SessionManager.getCurrentUser().getRole().toUpperCase();

            // Determine which dashboard to load
            String dashboardFXML = userRole.contains("LAWYER")
                    ? "/LawyerDashboard.fxml"
                    : "/ClientDashboard.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(dashboardFXML));
            Parent dashboard = loader.load();

            // Set chat target on the dashboard controller
            if (userRole.contains("LAWYER")) {
                LawyerDashboardController controller = loader.getController();
                controller.setChatTarget(caseOwnerId, caseOwnerName );
            } else {
                ClientDashboardController controller = loader.getController();
                controller.setChatTarget(caseOwnerId, caseOwnerName );
            }

            // Get current stage and switch scene
            Stage stage = (Stage) titleLabel.getScene().getWindow();
            stage.setScene(new Scene(dashboard));

        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Failed to start chat: " + e.getMessage());
        }
    }
    private String findUserNameById(int userId) {
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement pstmt = cnx.prepareStatement("SELECT name FROM user WHERE id = ?")) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("name");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // or you can return "Unknown" or throw an exception if you prefer
    }







    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/HomePage.fxml"));
            Stage stage = (Stage) titleLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            showErrorAlert("Navigation Error", "Failed to return to cases list: " + e.getMessage());
        }
    }

    private void showSuccessAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}