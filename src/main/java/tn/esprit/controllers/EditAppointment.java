package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import tn.esprit.models.Appointment;
import tn.esprit.services.ServiceAppointment;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EditAppointment {

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> time_periodCombo;

    @FXML
    private TextArea descriptionArea;

    private Appointment appointment;

    public void setAppointmentToEdit(Appointment appointment) {
        this.appointment = appointment;
        datePicker.setValue(appointment.getDate().toLocalDate());
        time_periodCombo.setValue(appointment.getTime_period());
        descriptionArea.setText(appointment.getDescription());
    }

    @FXML
    void save(ActionEvent event) {
        LocalDate date = datePicker.getValue();
        LocalDateTime dateTime = date.atStartOfDay(); // Time will default to 00:00

        // Check if an appointment already exists at the selected date and time
        if (new ServiceAppointment().isAppointmentExists(dateTime)) {
            showAlert(Alert.AlertType.ERROR, "Time Slot Unavailable", "An appointment already exists at this time.");
            return;
        }

        appointment.setDate(dateTime);
        appointment.setTime_period(time_periodCombo.getValue());
        appointment.setDescription(descriptionArea.getText());

        new ServiceAppointment().update(appointment);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Appointment Updated");
        alert.setContentText("The appointment was successfully updated.");
        alert.showAndWait();

        ((Button) event.getSource()).getScene().getWindow().hide();
    }

    // Create the showAlert method
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
