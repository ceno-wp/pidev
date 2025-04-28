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
import tn.esprit.utils.MailService;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AjouterAppointment {

    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> time_periodCombo;
    @FXML private TextArea descriptionArea;
    @FXML private TextField emailField;

    private final ServiceAppointment serviceAppointment = new ServiceAppointment();

    @FXML
    public void initialize() {
        time_periodCombo.getItems().addAll("Morning", "Noon", "Evening");
        // REMOVE this line below 👇
        // time_periodCombo.setValue(time_periodCombo.getItems().get(0));

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

        // 📧 Email validation BEFORE saving
        String clientEmail = emailField.getText().trim();
        if (clientEmail.isEmpty() || !isValidEmail(clientEmail)) {
            emailField.setStyle("-fx-border-color: red;"); // 🔴 Highlight email field
            showAlert(Alert.AlertType.ERROR, "Invalid Email", "Please enter a valid email address.");
            return;
        } else {
            emailField.setStyle(null); // ✅ Reset style if valid
        }

        try {
            LocalDate date = datePicker.getValue();
            LocalDateTime dateTime = date.atStartOfDay();

            // Check if an appointment already exists at the selected date and time
            if (new ServiceAppointment().isAppointmentExists(dateTime)) {
                showAlert(Alert.AlertType.ERROR, "Time Slot Unavailable", "An appointment already exists at this time.");
                return;
            }

            Appointment appointment = new Appointment();
            appointment.setDate(dateTime);
            appointment.setTime_period(time_periodCombo.getValue());
            appointment.setDescription(descriptionArea.getText().trim());
            appointment.setCreatedAt(LocalDateTime.now());

            serviceAppointment.add(appointment);

            // 📧 Send email after saving appointment
            MailService.sendEmail(
                    clientEmail,
                    "Appointment Confirmation",
                    "Your appointment on " + date + " (" + appointment.getTime_period() + ") has been successfully saved."
            );

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

        if (time_periodCombo.getValue()==null || time_periodCombo.getValue().trim().isEmpty())
            errorMsg.append("Please select a time slot.\n");

        if (descriptionArea.getText().trim().isEmpty())
            errorMsg.append("Please enter a description.\n");

        return errorMsg.toString();
    }

    private void clearForm() {
        datePicker.setValue(null);
        time_periodCombo.setValue(time_periodCombo.getItems().get(0));
        descriptionArea.clear();
    }
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
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