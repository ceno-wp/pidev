package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import tn.esprit.models.Claim;
import tn.esprit.services.CaseService;
import tn.esprit.services.ClaimService;
import tn.esprit.controllers.ManageUserController.User;
import tn.esprit.utils.SessionManager;

import java.time.LocalDateTime;

public class ClientClaimsController {
    @FXML private ListView<Claim> claimsListView;
    private final ClaimService claimService = new ClaimService();
    private final CaseService caseService = new CaseService();

    @FXML
    public void initialize() {
        loadClaims();
        setupListView();
    }

    private void loadClaims() {
        if (SessionManager.getCurrentUser() == null) {
            System.out.println("No user in session");
            return;
        }
        int clientId = SessionManager.getCurrentUser().getId();
        try {
            claimsListView.getItems().setAll(
                    claimService.getPendingClaims(clientId)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupListView() {
        claimsListView.setCellFactory(list -> new ListCell<>() {
            private final Label caseLabel   = new Label();
            private final Label lawyerLabel = new Label();
            private final Button acceptBtn  = new Button("Accept");
            private final Button rejectBtn  = new Button("Reject");
            private final HBox container     = new HBox(10);

            {
                // Label styles
                caseLabel.setFont(Font.font("Consolas", 14));
                lawyerLabel.setFont(Font.font("Consolas", 14));
                lawyerLabel.setStyle("-fx-text-fill: #666;");

                // Button colors
                acceptBtn.setStyle("-fx-background-color: #08ff40; -fx-text-fill: white; -fx-background-radius: 5;");
                rejectBtn.setStyle("-fx-background-color: #ff2020; -fx-text-fill: white; -fx-background-radius: 5;");

                // Actions
                acceptBtn.setOnAction(e -> handleAccept());
                rejectBtn.setOnAction(e -> handleReject());

                // Spacer
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                // Build layout
                container.getChildren().setAll(
                        caseLabel,
                        lawyerLabel,
                        spacer,
                        acceptBtn,
                        rejectBtn
                );
                container.setStyle("-fx-padding: 8; -fx-alignment: CENTER_LEFT;");
            }

            @Override
            protected void updateItem(Claim claim, boolean empty) {
                super.updateItem(claim, empty);
                if (empty || claim == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // Fetch and display case title
                    String title = "";
                    try {
                        title = caseService.getCaseById(claim.getCaseId()).getTitle();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    caseLabel.setText("Case: " + title);

                    // Fetch and display lawyer name
                    User lawyer = User.findById(claim.getLawyerId());
                    String name = (lawyer != null ? lawyer.getName() : "Unknown");
                    lawyerLabel.setText("Lawyer: " + name);

                    setGraphic(container);
                }
            }

            private void handleAccept() {
                Claim claim = getItem();
                if (claim == null) return;
                try {
                    claimService.updateClaimStatus(claim.getId(), "ACCEPTED");
                    caseService.updateCaseClaimStatus(
                            claim.getCaseId(),
                            claim.getLawyerId(),
                            "ACCEPTED",
                            LocalDateTime.now()
                    );
                    loadClaims();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private void handleReject() {
                Claim claim = getItem();
                if (claim == null) return;
                try {
                    claimService.updateClaimStatus(claim.getId(), "REJECTED");
                    loadClaims();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
