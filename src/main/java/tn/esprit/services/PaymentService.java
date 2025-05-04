package tn.esprit.services;

import tn.esprit.models.Payment;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentService {

    private final Connection connection;

    public PaymentService() {
        this.connection = MyDataBase.getInstance().getCnx();
    }

    // Create a new payment
    public boolean addPayment(Payment payment) {
        String query = "INSERT INTO payment (appointment_id, payment_status, payment_date, amount, payment_method) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, payment.getAppointmentId());
            stmt.setString(2, payment.getPaymentStatus());
            stmt.setTimestamp(3, Timestamp.valueOf(payment.getPaymentDate()));
            stmt.setBigDecimal(4, payment.getAmount());
            stmt.setString(5, payment.getPaymentMethod());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Read a payment by ID
    public Payment getPaymentById(int paymentId) {
        String query = "SELECT * FROM payment WHERE payment_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, paymentId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapToPayment(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update payment status
    public boolean updatePaymentStatus(int paymentId, String newStatus) {
        String query = "UPDATE payment SET payment_status = ? WHERE payment_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, paymentId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete a payment by ID
    public boolean deletePayment(int paymentId) {
        String query = "DELETE FROM payment WHERE payment_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, paymentId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all payments (to fetch payments for a particular appointment or general list)
    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        String query = "SELECT * FROM payment";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                payments.add(mapToPayment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    // Helper method to map ResultSet to Payment object
    private Payment mapToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setAppointmentId(rs.getInt("appointment_id"));
        payment.setPaymentStatus(rs.getString("payment_status"));
        payment.setPaymentDate(rs.getTimestamp("payment_date").toLocalDateTime());
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setPaymentMethod(rs.getString("payment_method"));
        return payment;
    }
}
