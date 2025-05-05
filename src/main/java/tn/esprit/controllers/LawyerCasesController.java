package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import tn.esprit.models.Case;
import tn.esprit.services.CaseService;
import tn.esprit.utils.SessionManager;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class LawyerCasesController {
    @FXML private ListView<Case> casesListView;
    private final CaseService caseService = new CaseService();


    @FXML
    public void initialize() {
        int lawyerId = SessionManager.getCurrentUser().getId();
        casesListView.setItems(caseService.getClaimedCasesObservable(lawyerId));
        casesListView.setCellFactory(list -> new ClaimedCaseCell());
    }



    private void showErrorAlert(String navigationError, String s) {
    }

    private class ClaimedCaseCell extends ListCell<Case> {
        private final Label titleLabel = new Label();
        private final Label dateLabel = new Label();
        private final Button closeBtn = new Button();
        private final HBox container = new HBox(10);

        ClaimedCaseCell() {
            // Style elements
            titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            dateLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
            closeBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

            // Add spacing between elements
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            container.getChildren().addAll(titleLabel, dateLabel, spacer, closeBtn);
            container.setStyle("-fx-padding: 8; -fx-alignment: CENTER_LEFT;");

            // Button action
            closeBtn.setOnAction(e -> closeCase());
        }

        @Override
        protected void updateItem(Case caseObj, boolean empty) {
            super.updateItem(caseObj, empty);
            if (empty || caseObj == null) {
                setGraphic(null);
                setText(null);
            } else {
                titleLabel.setText(caseObj.getTitle());
                dateLabel.setText("Posted: " + caseObj.getCreatedAt()
                        .format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
                closeBtn.setText(caseObj.getStatus());
                setGraphic(container);

            }
        }

        private void closeCase() {
            Case c = getItem();
            String next = switch (c.getStatus()) {
                case "open"    -> "pending";
                case "pending" -> "closed";
                case "closed"  -> "open";
                default        -> "open";
            };
            caseService.updateStatus(c.getId(), next);
            c.setStatus(next);
            closeBtn.setText(next);
        }
    }
}