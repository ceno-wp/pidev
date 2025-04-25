package tn.esprit.services;

import tn.esprit.interfaces.IService;
import tn.esprit.models.Appointment;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceAppointment implements IService<Appointment> {

    private Connection cnx;

    public ServiceAppointment() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void add(Appointment a) {
        String qry = "INSERT INTO `rendez-vous`(date, status, description, createdAt) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setTimestamp(1, Timestamp.valueOf(a.getDate()));
            pstm.setString(2, a.getStatus());
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
                a.setStatus(rs.getString("status"));
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
        String qry = "UPDATE `rendez-vous` SET date=?, status=?, description=?, createdAt=? WHERE id=?";

        try {
            PreparedStatement pstm = cnx.prepareStatement(qry);
            pstm.setTimestamp(1, Timestamp.valueOf(a.getDate()));
            pstm.setString(2, a.getStatus());
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
