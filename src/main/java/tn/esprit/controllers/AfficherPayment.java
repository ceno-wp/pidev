package tn.esprit.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.models.Payment;
import tn.esprit.services.PaymentService;

import java.util.List;

public class AfficherPayment {

    @FXML private TableView<Payment> paymentTable;
    @FXML private TableColumn<Payment, Integer> paymentIdColumn;
    @FXML private TableColumn<Payment, Integer> appointmentIdColumn;
    @FXML private TableColumn<Payment, String> paymentStatusColumn;
    @FXML private TableColumn<Payment, String> paymentMethodColumn;
    @FXML private TableColumn<Payment, String> paymentDateColumn;
    @FXML private TableColumn<Payment, String> amountColumn;

    private final PaymentService paymentService = new PaymentService();

    @FXML
    public void initialize() {
        // Bind each column's value to the respective property in the Payment model
        paymentIdColumn.setCellValueFactory(cellData -> cellData.getValue().paymentIdProperty().asObject());
        appointmentIdColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentIdProperty().asObject());
        paymentStatusColumn.setCellValueFactory(cellData -> cellData.getValue().paymentStatusProperty());
        paymentMethodColumn.setCellValueFactory(cellData -> cellData.getValue().paymentMethodProperty());
        paymentDateColumn.setCellValueFactory(cellData -> cellData.getValue().paymentDateProperty().asString());

        // Convert BigDecimal to String for display in the TableColumn
        amountColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getAmount().toString())
        );

        loadPayments();
    }

    private void loadPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        paymentTable.getItems().setAll(payments);
    }

}
