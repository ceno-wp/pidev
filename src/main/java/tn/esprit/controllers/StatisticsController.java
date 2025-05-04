package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import tn.esprit.services.ServiceAppointment;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

public class StatisticsController {

    @FXML private PieChart statsChart;
    private final ServiceAppointment serviceAppointment = new ServiceAppointment();

    @FXML
    public void initialize() {
        loadStatistics();
    }

    private void loadStatistics() {
        try {
            // Fetch appointment statistics per day from the database
            Map<LocalDate, Integer> stats = serviceAppointment.getAppointmentsPerDay();

            // Clear the current data
            statsChart.getData().clear();

            // Add data to the PieChart
            for (Map.Entry<LocalDate, Integer> entry : stats.entrySet()) {
                statsChart.getData().add(new PieChart.Data(entry.getKey().toString(), entry.getValue()));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error loading statistics: " + e.getMessage());
        }
    }
}
