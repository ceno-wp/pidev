package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.models.Appointment;
import tn.esprit.services.ServiceAppointment;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AjouterAppointment {

    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextArea descriptionArea;

    private final ServiceAppointment serviceAppointment = new ServiceAppointment();

    @FXML
    public void initialize() {
        statusCombo.getItems().addAll("Morning", "Noon", "Evening");
        statusCombo.setValue(statusCombo.getItems().get(0));

        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                boolean isPast   = date.isBefore(LocalDate.now());
                boolean isSunday = date.getDayOfWeek().getValue() == 7;
                if (isPast || isSunday) {
                    setDisable(true);
                    setStyle("-fx-background-color: #eeeeee;");
                }
            }
        });
    }

    @FXML
    void saveButton(ActionEvent event) {
        String errorMsg = validateInputs();
        if (!errorMsg.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", errorMsg);
            return;
        }

        try {
            LocalDate      date     = datePicker.getValue();
            LocalDateTime  dateTime = date.atStartOfDay();

            Appointment appointment = new Appointment();
            appointment.setDate(dateTime);
            appointment.setStatus(statusCombo.getValue());
            appointment.setDescription(descriptionArea.getText().trim());
            appointment.setCreatedAt(LocalDateTime.now());

            serviceAppointment.add(appointment);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment added successfully!");
            clearForm();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add appointment: " + e.getMessage());
        }
    }

    private String validateInputs() {
        StringBuilder errorMsg = new StringBuilder();

        if (datePicker.getValue() == null)
            errorMsg.append("Please select a date.\n");

        if (statusCombo.getValue()==null || statusCombo.getValue().trim().isEmpty())
            errorMsg.append("Please select a time slot.\n");

        if (descriptionArea.getText().trim().isEmpty())
            errorMsg.append("Please enter a description.\n");

        return errorMsg.toString();
    }

    private void clearForm() {
        datePicker.setValue(null);
        statusCombo.setValue(statusCombo.getItems().get(0));
        descriptionArea.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void display(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherAppointment.fxml"));
            Parent    root   = loader.load();
            Stage     stage  = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Cannot open display page: " + e.getMessage());
        }
    }
}
