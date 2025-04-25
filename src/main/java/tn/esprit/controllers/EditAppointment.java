package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import tn.esprit.models.Appointment;
import tn.esprit.services.ServiceAppointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class EditAppointment {

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField timeField;

    @FXML
    private ComboBox<String> statusCombo;

    @FXML
    private TextArea descriptionArea;

    private Appointment appointment;

    public void setAppointmentToEdit(Appointment appointment) {
        this.appointment = appointment;
        datePicker.setValue(appointment.getDate().toLocalDate());
        timeField.setText(appointment.getDate().toLocalTime().toString());
        statusCombo.setValue(appointment.getStatus());
        descriptionArea.setText(appointment.getDescription());
    }

    @FXML
    void save(ActionEvent event) {
        LocalDate date = datePicker.getValue();
        LocalTime time = LocalTime.parse(timeField.getText());

        LocalDateTime dateTime = LocalDateTime.of(date, time);
        appointment.setDate(dateTime);
        appointment.setStatus(statusCombo.getValue());
        appointment.setDescription(descriptionArea.getText());

        new ServiceAppointment().update(appointment);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Appointment Updated");
        alert.setContentText("The appointment was successfully updated.");
        alert.showAndWait();

        ((Button) event.getSource()).getScene().getWindow().hide();
    }
}
