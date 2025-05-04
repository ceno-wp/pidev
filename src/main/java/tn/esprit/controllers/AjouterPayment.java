package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.models.Payment;
import tn.esprit.services.PaymentService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AjouterPayment {

    @FXML private TextField appointmentIdField;
    @FXML private TextField amountField;
    @FXML private ComboBox<String> paymentStatusCombo;
    @FXML private ComboBox<String> paymentMethodCombo;
    @FXML private DatePicker paymentDatePicker;

    private final PaymentService paymentService = new PaymentService();

    @FXML
    public void initialize() {
        // Setup payment status and method combo boxes
        paymentStatusCombo.getItems().addAll("Pending", "Paid", "Failed");
        paymentStatusCombo.setValue("Pending");

        paymentMethodCombo.getItems().addAll("Credit Card", "PayPal", "Bank Transfer");
        paymentMethodCombo.setValue("Credit Card");
    }

    @FXML
    void savePayment(ActionEvent event) {
        String errorMsg = validateInputs();
        if (!errorMsg.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", errorMsg);
            return;
        }

        try {
            Payment payment = new Payment();
            payment.setAppointmentId(Integer.parseInt(appointmentIdField.getText().trim()));
            payment.setPaymentStatus(paymentStatusCombo.getValue());
            payment.setAmount(new BigDecimal(amountField.getText().trim()));
            payment.setPaymentMethod(paymentMethodCombo.getValue());
            payment.setPaymentDate(paymentDatePicker.getValue().atStartOfDay());

            // Save payment
            if (paymentService.addPayment(payment)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Payment added successfully!");
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add payment.");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add payment: " + e.getMessage());
        }
    }

    private String validateInputs() {
        StringBuilder errorMsg = new StringBuilder();

        if (appointmentIdField.getText().trim().isEmpty())
            errorMsg.append("Please enter the appointment ID.\n");

        if (amountField.getText().trim().isEmpty())
            errorMsg.append("Please enter the payment amount.\n");

        if (paymentDatePicker.getValue() == null)
            errorMsg.append("Please select the payment date.\n");

        return errorMsg.toString();
    }

    private void clearForm() {
        appointmentIdField.clear();
        amountField.clear();
        paymentStatusCombo.setValue("Pending");
        paymentMethodCombo.setValue("Credit Card");
        paymentDatePicker.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
