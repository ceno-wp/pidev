package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.models.Payment;
import tn.esprit.services.PaymentService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EditPayment {

    @FXML private TextField paymentIdField;
    @FXML private TextField appointmentIdField;
    @FXML private TextField amountField;
    @FXML private ComboBox<String> paymentStatusCombo;
    @FXML private ComboBox<String> paymentMethodCombo;
    @FXML private DatePicker paymentDatePicker;

    private final PaymentService paymentService = new PaymentService();

    @FXML
    public void initialize() {
        paymentStatusCombo.getItems().addAll("Pending", "Paid", "Failed");
        paymentStatusCombo.setValue("Pending");

        paymentMethodCombo.getItems().addAll("Credit Card", "PayPal", "Bank Transfer");
        paymentMethodCombo.setValue("Credit Card");
    }

    @FXML
    void loadPaymentDetails(ActionEvent event) {
        try {
            int paymentId = Integer.parseInt(paymentIdField.getText().trim());
            Payment payment = paymentService.getPaymentById(paymentId);
            if (payment != null) {
                appointmentIdField.setText(String.valueOf(payment.getAppointmentId()));
                amountField.setText(String.valueOf(payment.getAmount()));
                paymentStatusCombo.setValue(payment.getPaymentStatus());
                paymentMethodCombo.setValue(payment.getPaymentMethod());
                paymentDatePicker.setValue(payment.getPaymentDate().toLocalDate());
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Payment not found.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load payment details: " + e.getMessage());
        }
    }

    @FXML
    void updatePayment(ActionEvent event) {
        String errorMsg = validateInputs();
        if (!errorMsg.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", errorMsg);
            return;
        }

        try {
            Payment payment = new Payment();
            payment.setPaymentId(Integer.parseInt(paymentIdField.getText().trim()));
            payment.setAppointmentId(Integer.parseInt(appointmentIdField.getText().trim()));
            payment.setPaymentStatus(paymentStatusCombo.getValue());
            payment.setAmount(new BigDecimal(amountField.getText().trim()));
            payment.setPaymentMethod(paymentMethodCombo.getValue());
            payment.setPaymentDate(paymentDatePicker.getValue().atStartOfDay());

            // Update payment
            boolean isUpdated = paymentService.updatePaymentStatus(payment.getPaymentId(), payment.getPaymentStatus());
            if (isUpdated) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Payment updated successfully!");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update payment.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update payment: " + e.getMessage());
        }
    }

    private String validateInputs() {
        StringBuilder errorMsg = new StringBuilder();

        if (paymentIdField.getText().trim().isEmpty())
            errorMsg.append("Please enter the payment ID.\n");

        if (appointmentIdField.getText().trim().isEmpty())
            errorMsg.append("Please enter the appointment ID.\n");

        if (amountField.getText().trim().isEmpty())
            errorMsg.append("Please enter the payment amount.\n");

        if (paymentDatePicker.getValue() == null)
            errorMsg.append("Please select the payment date.\n");

        return errorMsg.toString();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
