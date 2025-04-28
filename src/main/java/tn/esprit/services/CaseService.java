package tn.esprit.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tn.esprit.models.Case;
import tn.esprit.utils.MyDataBase;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaseService {
    private int claimedById;
    private String claimStatus;

    // Remove class-level connection - get fresh connection for each operation
    public CaseService() {
        // No initial connection needed
    }

    private final Connection cnx = MyDataBase.getInstance().getCnx();




    public List<String> validateCase(Case caseObj) {
        // Validation logic remains the same
        List<String> errors = new ArrayList<>();
        if (caseObj.getTitle().isEmpty()) errors.add("Title is required");
        if (caseObj.getLocation().isEmpty()) errors.add("Location is required");
        if (caseObj.getDescription().isEmpty()) errors.add("Description is required");
        if (caseObj.getTelephone().isEmpty()) errors.add("Telephone is required");

        if (caseObj.getTitle().length() > 30)
            errors.add("Title must be ≤30 characters");
        if (caseObj.getLocation().length() > 30)
            errors.add("Location must be ≤30 characters");
        if (caseObj.getDescription().length() > 500)
            errors.add("Description must be ≤500 characters");
        if (caseObj.getTelephone().length() > 15)
            errors.add("Telephone must be ≤15 digits");

        if (!caseObj.getTelephone().matches("^[0-9]+$")) {
            errors.add("Telephone must contain only numbers");
        }
        return errors;
    }

    public Case getCaseById(int caseId) throws SQLException {
        String sql = "SELECT * FROM legal_case WHERE id = ?";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, caseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Case c = new Case(
                            rs.getInt("user_id"),
                            rs.getString("title"),
                            rs.getString("location"),
                            rs.getString("type"),
                            rs.getString("visibility"),
                            rs.getString("description"),
                            rs.getString("telephone")
                    );
                    c.setId(rs.getInt("id"));
                    c.setStatus(rs.getString("status"));
                    c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    // and any other fields (claimed_by_id, claim_status, resolved_at) if your Case model supports them
                    return c;
                }
            }
        }
        return null;
    }

    public ObservableList<Case> getClaimedCasesObservable(int lawyerId) {
        ObservableList<Case> claimedCases = FXCollections.observableArrayList();
        // Add your database query logic here to get cases claimed by the lawyer
        String query = "SELECT * FROM legal_case WHERE claimed_by_id = ?";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement pstmt = cnx.prepareStatement(query)) {
            pstmt.setInt(1, lawyerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                claimedCases.add(createCaseFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return claimedCases;
    }

    private Case createCaseFromResultSet(ResultSet rs) throws SQLException {
        Case caseObj = new Case(
                rs.getInt("user_id"),
                rs.getString("title"),
                rs.getString("location"),
                rs.getString("type"),
                rs.getString("visibility"),
                rs.getString("description"),
                rs.getString("telephone")
        );

        caseObj.setId(rs.getInt("id"));
        caseObj.setStatus(rs.getString("status"));

        Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
        if (createdAtTimestamp != null) {
            caseObj.setCreatedAt(createdAtTimestamp.toLocalDateTime());
        } else {
            // If created_at is null, set a default value to avoid crash
            caseObj.setCreatedAt(LocalDateTime.now());
        }

        // Also set claimed_by_id and claim_status if needed
        int claimedById = rs.getInt("claimed_by_id");
        if (!rs.wasNull()) {  // Check if claimed_by_id is not null
            caseObj.setClaimed_by_id(String.valueOf(claimedById));
        }

        String claimStatus = rs.getString("claim_status");
        if (claimStatus != null) {
            caseObj.setClaimStatus(claimStatus);
        }

        return caseObj;
    }



    public List<Case> getClientCases(int userId) {
        List<Case> cases = new ArrayList<>();
        String query = "SELECT * FROM legal_case WHERE user_id = ?";

        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement stmt = cnx.prepareStatement(query)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Case caseObj = new Case(
                            rs.getInt("user_id"),
                            rs.getString("title"),
                            rs.getString("location"),
                            rs.getString("type"),
                            rs.getString("visibility"),
                            rs.getString("description"),
                            rs.getString("telephone")

                    );
                    caseObj.setId(rs.getInt("id"));
                    caseObj.setStatus(rs.getString("status"));
                    caseObj.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    cases.add(caseObj);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading client cases: " + e.getMessage());
        }
        return cases;
    }

    public ObservableList<Case> getClientCasesObservable(int userId) {
        return FXCollections.observableArrayList(getClientCases(userId));
    }

    public List<String> saveCase(Case caseObj) {
        List<String> errors = validateCase(caseObj);
        if (!errors.isEmpty()) return errors;

        String query = "INSERT INTO legal_case (user_id, title, location, type, visibility, "
                + "description, telephone, status, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement stmt = cnx.prepareStatement(query)) {

            stmt.setInt(1, caseObj.getUserId());
            stmt.setString(2, caseObj.getTitle());
            stmt.setString(3, caseObj.getLocation());
            stmt.setString(4, caseObj.getType());
            stmt.setString(5, caseObj.getVisibility());
            stmt.setString(6, caseObj.getDescription());
            stmt.setString(7, caseObj.getTelephone());
            stmt.setString(8, caseObj.getStatus());
            stmt.setTimestamp(9, Timestamp.valueOf(caseObj.getCreatedAt()));

            stmt.executeUpdate();

        } catch (SQLException e) {
            errors.add("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        return errors;
    }

    public void updateCaseClaimStatus(int caseId, int claimedById, String status, LocalDateTime resolvedAt) {
        String query = "UPDATE legal_case SET claimed_by_id = ?, claim_status = ?, resolved_at = ? WHERE id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, claimedById);
            stmt.setString(2, status);
            stmt.setTimestamp(3, Timestamp.valueOf(resolvedAt));
            stmt.setInt(4, caseId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update case claim status", e);
        }
    }


    public List<Case> getCases() {
        List<Case> cases = new ArrayList<>();
        String query = "SELECT * FROM legal_case";

        try (Connection cnx = MyDataBase.getInstance().getCnx();
             Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Case caseObj = new Case(
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("location"),
                        rs.getString("type"),
                        rs.getString("visibility"),
                        rs.getString("description"),
                        rs.getString("telephone")
                );
                caseObj.setId(rs.getInt("id"));
                caseObj.setStatus(rs.getString("status"));
                caseObj.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                cases.add(caseObj);
            }
        } catch (SQLException e) {
            System.err.println("Error loading cases: " + e.getMessage());
        }
        return cases;
    }

    public ObservableList<Case> getCasesObservable() {
        return FXCollections.observableArrayList(getCases());
    }

    public Map<String, Integer> getCaseTypeCounts() {
        Map<String, Integer> counts = new HashMap<>();
        String query = "SELECT type, COUNT(*) as count FROM legal_case GROUP BY type";

        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement stmt = cnx.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                counts.put(rs.getString("type"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting type counts: " + e.getMessage());
        }
        return counts;
    }

    public void updateStatus(int id, String newStatus) {
        updateField(id, "status", newStatus);
    }

    public void updateVisibility(int id, String newVisibility) {
        updateField(id, "visibility", newVisibility);
    }

    private void updateField(int id, String field, String value) {
        String query = String.format("UPDATE legal_case SET %s = ? WHERE id = ?", field);

        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement stmt = cnx.prepareStatement(query)) {

            stmt.setString(1, value);
            stmt.setInt(2, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating " + field + ": " + e.getMessage());
        }
    }

    public void assignToLawyer(int caseId, int lawyerId) throws SQLException {
        String query = "UPDATE legal_case SET claimed_by_id = ? WHERE id = ?";
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, lawyerId);
            stmt.setInt(2, caseId);
            stmt.executeUpdate();
        }
    }

    public void deleteCase(int caseId) {
        String query = "DELETE FROM legal_case WHERE id = ?";

        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement stmt = cnx.prepareStatement(query)) {

            stmt.setInt(1, caseId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting case: " + e.getMessage());
        }
    }
}