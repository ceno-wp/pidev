package tn.esprit.services;

import tn.esprit.interfaces.IService;
import tn.esprit.models.Appointment;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceAppointment implements IService<Appointment> {

    private Connection cnx;

    public ServiceAppointment() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    // Add this method to check if an appointment exists at the specified date and time
    public boolean isAppointmentExists(LocalDateTime dateTime) {
        String sql = "SELECT COUNT(*) FROM `rendez-vous` WHERE date = ?";

        try (PreparedStatement pstm = cnx.prepareStatement(sql)) {
            pstm.setTimestamp(1, Timestamp.valueOf(dateTime));
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Return true if there's an existing appointment
            }
        } catch (SQLException e) {
            System.out.println("Error checking if appointment exists: " + e.getMessage());
        }
        return false; // No appointment found at the same time
    }

    @Override
    public void add(Appointment a) {
        String qry = "INSERT INTO `rendez-vous`(date, time_period, description, createdAt) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setTimestamp(1, Timestamp.valueOf(a.getDate()));
            pstm.setString(2, a.getTime_period());
            pstm.setString(3, a.getDescription());
            pstm.setTimestamp(4, Timestamp.valueOf(a.getCreatedAt()));
            pstm.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error adding appointment: " + e.getMessage());
        }
    }

    @Override
    public List<Appointment> getAll() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM `rendez-vous`";

        try (Statement stmt = cnx.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Appointment a = new Appointment();
                a.setId(rs.getInt("id"));
                a.setDate(rs.getTimestamp("date").toLocalDateTime());
                a.setTime_period(rs.getString("time_period"));
                a.setDescription(rs.getString("description"));
                a.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                appointments.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }

    @Override
    public void update(Appointment a) {
        String qry = "UPDATE `rendez-vous` SET date=?, time_period=?, description=?, createdAt=? WHERE id=?";

        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setTimestamp(1, Timestamp.valueOf(a.getDate()));
            pstm.setString(2, a.getTime_period());
            pstm.setString(3, a.getDescription());
            pstm.setTimestamp(4, Timestamp.valueOf(a.getCreatedAt()));
            pstm.setInt(5, a.getId());
            pstm.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error updating appointment: " + e.getMessage());
        }
    }

    @Override
    public void delete(Appointment a) {
        String qry = "DELETE FROM `rendez-vous` WHERE id=?";

        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setInt(1, a.getId());
            pstm.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error deleting appointment: " + e.getMessage());
        }
    }
}