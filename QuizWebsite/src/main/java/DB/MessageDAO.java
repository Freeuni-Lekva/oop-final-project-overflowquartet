package DB;

import Bean.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Data Access Object for Message entities.
 * Provides CRUD operations and utilities for message handling between users.
 */
public class MessageDAO {

    // Allowed message types matching the database ENUM
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "friend_request",
            "challenge",
            "note"
    );

    /**
     * Checks whether a user with the given ID exists.
     */
    private boolean userExists(int userId) {
        String sql = "SELECT 1 FROM users WHERE user_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Inserts a new message. Returns generated message_id, or null if failure or invalid parameters.
     */
    public Integer createMessage(int senderId,
                                 int receiverId,
                                 String messageType,
                                 String content) {
        // Validate users and message type
        if (!userExists(senderId) || !userExists(receiverId) || !ALLOWED_TYPES.contains(messageType)) {
            return null;
        }

        String sql = "INSERT INTO messages (sender_id, receiver_id, message_type, content, sent_at) "
                + "VALUES (?, ?, ?, ?, NOW())";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            ps.setString(3, messageType);
            ps.setString(4, content);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                return null;
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a message by its ID.
     */
    public Message getMessageById(int messageId) {
        String sql = "SELECT * FROM messages WHERE message_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, messageId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractMessage(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public int countUnread(int receiverId) {
        String sql = "SELECT COUNT(*) FROM messages WHERE receiver_id = ? AND is_read = FALSE";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, receiverId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * Retrieves all messages received by a specific user, newest first.
     */
    public List<Message> getMessagesByReceiver(int receiverId) {
        List<Message> list = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE receiver_id = ? ORDER BY sent_at DESC";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, receiverId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractMessage(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Retrieves all messages sent by a specific user, newest first.
     */
    public List<Message> getMessagesBySender(int senderId) {
        List<Message> list = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE sender_id = ? ORDER BY sent_at DESC";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, senderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractMessage(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Marks a message as read. Returns true if successful.
     */
    public boolean markAsRead(int messageId) {
        String sql = "UPDATE messages SET is_read = TRUE WHERE message_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, messageId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates the content and read status of an existing message.
     */
    public boolean updateMessage(Message message) {
        String sql = "UPDATE messages SET content = ?, is_read = ? WHERE message_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, message.getContent());
            ps.setBoolean(2, message.isRead());
            ps.setInt(3, message.getMessageId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a message by its ID.
     */
    public boolean deleteMessage(int messageId) {
        String sql = "DELETE FROM messages WHERE message_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, messageId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sends a friend request message from one user to another.
     */
    public boolean sendFriendRequestMessage(int fromId, int toId) {
        return createMessage(fromId, toId, "friend_request", "") != null;
    }

    /**
     * Sends a note message from one user to another.
     */
    public boolean sendNote(int fromId, int toId, String content) {
        return createMessage(fromId, toId, "note", content) != null;
    }

    /**
     * Sends a challenge message from one user to another for a specific quiz.
     * The content should be the quizId as a string.
     */
    public boolean sendChallenge(int fromId, int toId, int quizId) {
        return createMessage(fromId, toId, "challenge", String.valueOf(quizId)) != null;
    }


    /**
     * Sends a challenge message with both quiz ID and custom message content.
     */
    public static boolean sendChallengeMessage(int fromUserId, int toUserId, int quizId, String content) {
        String sql = "INSERT INTO messages (sender_id, receiver_id, message_type, content, quiz_id, sent_at) " +
                "VALUES (?, ?, 'challenge', ?, ?, NOW())";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, fromUserId);
            ps.setInt(2, toUserId);
            ps.setString(3, content);
            ps.setInt(4, quizId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Helper to map a ResultSet row to a Message object.
     */
    private Message extractMessage(ResultSet rs) throws SQLException {
        Message msg = new Message();
        msg.setMessageId(rs.getInt("message_id"));
        msg.setSenderId(rs.getInt("sender_id"));
        msg.setReceiverId(rs.getInt("receiver_id"));
        msg.setMessageType(rs.getString("message_type"));
        msg.setContent(rs.getString("content"));
        msg.setRead(rs.getBoolean("is_read"));
        Timestamp sentAt = rs.getTimestamp("sent_at");
        msg.setSentAt(sentAt != null ? sentAt : new Timestamp(System.currentTimeMillis()));
        return msg;
    }
}
