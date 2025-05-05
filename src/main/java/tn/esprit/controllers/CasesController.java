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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.models.Case;
import tn.esprit.services.CaseService;
import tn.esprit.utils.SessionManager;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class CasesController {
    @FXML private ListView<Case> listView;
    private final CaseService caseService = new CaseService();

    @FXML
    public void initialize() {
        setupListView();
        loadCases();
    }

    private void setupListView() {
        listView.setCellFactory(param -> new ListCell<Case>() {
            @Override
            protected void updateItem(Case caseObj, boolean empty) {
                super.updateItem(caseObj, empty);
                if (empty || caseObj == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/CaseCell.fxml"));
                        VBox cellRoot = loader.load();

                        Label titleLabel = (Label) cellRoot.lookup("#titleLabel");
                        Label typeLabel = (Label) cellRoot.lookup("#typeLabel");
                        Label locationLabel = (Label) cellRoot.lookup("#locationLabel");
                        Label dateLabel = (Label) cellRoot.lookup("#dateLabel");

                        if (titleLabel != null) titleLabel.setText(caseObj.getTitle());
                        if (typeLabel != null) typeLabel.setText(caseObj.getType());
                        if (locationLabel != null) locationLabel.setText(caseObj.getLocation());
                        if (dateLabel != null) dateLabel.setText(caseObj.getCreatedAt()
                                .format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));

                        setGraphic(cellRoot);
                    } catch (IOException e) {
                        setText("Error loading cell content");
                        showErrorAlert("UI Error", "Failed to load case cell: " + e.getMessage());
                    }
                }
            }
        });
        listView.setOnMouseClicked(this::handleCaseClick);
    }

    @FXML
    private void handleAppointment() {
        try {
            // Load the FXML file
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterAppointment.fxml"));

            // Create new stage (window)
            Stage appointmentStage = new Stage();
            appointmentStage.setTitle("Ajouter Appointment");
            appointmentStage.setScene(new Scene(root));

            // Show the new window without closing the current one
            appointmentStage.show();

        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Failed to open appointment window: " + e.getMessage());
        }
    }

    private void loadCases() {
        try {
            listView.getItems().setAll(caseService.getCases());
        } catch (Exception e) {
            showErrorAlert("Data Error", "Failed to load cases: " + e.getMessage());
        }
    }

    @FXML
    private void handleCaseClick(MouseEvent event) {
        if (event.getClickCount() == 1) {
            Case selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                openCaseDetails(selected);
            }
        }
    }

    private void openCaseDetails(Case selectedCase) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CaseDetails.fxml"));
            Parent root = loader.load();

            CaseDetailsController controller = loader.getController();
            controller.initData(selectedCase);

            // Replace current scene instead of opening new window
            Stage currentStage = (Stage) listView.getScene().getWindow();
            currentStage.setScene(new Scene(root));
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Failed to open case details: " + e.getMessage());
        }
    }

    @FXML
    private void handleSettings() {
        ManageUserController.User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            showErrorAlert("Access Denied", "No user logged in");
            return;
        }

        String role = currentUser.getRole().toUpperCase();
        String page = "/LawyerDashboard.fxml"; // Default for lawyers

        if (role.contains("ROLE_ADMIN")) {
            page = "/AdminDashboard.fxml";
        } else if (role.contains("ROLE_CLIENT")) {
            page = "/ClientDashboard.fxml";
        }

        loadPage(page);
    }

    @FXML
    private void handlePostCase() {
        loadPage("/Post_Case.fxml");
    }

    @FXML
    private void handleDiscover() {
        loadPage("/HomePage.fxml");
    }

    @FXML
    private void handleCases() {
        ManageUserController.User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            showErrorAlert("Access Denied", "No user logged in");
            return;
        }

        String page = switch (currentUser.getRole().toUpperCase()) {
            case "ROLE_CLIENT" -> "/Cases2.fxml";
            case "ROLE_LAWYER" -> "/Cases.fxml";
            default -> "/Cases.fxml"; // Admin default
        };
        loadPage(page);
    }



    private void loadPage(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage currentStage = (Stage) listView.getScene().getWindow();
            currentStage.setScene(new Scene(root));
        } catch (IOException e) {
            showErrorAlert("Navigation Error", "Failed to load page: " + fxmlPath + "\n" + e.getMessage());
        }
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}


