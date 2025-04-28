package tn.esprit.controllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import tn.esprit.models.Case;
import tn.esprit.services.CaseService;
import javafx.scene.layout.VBox;
import tn.esprit.utils.SessionManager;

import java.time.format.DateTimeFormatter;
import java.io.IOException;

public class Cases2Controller {
    @FXML
    private ListView<Case> listView;
    private final CaseService caseService = new CaseService();

    @FXML
    public void initialize() {
        setupListView();
        loadPublicCases(); // Changed to load public cases only
    }

    private void loadPublicCases() {
        ObservableList<Case> allCases = caseService.getCasesObservable();
        ObservableList<Case> publicCases = allCases.filtered(caseObj ->
                caseObj.getVisibility().equalsIgnoreCase("public")
        );
        listView.setItems(publicCases);
    }

    // The rest of your code remains the same
    @FXML
    private void handlePostCase() {
        loadPage("/Post_Case.fxml");
    }

    @FXML
    private void handleDiscover() {
        loadPage("/HomePage.fxml");
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

                        titleLabel.setText(caseObj.getTitle());
                        typeLabel.setText(caseObj.getType());
                        locationLabel.setText(caseObj.getLocation());
                        dateLabel.setText(caseObj.getCreatedAt()
                                .format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));

                        setGraphic(cellRoot);
                    } catch (IOException e) {
                        setText("Error loading cell");
                    }
                }
            }
        });
        listView.setOnMouseClicked(this::handleCaseClick);
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

    private void handleCaseClick(MouseEvent event) {
        Case selected = listView.getSelectionModel().getSelectedItem();
        if (selected != null && event.getClickCount() == 1) {
            openCaseDetails(selected);
        }
    }

    private void openCaseDetails(Case selectedCase) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CaseDetails.fxml"));
            Parent root = loader.load();

            CaseDetailsController controller = loader.getController();
            controller.initData(selectedCase);

            Stage stage = (Stage) listView.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            System.err.println("Error loading case details: " + e.getMessage());
        }
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
}