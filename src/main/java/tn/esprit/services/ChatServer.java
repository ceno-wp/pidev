package tn.esprit.services;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Character.getName;

public class ChatServer extends WebSocketServer {
    private static final int PORT = 8887;
    private static final Map<Integer, WebSocket> connections = new HashMap<>();

    public ChatServer() {
        super(new InetSocketAddress(PORT));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        int userId = Integer.parseInt(handshake.getFieldValue("user-id"));
        connections.put(userId, conn);
        System.out.println("New connection: " + userId);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // Format: senderId|receiverId|messageContent|messageId
        String[] parts = message.split("\\|", 4);
        int senderId = Integer.parseInt(parts[0]);
        int receiverId = Integer.parseInt(parts[1]);
        String content = parts[2];
        String messageId = parts[3];

        // Get sender name from database
        String senderName = getName(senderId);

        // Format: MSG|senderId|senderName|content|messageId
        String formattedMessage = "MSG|" + senderId + "|" + senderName + "|" + content + "|" + messageId;
        WebSocket receiverConn = connections.get(receiverId);
        if (receiverConn != null) {
            receiverConn.send(formattedMessage);
        }

        WebSocket senderConn = connections.get(senderId);
        if (senderConn != null) {
            senderConn.send("ACK|" + messageId);
        }

        new ChatService().saveMessage(senderId, receiverId, content, messageId);
    }



    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

        connections.values().removeIf(webSocket -> webSocket == conn);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Chat server started on port " + PORT);
    }
}