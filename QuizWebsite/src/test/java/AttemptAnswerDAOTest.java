import Bean.AttemptAnswer;
import DB.AttemptAnswerDAO;
import DB.DBConnector;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class AttemptAnswerDAOTest {

    private int userId;
    private int quizId;
    private int questionId;
    private int attemptId;

    @BeforeEach
    public void setup() throws SQLException {
        try (Connection conn = DBConnector.getConnection()) {
            // Insert user
            PreparedStatement userStmt = conn.prepareStatement(
                    "INSERT INTO users (username, password_hash, display_name) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, "testuser_" + System.nanoTime());
            userStmt.setString(2, "hash");
            userStmt.setString(3, "AttemptAnswer Test User");
            userStmt.executeUpdate();
            try (ResultSet rs = userStmt.getGeneratedKeys()) {
                assertTrue(rs.next());
                userId = rs.getInt(1);
            }
            // Insert quiz
            PreparedStatement quizStmt = conn.prepareStatement(
                    "INSERT INTO quizzes (owner_id, title, description) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            quizStmt.setInt(1, userId);
            quizStmt.setString(2, "Test Quiz for AttemptAnswers " + System.nanoTime());
            quizStmt.setString(3, "AttemptAnswerDAO Testing");
            quizStmt.executeUpdate();
            try (ResultSet rs = quizStmt.getGeneratedKeys()) {
                assertTrue(rs.next());
                quizId = rs.getInt(1);
            }
            // Insert question
            PreparedStatement qStmt = conn.prepareStatement(
                    "INSERT INTO questions (quiz_id, question_type, question_text, image_url, question_order) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            qStmt.setInt(1, quizId);
            qStmt.setString(2, "question_response");
            qStmt.setString(3, "What is 2+2?");
            qStmt.setString(4, null);
            qStmt.setInt(5, 1);
            qStmt.executeUpdate();
            try (ResultSet rs = qStmt.getGeneratedKeys()) {
                assertTrue(rs.next());
                questionId = rs.getInt(1);
            }
            // Insert attempt
            PreparedStatement aStmt = conn.prepareStatement(
                    "INSERT INTO quiz_attempts (user_id, quiz_id, score, duration_seconds) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            aStmt.setInt(1, userId);
            aStmt.setInt(2, quizId);
            aStmt.setInt(3, 100);
            aStmt.setInt(4, 42);
            aStmt.executeUpdate();
            try (ResultSet rs = aStmt.getGeneratedKeys()) {
                assertTrue(rs.next());
                attemptId = rs.getInt(1);
            }
        }
    }

    @AfterEach
    public void cleanup() throws SQLException {
        try (Connection conn = DBConnector.getConnection()) {
            // Delete attempt answers
            PreparedStatement delAnswers = conn.prepareStatement(
                    "DELETE FROM attempt_answers WHERE attempt_id = ?");
            delAnswers.setInt(1, attemptId);
            delAnswers.executeUpdate();
            // Delete attempt
            PreparedStatement delAttempt = conn.prepareStatement(
                    "DELETE FROM quiz_attempts WHERE attempt_id = ?");
            delAttempt.setInt(1, attemptId);
            delAttempt.executeUpdate();
            // Delete question
            PreparedStatement delQ = conn.prepareStatement(
                    "DELETE FROM questions WHERE question_id = ?");
            delQ.setInt(1, questionId);
            delQ.executeUpdate();
            // Delete quiz
            PreparedStatement delQuiz = conn.prepareStatement(
                    "DELETE FROM quizzes WHERE quiz_id = ?");
            delQuiz.setInt(1, quizId);
            delQuiz.executeUpdate();
            // Delete user
            PreparedStatement delUser = conn.prepareStatement(
                    "DELETE FROM users WHERE user_id = ?");
            delUser.setInt(1, userId);
            delUser.executeUpdate();
        }
    }

    @Test
    public void testAddAndGetAttemptAnswer() {
        AttemptAnswerDAO dao = new AttemptAnswerDAO();
        AttemptAnswer ans = new AttemptAnswer(attemptId, questionId, "4", true);
        boolean added = dao.addAttemptAnswer(ans);
        assertTrue(added);
        assertTrue(ans.getAttemptAnswerId() > 0);

        AttemptAnswer fromDb = dao.getAttemptAnswerById(ans.getAttemptAnswerId());
        assertNotNull(fromDb);
        assertEquals(attemptId, fromDb.getAttemptId());
        assertEquals(questionId, fromDb.getQuestionId());
        assertEquals("4", fromDb.getUserAnswerText());
        assertTrue(fromDb.getIsCorrect());
    }

    @Test
    public void testAddMultipleAttemptAnswers() {
        AttemptAnswerDAO dao = new AttemptAnswerDAO();
        AttemptAnswer a1 = new AttemptAnswer(attemptId, questionId, "4", true);
        AttemptAnswer a2 = new AttemptAnswer(attemptId, questionId, "five", false);
        boolean added = dao.addAttemptAnswers(Arrays.asList(a1, a2));
        assertTrue(added);
        assertTrue(a1.getAttemptAnswerId() > 0 && a2.getAttemptAnswerId() > 0);

        List<AttemptAnswer> fromDb = dao.getAnswersForAttempt(attemptId);
        assertNotNull(fromDb);
        assertEquals(2, fromDb.size());
        assertTrue(fromDb.stream().anyMatch(a -> "4".equals(a.getUserAnswerText())));
        assertTrue(fromDb.stream().anyMatch(a -> "five".equals(a.getUserAnswerText())));
    }

    @Test
    public void testUpdateAttemptAnswer() {
        AttemptAnswerDAO dao = new AttemptAnswerDAO();
        AttemptAnswer ans = new AttemptAnswer(attemptId, questionId, "3", false);
        assertTrue(dao.addAttemptAnswer(ans));
        int id = ans.getAttemptAnswerId();

        // Change the answer and correctness
        ans.setUserAnswerText("4");
        ans.setIsCorrect(true);
        boolean updated = dao.updateAttemptAnswer(ans);
        assertTrue(updated);

        AttemptAnswer updatedAns = dao.getAttemptAnswerById(id);
        assertNotNull(updatedAns);
        assertEquals("4", updatedAns.getUserAnswerText());
        assertTrue(updatedAns.getIsCorrect());
    }

    @Test
    public void testDeleteAttemptAnswer() {
        AttemptAnswerDAO dao = new AttemptAnswerDAO();
        AttemptAnswer ans = new AttemptAnswer(attemptId, questionId, "toDelete", false);
        assertTrue(dao.addAttemptAnswer(ans));
        int id = ans.getAttemptAnswerId();

        boolean deleted = dao.deleteAttemptAnswer(id);
        assertTrue(deleted);

        AttemptAnswer shouldBeNull = dao.getAttemptAnswerById(id);
        assertNull(shouldBeNull);
    }

    @Test
    public void testDeleteAnswersForAttempt() {
        AttemptAnswerDAO dao = new AttemptAnswerDAO();
        AttemptAnswer a1 = new AttemptAnswer(attemptId, questionId, "answer1", true);
        AttemptAnswer a2 = new AttemptAnswer(attemptId, questionId, "answer2", false);
        assertTrue(dao.addAttemptAnswers(Arrays.asList(a1, a2)));
        List<AttemptAnswer> before = dao.getAnswersForAttempt(attemptId);
        assertEquals(2, before.size());

        boolean deleted = dao.deleteAnswersForAttempt(attemptId);
        assertTrue(deleted);

        List<AttemptAnswer> after = dao.getAnswersForAttempt(attemptId);
        assertTrue(after.isEmpty());
    }

    @Test
    public void testGetAnswersForQuestion() {
        AttemptAnswerDAO dao = new AttemptAnswerDAO();
        AttemptAnswer ans = new AttemptAnswer(attemptId, questionId, "special", true);
        assertTrue(dao.addAttemptAnswer(ans));
        List<AttemptAnswer> fromDb = dao.getAnswersForQuestion(questionId);
        assertNotNull(fromDb);
        assertTrue(fromDb.stream().anyMatch(a -> "special".equals(a.getUserAnswerText())));
    }

    @Test
    public void testAddAnswerWithNullCorrectness() {
        AttemptAnswerDAO dao = new AttemptAnswerDAO();
        AttemptAnswer ans = new AttemptAnswer(attemptId, questionId, "ungraded", null);
        boolean added = dao.addAttemptAnswer(ans);
        assertTrue(added);
        assertTrue(ans.getAttemptAnswerId() > 0);

        AttemptAnswer fromDb = dao.getAttemptAnswerById(ans.getAttemptAnswerId());
        assertNotNull(fromDb);
        assertNull(fromDb.getIsCorrect());
    }
}
