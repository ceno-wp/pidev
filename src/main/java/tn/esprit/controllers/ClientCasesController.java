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
import tn.esprit.models.Case;
import tn.esprit.services.CaseService;
import tn.esprit.utils.SessionManager;

import java.time.format.DateTimeFormatter;

public class ClientCasesController {
    @FXML private ListView<Case> casesListView;
    private final CaseService caseService = new CaseService();

    @FXML
    public void initialize() {
        int userId = SessionManager.getCurrentUser().getId();
        casesListView.setItems(caseService.getClientCasesObservable(userId));
        casesListView.setCellFactory(list -> new ClientCaseCell());
    }

    private class ClientCaseCell extends ListCell<Case> {
        private final Label titleLabel       = new Label();
        private final Label dateLabel        = new Label();
        private final Button statusBtn       = new Button();
        private final Button visibilityBtn   = new Button();
        private final HBox  container        = new HBox(10);  // 10px gap

        ClientCaseCell() {
            // Style labels
            titleLabel.setFont(Font.font("Consolas", 14));
            dateLabel.setFont(Font.font("Consolas", 12));
            dateLabel.setStyle("-fx-text-fill: #888;");

            // Style buttons
            statusBtn.setStyle(
                    "-fx-background-color: #28a745; -fx-text-fill: white; -fx-background-radius: 5;");
            visibilityBtn.setStyle(
                    "-fx-background-color: #012c80; -fx-text-fill: white; -fx-background-radius: 5;");

            // Button actions
            statusBtn.setOnAction(e -> updateStatus());
            visibilityBtn.setOnAction(e -> updateVisibility());

            // Spacer to push buttons to the right
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // Assemble cell layout
            HBox infoBox = new HBox(5, titleLabel, dateLabel);
            container.getChildren().addAll(infoBox, spacer, statusBtn, visibilityBtn);
            container.setStyle("-fx-padding: 8; -fx-alignment: CENTER_LEFT;");
        }

        @Override
        protected void updateItem(Case caseObj, boolean empty) {
            super.updateItem(caseObj, empty);
            if (empty || caseObj == null) {
                setGraphic(null);
                setText(null);
            } else {
                titleLabel.setText(caseObj.getTitle());
                dateLabel.setText(caseObj.getCreatedAt()
                        .format(DateTimeFormatter.ISO_DATE));
                statusBtn.setText(caseObj.getStatus());
                visibilityBtn.setText(caseObj.getVisibility());
                setGraphic(container);
            }
        }

        private void updateStatus() {
            Case c = getItem();
            String next = switch (c.getStatus()) {
                case "open"    -> "pending";
                case "pending" -> "closed";
                case "closed"  -> "open";
                default        -> "open";
            };
            caseService.updateStatus(c.getId(), next);
            c.setStatus(next);
            statusBtn.setText(next);
        }

        private void updateVisibility() {
            Case c = getItem();
            String next = "public".equals(c.getVisibility()) ? "unlisted" : "public";
            caseService.updateVisibility(c.getId(), next);
            c.setVisibility(next);
            visibilityBtn.setText(next);
        }
    }
}

