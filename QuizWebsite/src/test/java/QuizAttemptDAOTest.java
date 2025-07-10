import Bean.QuizAttempt;
import DB.DBConnector;
import DB.QuizAttemptDAO;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class QuizAttemptDAOTest {

    private int userId;
    private int quizId;

    // Insert a fresh test user and quiz before each test
    @BeforeEach
    public void setup() throws SQLException {
        try (Connection conn = DBConnector.getConnection()) {
            // Insert user
            PreparedStatement userStmt = conn.prepareStatement(
                    "INSERT INTO users (username, password_hash, display_name) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, "testuser_" + System.nanoTime());
            userStmt.setString(2, "hash");
            userStmt.setString(3, "Test User");
            userStmt.executeUpdate();
            try (ResultSet rs = userStmt.getGeneratedKeys()) {
                assertTrue(rs.next());
                userId = rs.getInt(1);
            }
            // Insert quiz (owner is this user)
            PreparedStatement quizStmt = conn.prepareStatement(
                    "INSERT INTO quizzes (owner_id, title, description) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            quizStmt.setInt(1, userId);
            quizStmt.setString(2, "JUnit Test Quiz " + System.nanoTime());
            quizStmt.setString(3, "For DAO testing");
            quizStmt.executeUpdate();
            try (ResultSet rs = quizStmt.getGeneratedKeys()) {
                assertTrue(rs.next());
                quizId = rs.getInt(1);
            }
        }
    }

    // Remove all test attempts, quiz, user after each test
    @AfterEach
    public void cleanup() throws SQLException {
        try (Connection conn = DBConnector.getConnection()) {
            // Remove attempts for this quiz/user
            PreparedStatement delAttempts = conn.prepareStatement(
                    "DELETE FROM quiz_attempts WHERE quiz_id = ? OR user_id = ?");
            delAttempts.setInt(1, quizId);
            delAttempts.setInt(2, userId);
            delAttempts.executeUpdate();

            // Remove quiz
            PreparedStatement delQuiz = conn.prepareStatement(
                    "DELETE FROM quizzes WHERE quiz_id = ?");
            delQuiz.setInt(1, quizId);
            delQuiz.executeUpdate();

            // Remove user
            PreparedStatement delUser = conn.prepareStatement(
                    "DELETE FROM users WHERE user_id = ?");
            delUser.setInt(1, userId);
            delUser.executeUpdate();
        }
    }

    @Test
    public void testCreateAndRetrieveQuizAttempt() {
        QuizAttemptDAO dao = new QuizAttemptDAO();
        QuizAttempt attempt = new QuizAttempt(userId, quizId, 80, 123);

        boolean created = dao.createQuizAttempt(attempt);
        assertTrue(created, "QuizAttempt should be created");
        assertTrue(attempt.getAttemptId() > 0, "QuizAttempt should have attemptId set");

        QuizAttempt fromDb = dao.getQuizAttemptById(attempt.getAttemptId());
        assertNotNull(fromDb, "Should retrieve attempt by ID");
        assertEquals(userId, fromDb.getUserId());
        assertEquals(quizId, fromDb.getQuizId());
        assertEquals(80, fromDb.getScore());
        assertEquals(123, fromDb.getDurationSeconds());
        assertNotNull(fromDb.getAttemptDate());
    }

    @Test
    public void testGetAttemptsByUser() {
        QuizAttemptDAO dao = new QuizAttemptDAO();
        QuizAttempt attempt = new QuizAttempt(userId, quizId, 77, 99);
        assertTrue(dao.createQuizAttempt(attempt));

        List<QuizAttempt> attempts = dao.getAttemptsByUser(userId);
        assertNotNull(attempts);
        assertTrue(attempts.stream().anyMatch(a -> a.getAttemptId() == attempt.getAttemptId()));
    }

    @Test
    public void testGetAttemptsByQuiz() {
        QuizAttemptDAO dao = new QuizAttemptDAO();
        QuizAttempt attempt = new QuizAttempt(userId, quizId, 100, 333);
        assertTrue(dao.createQuizAttempt(attempt));

        List<QuizAttempt> attempts = dao.getAttemptsByQuiz(quizId);
        assertNotNull(attempts);
        assertTrue(attempts.stream().anyMatch(a -> a.getAttemptId() == attempt.getAttemptId()));
    }

    @Test
    public void testUpdateQuizAttempt() {
        QuizAttemptDAO dao = new QuizAttemptDAO();
        QuizAttempt attempt = new QuizAttempt(userId, quizId, 55, 11);
        assertTrue(dao.createQuizAttempt(attempt));
        int id = attempt.getAttemptId();

        // Update
        attempt.setScore(95);
        attempt.setDurationSeconds(22);
        assertTrue(dao.updateQuizAttempt(attempt), "QuizAttempt should update");

        // Verify updated
        QuizAttempt updated = dao.getQuizAttemptById(id);
        assertNotNull(updated);
        assertEquals(95, updated.getScore());
        assertEquals(22, updated.getDurationSeconds());
    }

    @Test
    public void testDeleteQuizAttempt() {
        QuizAttemptDAO dao = new QuizAttemptDAO();
        QuizAttempt attempt = new QuizAttempt(userId, quizId, 99, 66);
        assertTrue(dao.createQuizAttempt(attempt));
        int id = attempt.getAttemptId();

        assertTrue(dao.deleteQuizAttempt(id), "QuizAttempt should delete");

        QuizAttempt shouldBeNull = dao.getQuizAttemptById(id);
        assertNull(shouldBeNull, "Deleted attempt should not be found");
    }
}
