package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.models.Appointment;
import tn.esprit.services.ServiceAppointment;

import java.io.IOException;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class AfficherAppointment {

    @FXML private TextField idField;
    @FXML private DatePicker dateFilter;
    @FXML private TextField keywordField;
    @FXML private ListView<Appointment> display;

    private final ServiceAppointment service = new ServiceAppointment();
    private final ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        allAppointments.addAll(service.getAll());
        display.setItems(allAppointments);

        idField.textProperty().addListener((obs, o, n) -> applyFilters());
        dateFilter.valueProperty().addListener((obs, o, n) -> applyFilters());
        keywordField.textProperty().addListener((obs, o, n) -> applyFilters());
    }

    private void applyFilters() {
        String idText = idField.getText().trim();
        LocalDate date = dateFilter.getValue();
        String kw = keywordField.getText().trim().toLowerCase();

        var filtered = allAppointments.stream()
                .filter(app -> {
                    if (!idText.isEmpty() && !String.valueOf(app.getId()).equals(idText)) return false;
                    if (date != null && !app.getDate().toLocalDate().equals(date)) return false;
                    if (!kw.isEmpty() && !app.getDescription().toLowerCase().contains(kw)
                            && !app.getTime_period().toLowerCase().contains(kw)) return false;
                    return true;
                })
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        display.setItems(filtered);
    }

    @FXML
    void delete(ActionEvent event) {
        var sel = display.getSelectionModel().getSelectedItem();
        if (sel != null) {
            service.delete(sel);
            allAppointments.remove(sel);
            applyFilters();
        } else {
            showAlert("Please select an appointment to delete.");
        }
    }

    @FXML
    void edit(ActionEvent event) {
        var sel = display.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert("Please select an appointment to edit.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditAppointment.fxml"));
            Parent root = loader.load();
            EditAppointment ctrl = loader.getController();
            ctrl.setAppointmentToEdit(sel);

            Stage s = new Stage();
            s.setScene(new Scene(root));
            s.setTitle("Edit Appointment #" + sel.getId());
            s.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error opening edit window: " + e.getMessage());
        }
    }

    @FXML
    private void goToStatistics(ActionEvent event) throws IOException {
        Parent statisticsView = FXMLLoader.load(getClass().getResource("/StatisticsView.fxml"));
        Scene scene = new Scene(statisticsView);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
