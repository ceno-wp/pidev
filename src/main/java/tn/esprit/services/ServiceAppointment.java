package tn.esprit.services;

import tn.esprit.interfaces.IService;
import tn.esprit.models.Appointment;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceAppointment implements IService<Appointment> {

    private Connection cnx;

    // Constructor to initialize the connection
    public ServiceAppointment() {
        try {
            this.cnx = DriverManager.getConnection("jdbc:mysql://localhost:3306/legallink", "root", ""); // Update connection details if needed
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add appointment to the database
    @Override
    public void add(Appointment appointment) {
        String sql = "INSERT INTO `rendez-vous` (date, time_period, description, createdAt) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstm = cnx.prepareStatement(sql)) {
            pstm.setTimestamp(1, Timestamp.valueOf(appointment.getDate()));
            pstm.setString(2, appointment.getTime_period());
            pstm.setString(3, appointment.getDescription());
            pstm.setTimestamp(4, Timestamp.valueOf(appointment.getCreatedAt()));
            pstm.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding appointment: " + e.getMessage());
        }
    }

    // Method to get all appointments
    @Override

    public List<Appointment> getAll() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM `rendez-vous`";
        try (Statement stmt = cnx.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(rs.getInt("id"));
                appointment.setDate(rs.getTimestamp("date").toLocalDateTime());
                appointment.setTime_period(rs.getString("time_period"));
                appointment.setDescription(rs.getString("description"));
                appointment.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving appointments: " + e.getMessage());
        }
        return appointments;
    }

    // Method to get conflicting appointments
    public List<Appointment> getConflictingAppointments(LocalDateTime dateTime) {
        List<Appointment> conflicts = new ArrayList<>();
        String sql = "SELECT * FROM `rendez-vous` WHERE date = ?";

        try (PreparedStatement pstm = cnx.prepareStatement(sql)) {
            pstm.setTimestamp(1, Timestamp.valueOf(dateTime));
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                Appointment a = new Appointment();
                a.setId(rs.getInt("id"));
                a.setDate(rs.getTimestamp("date").toLocalDateTime());
                a.setTime_period(rs.getString("time_period"));
                a.setDescription(rs.getString("description"));
                a.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                conflicts.add(a);
            }
        } catch (SQLException e) {
            System.out.println("Error checking for conflicts: " + e.getMessage());
        }
        return conflicts;
    }

    // Method to suggest an alternative time slot if the desired time is taken
    public LocalDateTime suggestAlternativeTimeSlot(LocalDateTime originalDateTime) {
        LocalDateTime currentDateTime = originalDateTime;

        // Loop until we find an available time slot
        while (true) {
            // Check for conflicts in the morning slot (from 9:00 AM to 12:00 PM)
            LocalDateTime morningSlot = currentDateTime.withHour(9).withMinute(0).withSecond(0).withNano(0);
            if (!isAppointmentExists(morningSlot)) {
                return morningSlot;
            }

            // Check for conflicts in the noon slot (from 1:00 PM to 4:00 PM)
            LocalDateTime noonSlot = currentDateTime.withHour(13).withMinute(0).withSecond(0).withNano(0);
            if (!isAppointmentExists(noonSlot)) {
                return noonSlot;
            }

            // Check for conflicts in the evening slot (from 4:30 PM to 7:00 PM)
            LocalDateTime eveningSlot = currentDateTime.withHour(16).withMinute(30).withSecond(0).withNano(0);
            if (!isAppointmentExists(eveningSlot)) {
                return eveningSlot;
            }

            // Move to the next day if all slots are taken
            currentDateTime = currentDateTime.plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
        }
    }

    // Helper method to check if an appointment already exists at a specific time
    public boolean isAppointmentExists(LocalDateTime dateTime) {
        String sql = "SELECT * FROM `rendez-vous` WHERE date = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(sql)) {
            pstm.setTimestamp(1, Timestamp.valueOf(dateTime));
            ResultSet rs = pstm.executeQuery();
            return rs.next();  // Returns true if there's a conflicting appointment
        } catch (SQLException e) {
            System.out.println("Error checking appointment existence: " + e.getMessage());
        }
        return false;
    }

    // Method to update appointment
    public void update(Appointment appointment) {
        String sql = "UPDATE `rendez-vous` SET date = ?, time_period = ?, description = ?, createdAt = ? WHERE id = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(sql)) {
            pstm.setTimestamp(1, Timestamp.valueOf(appointment.getDate()));
            pstm.setString(2, appointment.getTime_period());
            pstm.setString(3, appointment.getDescription());
            pstm.setTimestamp(4, Timestamp.valueOf(appointment.getCreatedAt()));
            pstm.setInt(5, appointment.getId());
            pstm.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating appointment: " + e.getMessage());
        }
    }

    @Override
    public void delete(Appointment appointment) {

    }

    // Method to delete appointment
    public void delete(int id) {
        String sql = "DELETE FROM `rendez-vous` WHERE id = ?";
        try (PreparedStatement pstm = cnx.prepareStatement(sql)) {
            pstm.setInt(1, id);
            pstm.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting appointment: " + e.getMessage());
        }
    }
    public Map<LocalDate, Integer> getAppointmentsPerDay() throws SQLException {
        Map<LocalDate, Integer> stats = new HashMap<>();
        String sql = "SELECT DATE(date) AS day, COUNT(*) FROM `rendez-vouz` GROUP BY day";
        Statement stmt = cnx.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            stats.put(rs.getDate(1).toLocalDate(), rs.getInt(2));
        }
        return stats;
    }


}
