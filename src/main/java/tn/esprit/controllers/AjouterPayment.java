package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import tn.esprit.models.Payment;
import tn.esprit.services.ServicePayment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AjouterPayment {

    @FXML private TextField amountField;
    @FXML private TextField currencyField;
    @FXML private ComboBox<String> methodCombo;
    @FXML private ComboBox<String> statusCombo;

    // Will be set by the caller so we know which appointment this payment is for:
    private int appointmentId;

    private final ServicePayment service = new ServicePayment();

    /** Called by FXMLLoader after controls are injected */
    @FXML
    public void initialize() {
        methodCombo.getItems().addAll("Cash", "Credit Card", "Bank Transfer");
        statusCombo.getItems().addAll("PENDING", "PAID", "FAILED", "REFUNDED");
        // Optionally select defaults:
        methodCombo.getSelectionModel().selectFirst();
        statusCombo.getSelectionModel().select("PENDING");
    }

    /** Setter to receive appointmentId before showing this dialog */
    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    /** Called when user clicks “Ajouter Paiement” */
    @FXML
    private void handleAddPayment() {
        try {
            // Build Payment object
            Payment p = new Payment();
            p.setAppointmentId(appointmentId);
            p.setAmount(new BigDecimal(amountField.getText().trim()));
            p.setCurrency(currencyField.getText().trim().toUpperCase());
            p.setMethod(methodCombo.getValue());
            p.setStatus(statusCombo.getValue());
            p.setPaidAt(LocalDateTime.now());
            // createdAt & updatedAt handled by DB defaults/ON UPDATE

            // Persist
            service.add(p);

            // Success alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Succès");
            alert.setContentText("Paiement enregistré pour RDV #" + appointmentId);
            alert.showAndWait();

            // Optionally close the window:
            amountField.getScene().getWindow().hide();

        } catch (NumberFormatException nfe) {
            showError("Le montant doit être un nombre valide.");
        } catch (Exception ex) {
            showError("Erreur lors de l'enregistrement : " + ex.getMessage());
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Erreur");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
