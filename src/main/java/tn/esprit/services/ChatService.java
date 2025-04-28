package tn.esprit.services;

import tn.esprit.utils.MyDataBase;
import java.sql.*;

public class ChatService {
    public void saveMessage(int senderId, int receiverId, String content, String messageId) {
        try (Connection conn = MyDataBase.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO messages (sender_id, receiver_id, content, message_id) VALUES (?, ?, ?, ?)")) {

            pstmt.setInt(1, senderId);
            pstmt.setInt(2, receiverId);
            pstmt.setString(3, content);
            pstmt.setString(4, messageId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error saving message: " + e.getMessage());
        }
    }
}