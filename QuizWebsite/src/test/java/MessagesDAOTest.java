
import Bean.Message;
import DB.DBConnector;
import DB.MessageDAO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessageDAOTest {

    private static MessageDAO messageDAO;
    private static int senderId;
    private static int receiverId;

    @BeforeAll
    static void setupClass() throws SQLException {
        messageDAO = new MessageDAO();
        try (Connection conn = DBConnector.getConnection()) {
            // Ensure sender exists
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT user_id FROM users WHERE username = ?")) {
                ps.setString(1, "msg_sender");
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        senderId = rs.getInt("user_id");
                    } else {
                        try (PreparedStatement ins = conn.prepareStatement(
                                "INSERT INTO users (username, password_hash, display_name) VALUES (?, ?, ?)",
                                PreparedStatement.RETURN_GENERATED_KEYS)) {
                            ins.setString(1, "msg_sender");
                            ins.setString(2, "hash");
                            ins.setString(3, "Msg Sender");
                            ins.executeUpdate();
                            try (ResultSet ks = ins.getGeneratedKeys()) {
                                assertTrue(ks.next());
                                senderId = ks.getInt(1);
                            }
                        }
                    }
                }
            }
            // Ensure receiver exists
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT user_id FROM users WHERE username = ?")) {
                ps.setString(1, "msg_receiver");
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        receiverId = rs.getInt("user_id");
                    } else {
                        try (PreparedStatement ins = conn.prepareStatement(
                                "INSERT INTO users (username, password_hash, display_name) VALUES (?, ?, ?)",
                                PreparedStatement.RETURN_GENERATED_KEYS)) {
                            ins.setString(1, "msg_receiver");
                            ins.setString(2, "hash");
                            ins.setString(3, "Msg Receiver");
                            ins.executeUpdate();
                            try (ResultSet ks = ins.getGeneratedKeys()) {
                                assertTrue(ks.next());
                                receiverId = ks.getInt(1);
                            }
                        }
                    }
                }
            }
        }
    }

    @BeforeEach
    void cleanup() throws SQLException {
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM messages WHERE sender_id = ? OR receiver_id = ?")) {
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            ps.executeUpdate();
        }
    }

    @Test
    void testCreateMessage_success() {
        Integer id = messageDAO.createMessage(senderId, receiverId, "note", "Hello world");
        assertNotNull(id, "Should return generated ID for valid message");

        Message msg = messageDAO.getMessageById(id);
        assertNotNull(msg);
        assertEquals(senderId, msg.getSenderId());
        assertEquals(receiverId, msg.getReceiverId());
        assertEquals("note", msg.getMessageType());
        assertEquals("Hello world", msg.getContent());
        assertFalse(msg.isRead(), "New messages should be unread");
        assertNotNull(msg.getSentAt());
    }

    @Test
    void testCreateMessage_invalidSender() {
        Integer id = messageDAO.createMessage(-1, receiverId, "note", "X");
        assertNull(id, "Invalid sender should return null");
    }

    @Test
    void testCreateMessage_invalidReceiver() {
        Integer id = messageDAO.createMessage(senderId, -1, "note", "X");
        assertNull(id, "Invalid receiver should return null");
    }

    @Test
    void testCreateMessage_invalidType() {
        // ENUM constraint violation leads to SQLException => null
        Integer id = messageDAO.createMessage(senderId, receiverId, "invalid_type", "X");
        assertNull(id, "Invalid message type should return null");
    }

    @Test
    void testGetMessageById_notFound() {
        Message msg = messageDAO.getMessageById(-9999);
        assertNull(msg, "Non-existent ID should return null");
    }

    @Test
    void testGetMessagesByReceiver() {
        // add two messages
        Integer id1 = messageDAO.createMessage(senderId, receiverId, "note", "First");
        Integer id2 = messageDAO.createMessage(senderId, receiverId, "challenge", "Second");
        assertNotNull(id1);
        assertNotNull(id2);

        List<Message> list = messageDAO.getMessagesByReceiver(receiverId);
        assertEquals(2, list.size());
        // All entries should belong to receiver
        assertTrue(list.stream().allMatch(m -> m.getReceiverId() == receiverId));
    }

    @Test
    void testGetMessagesBySender() {
        Integer id1 = messageDAO.createMessage(senderId, receiverId, "friend_request", "Req");
        Integer id2 = messageDAO.createMessage(senderId, receiverId, "note", "Msg");
        assertNotNull(id1);
        assertNotNull(id2);

        List<Message> list = messageDAO.getMessagesBySender(senderId);
        assertEquals(2, list.size());
        assertTrue(list.stream().allMatch(m -> m.getSenderId() == senderId));
    }

    @Test
    void testMarkAsRead_andUpdateMessage() {
        Integer id = messageDAO.createMessage(senderId, receiverId, "note", "ReadTest");
        assertNotNull(id);

        // mark as read
        assertTrue(messageDAO.markAsRead(id));
        Message msg = messageDAO.getMessageById(id);
        assertTrue(msg.isRead());

        // update content and read flag
        msg.setContent("Updated");
        msg.setRead(false);
        assertTrue(messageDAO.updateMessage(msg));

        Message updated = messageDAO.getMessageById(id);
        assertEquals("Updated", updated.getContent());
        assertFalse(updated.isRead());
    }

    @Test
    void testDeleteMessage() {
        Integer id = messageDAO.createMessage(senderId, receiverId, "note", "DelTest");
        assertNotNull(id);

        assertTrue(messageDAO.deleteMessage(id));
        assertNull(messageDAO.getMessageById(id));
    }

    @Test
    void testDeleteMessage_nonExistent() {
        assertFalse(messageDAO.deleteMessage(-9999));
    }
}
