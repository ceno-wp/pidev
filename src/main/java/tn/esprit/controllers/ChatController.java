package tn.esprit.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import tn.esprit.services.ChatService;
import tn.esprit.utils.SessionManager;
import tn.esprit.utils.MyDataBase;

import java.net.URI;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatController {
    @FXML private ListView<String> chatList;
    @FXML private VBox messageContainer;
    @FXML private TextField messageInput;
    @FXML private Label chatPartnerLabel;
    @FXML private ScrollPane messageScroll;

    private WebSocketClient client;
    private int currentUserId;
    private Map<Integer, String> conversations = new HashMap<>();
    private int currentPartnerId = -1;
    private final ChatService chatService = new ChatService();


    @FXML
    public void initialize() {
        ManageUserController.User currentUser = SessionManager.getCurrentUser();
        currentUserId = currentUser.getId();
        connectToChatServer();
        setupListSelection();
        loadPersistedConversations();
    }

    private void connectToChatServer() {
        try {
            client = new WebSocketClient(new URI("ws://localhost:8887")) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    send(String.valueOf(currentUserId));
                }

                @Override
                public void onMessage(String message) {
                    handleIncomingMessage(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {}

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                }
            };
            client.addHeader("user-id", String.valueOf(currentUserId));
            client.connect();
        } catch (Exception e) {
            showError("Connection Error", "Failed to connect to chat server");
        }
    }

    private void setupListSelection() {
        chatList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentPartnerId = getPartnerIdByName(newVal);
                loadChatHistory();
                chatPartnerLabel.setText("Chat with " + newVal);
            }
        });
    }



    private void loadPersistedConversations() {
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement pstmt = cnx.prepareStatement(
                     "SELECT DISTINCT u.id, u.name FROM user u " +
                             "JOIN messages m ON u.id = m.sender_id OR u.id = m.receiver_id " +
                             "WHERE ? IN (m.sender_id, m.receiver_id) AND u.id != ?")) {

            pstmt.setInt(1, currentUserId);
            pstmt.setInt(2, currentUserId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                conversations.put(rs.getInt("id"), rs.getString("name"));
                chatList.getItems().add(rs.getString("name"));
            }
        } catch (SQLException e) {
            showError("Database Error", "Failed to load conversations");
        }
    }

    private void handleIncomingMessage(String message) {
        Platform.runLater(() -> {
            if (message.startsWith("MSG|")) {
                String[] parts = message.split("\\|", 5);
                // Format: MSG|senderId|senderName|content|messageId
                int senderId = Integer.parseInt(parts[1]);
                String senderName = parts[2];
                String content = parts[3];
                String messageId = parts[4];

                // Update conversations list
                if (!conversations.containsKey(senderId)) {
                    addConversation(senderId, senderName);
                }

                // If in the correct chat
                if (senderId == currentPartnerId || senderId == currentUserId) {
                    addMessageToChat(content, senderId == currentUserId, messageId);
                }
                messageContainer.requestLayout();
                messageScroll.setVvalue(1.0);
            }
        });
    }
    public void initializeConversation(int partnerId, String partnerName) {
        this.currentPartnerId = partnerId;
        this.chatPartnerLabel.setText("Chat with " + partnerName);
        loadChatHistory();

        // Force WebSocket reconnect if needed
        if (client == null || client.isClosed()) {
            connectToChatServer();
        }
    }

    private String getSenderNameFromMessageOrDB(String rawMessage) {
        // If your server sends names in messages (recommended)
        // Format message as "senderId|senderName|content"
        String[] parts = rawMessage.split("\\|", 3);
        if (parts.length >= 3) {
            return parts[1]; // Directly get name from message
        }

        // Fallback to database lookup
        int senderId = Integer.parseInt(parts[0]);
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement pstmt = cnx.prepareStatement(
                     "SELECT name FROM user WHERE id = ?")) {
            pstmt.setInt(1, senderId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getString("name") : "Unknown User";
        } catch (SQLException e) {
            return "Unknown";
        }
    }

    public void addConversation(int partnerId, String partnerName) {
        if (!conversations.containsKey(partnerId)) {
            conversations.put(partnerId, partnerName);
            chatList.getItems().add(partnerName);
        }
        chatList.getSelectionModel().select(partnerName);
    }

    private void addMessageToChat(String message, boolean isSender, String messageId) {
        if (!isDuplicate(messageId)) {
            // Create message bubble
            Text text = new Text(message);
            text.setFill(Color.BLACK);

            TextFlow textFlow = new TextFlow(text);
            textFlow.setMaxWidth(250);
            textFlow.setPadding(new Insets(8, 12, 8, 12));

            // Create container for alignment
            HBox messageBubble = new HBox(textFlow);
            messageBubble.setMaxWidth(messageContainer.getWidth());
            messageBubble.setAlignment(isSender ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            messageBubble.getStyleClass().add(isSender ? "sent-message" : "received-message");

            // Add to chat container
            messageContainer.getChildren().add(messageBubble);

            // Scroll to bottom
            Platform.runLater(() -> messageScroll.setVvalue(1.0));
        }
    }
    private TextFlow createMessageBubble(String message, boolean isSender) {
        TextFlow textFlow = new TextFlow(new Text(message));
        textFlow.setMaxWidth(300);
        textFlow.setStyle("-fx-background-color: " + (isSender ? "#DCF8C6" : "#EAEAEA") +
                "; -fx-padding: 5px;" +
                "; -fx-background-radius: 10px;");
        textFlow.setId(UUID.randomUUID().toString()); // Set unique ID
        return textFlow;
    }


    private boolean isDuplicate(String messageId) {
        return messageContainer.getChildren().stream()
                .anyMatch(node -> node.getId() != null && node.getId().equals(messageId));
    }

    private int getPartnerIdByName(String name) {
        for (Map.Entry<Integer, String> entry : conversations.entrySet()) {
            if (entry.getValue().equals(name)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    private void loadChatHistory() {
        messageContainer.getChildren().clear();
        try (Connection cnx = MyDataBase.getInstance().getCnx();
             PreparedStatement pstmt = cnx.prepareStatement(
                     "SELECT * FROM messages WHERE " +
                             "(sender_id = ? AND receiver_id = ?) OR " +
                             "(sender_id = ? AND receiver_id = ?) " +
                             "ORDER BY timestamp")) {

            pstmt.setInt(1, currentUserId);
            pstmt.setInt(2, currentPartnerId);
            pstmt.setInt(3, currentPartnerId);
            pstmt.setInt(4, currentUserId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String content = rs.getString("content");
                String messageId = rs.getString("message_id");
                boolean isSender = rs.getInt("sender_id") == currentUserId;
                addMessageToChat(content, isSender, messageId);
            }
        } catch (SQLException e) {
            showError("Database Error", "Failed to load chat history");
        }
    }

    private void persistMessage(int senderId, int receiverId, String content, String messageId) {
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

    // Update message handler


    @FXML
    private void handleSendMessage() {
        if (currentPartnerId == -1) return;

        String message = messageInput.getText().trim();
        if (!message.isEmpty()) {
            // Generate unique message ID
            String messageId = UUID.randomUUID().toString();

            // Add to UI immediately with temporary ID
            addMessageToChat(message, true, messageId);

            // Send to server
            String formattedMessage = currentUserId + "|" + currentPartnerId + "|" + message + "|" + messageId;
            client.send(formattedMessage);
            persistMessage(currentUserId, currentPartnerId, message, messageId);
            messageInput.clear();
        }
    }



    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}