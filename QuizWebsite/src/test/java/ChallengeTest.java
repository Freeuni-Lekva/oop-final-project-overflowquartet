import Bean.Message;
import DB.DBConnector;
import DB.MessageDAO;
import DB.UserDAO;
import DB.QuizDAO;
import DB.FriendsDAO;
import DB.FriendsDAO.FriendStatus;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeTest {

    private static MessageDAO messageDAO;
    private static UserDAO userDAO;
    private static QuizDAO quizDAO;
    private static FriendsDAO friendsDAO;
    private static int testUserId1;
    private static int testUserId2;
    private static int testQuizId;

    @BeforeAll
    static void setupClass() throws SQLException {
        messageDAO = new MessageDAO();
        userDAO = new UserDAO();
        quizDAO = new QuizDAO();
        friendsDAO = new FriendsDAO();
        
        // Create test users
        try (Connection conn = DBConnector.getConnection()) {
            // Create user 1
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (username, password_hash, display_name) VALUES (?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "challenge_test_user1");
                ps.setString(2, "hash");
                ps.setString(3, "Test User 1");
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    assertTrue(rs.next());
                    testUserId1 = rs.getInt(1);
                }
            }
            
            // Create user 2
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO users (username, password_hash, display_name) VALUES (?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "challenge_test_user2");
                ps.setString(2, "hash");
                ps.setString(3, "Test User 2");
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    assertTrue(rs.next());
                    testUserId2 = rs.getInt(1);
                }
            }
            
            // Create test quiz
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO quizzes (owner_id, title, description) VALUES (?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, testUserId1);
                ps.setString(2, "Test Challenge Quiz");
                ps.setString(3, "A test quiz for challenge functionality");
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    assertTrue(rs.next());
                    testQuizId = rs.getInt(1);
                }
            }
            
            // Make users friends
            friendsDAO.sendFriendRequest(testUserId1, testUserId2);
            friendsDAO.acceptFriendRequest(testUserId2, testUserId1);
        }
    }

    @BeforeEach
    void cleanup() throws SQLException {
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM messages WHERE sender_id IN (?, ?) OR receiver_id IN (?, ?)")) {
            ps.setInt(1, testUserId1);
            ps.setInt(2, testUserId2);
            ps.setInt(3, testUserId1);
            ps.setInt(4, testUserId2);
            ps.executeUpdate();
        }
    }

    @Test
    void testSendChallengeMessage() {
        // Test the static challenge message method
        boolean success = MessageDAO.sendChallengeMessage(testUserId1, testUserId2, testQuizId, "Test challenge message");
        assertTrue(success, "Challenge message should be sent successfully");
        
        // Verify the message was created with quiz_id
        List<Message> messages = messageDAO.getMessagesByReceiver(testUserId2);
        assertFalse(messages.isEmpty(), "Should have received messages");
        
        Message challengeMsg = messages.stream()
                .filter(m -> "challenge".equals(m.getMessageType()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(challengeMsg, "Should find challenge message");
        assertEquals("Test challenge message", challengeMsg.getContent());
        assertEquals(Integer.valueOf(testQuizId), challengeMsg.getQuizId());
        assertEquals(testUserId1, challengeMsg.getSenderId());
        assertEquals(testUserId2, challengeMsg.getReceiverId());
    }

    @Test
    void testDatabaseConnection() {
        try (Connection conn = DBConnector.getConnection()) {
            assertNotNull(conn, "Database connection should not be null");
            assertFalse(conn.isClosed(), "Database connection should be open");
        } catch (SQLException e) {
            fail("Database connection failed: " + e.getMessage());
        }
    }

    @Test
    void testUsersExist() {
        assertNotNull(userDAO.getUserById(testUserId1), "Test user 1 should exist");
        assertNotNull(userDAO.getUserById(testUserId2), "Test user 2 should exist");
    }

    @Test
    void testQuizExists() {
        assertNotNull(quizDAO.getQuizById(testQuizId), "Test quiz should exist");
    }

    @Test
    void testUsersAreFriends() {
        List<Integer> friends = friendsDAO.getFriendIds(testUserId1, FriendStatus.ACCEPTED);
        assertTrue(friends.contains(testUserId2), "Users should be friends");
    }

    @AfterAll
    static void cleanupClass() throws SQLException {
        try (Connection conn = DBConnector.getConnection()) {
            // Clean up test data
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM messages WHERE sender_id IN (?, ?) OR receiver_id IN (?, ?)")) {
                ps.setInt(1, testUserId1);
                ps.setInt(2, testUserId2);
                ps.setInt(3, testUserId1);
                ps.setInt(4, testUserId2);
                ps.executeUpdate();
            }
            
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM friends WHERE user_id IN (?, ?) OR friend_id IN (?, ?)")) {
                ps.setInt(1, testUserId1);
                ps.setInt(2, testUserId2);
                ps.setInt(3, testUserId1);
                ps.setInt(4, testUserId2);
                ps.executeUpdate();
            }
            
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM quizzes WHERE quiz_id = ?")) {
                ps.setInt(1, testQuizId);
                ps.executeUpdate();
            }
            
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM users WHERE user_id IN (?, ?)")) {
                ps.setInt(1, testUserId1);
                ps.setInt(2, testUserId2);
                ps.executeUpdate();
            }
        }
    }
} 