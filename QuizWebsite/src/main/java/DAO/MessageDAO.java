package DAO;

import Bean.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    private Connection conn;

    public MessageDAO(Connection conn) {
        this.conn = conn;
    }

    public void createMessage(Message message) throws SQLException {
        String sql = "INSERT INTO messages (from_user_id, to_user_id, type, content, sent_at, is_read) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, message.getFromUserId());
            stmt.setInt(2, message.getToUserId());
            stmt.setString(3, message.getType());
            stmt.setString(4, message.getContent());
            stmt.setTimestamp(5, message.getSentAt());
            stmt.setBoolean(6, message.isRead());
            stmt.executeUpdate();
        }
    }

    public List<Message> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM messages WHERE from_user_id = ? OR to_user_id = ?";
        List<Message> messages = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(extractMessage(rs));
            }
        }
        return messages;
    }

    public void updateMessage(Message message) throws SQLException {
        String sql = "UPDATE messages SET is_read=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, message.isRead());
            stmt.setInt(2, message.getMessageId());
            stmt.executeUpdate();
        }
    }

    public void deleteMessage(int messageId) throws SQLException {
        String sql = "DELETE FROM messages WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            stmt.executeUpdate();
        }
    }

    private Message extractMessage(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setMessageId(rs.getInt("id"));
        message.setFromUserId(rs.getInt("from_user_id"));
        message.setToUserId(rs.getInt("to_user_id"));
        message.setType(rs.getString("type"));
        message.setContent(rs.getString("content"));
        message.setSentAt(rs.getTimestamp("sent_at"));
        message.setRead(rs.getBoolean("is_read"));
        return message;
    }
} 