package tn.esprit.services;


import tn.esprit.models.Claim;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClaimService {
    // Acquire a fresh (or reconnected) Connection each time
    private Connection getCnx() {
        return MyDataBase.getInstance().getCnx();
    }

    public void createClaim(Claim claim) throws SQLException {
        String query = "INSERT INTO claim (case_id, lawyer_id, client_id, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = getCnx();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, claim.getCaseId());
            stmt.setInt(2, claim.getLawyerId());
            stmt.setInt(3, claim.getClientId());
            stmt.setString(4, "PENDING");
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    claim.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Claim> getPendingClaims(int clientId) throws SQLException {
        List<Claim> claims = new ArrayList<>();
        String query = "SELECT * FROM claim WHERE client_id = ? AND status = 'PENDING'";

        try (Connection conn = getCnx();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Claim claim = new Claim(
                            rs.getInt("case_id"),
                            rs.getInt("lawyer_id"),
                            rs.getInt("client_id")
                    );
                    claim.setId(rs.getInt("id"));
                    claim.setStatus(rs.getString("status"));
                    claim.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    claims.add(claim);
                }
            }
        }
        return claims;
    }

    public void updateClaimStatus(int claimId, String status) throws SQLException {
        String query = "UPDATE claim SET status = ? WHERE id = ?";

        try (Connection conn = getCnx();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, status);
            stmt.setInt(2, claimId);
            stmt.executeUpdate();
        }
    }
}