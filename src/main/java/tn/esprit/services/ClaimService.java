package tn.esprit.services;

import tn.esprit.models.Claim;
import tn.esprit.utils.MyDataBase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClaimService {
    private final Connection cnx = MyDataBase.getInstance().getCnx();

    public void createClaim(Claim claim) throws SQLException {
        // Changed table name to 'claim'
        String query = "INSERT INTO claim (case_id, lawyer_id, client_id, status) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, claim.getCaseId());
            stmt.setInt(2, claim.getLawyerId());
            stmt.setInt(3, claim.getClientId());
            stmt.setString(4, "PENDING");
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) claim.setId(rs.getInt(1));
            }
        }
    }

    public List<Claim> getPendingClaims(int clientId) throws SQLException {
        List<Claim> claims = new ArrayList<>();
        // Changed table name to 'claim'
        String query = "SELECT * FROM claim WHERE client_id = ? AND status = 'PENDING'";

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();

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
        return claims;
    }

    public void updateClaimStatus(int claimId, String status) throws SQLException {
        // Changed table name to 'claim'
        String query = "UPDATE claim SET status = ? WHERE id = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, claimId);
            stmt.executeUpdate();
        }
    }
}