import DB.DBConnector;
import DB.QuestionAnswerDAO;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionAnswerDAOTest {

    private static QuestionAnswerDAO dao;
    private static int testQuestionId;
    private static int testUserId;

    @BeforeAll
    static void setupClass() throws SQLException {
        dao = new QuestionAnswerDAO();

        try (Connection conn = DBConnector.getConnection()) {

            try (PreparedStatement userStmt = conn.prepareStatement(
                    "INSERT INTO users (username, password_hash) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, "qa_test_user");
                userStmt.setString(2, "dummy_hash_123");
                userStmt.executeUpdate();
                try (ResultSet userKeys = userStmt.getGeneratedKeys()) {
                    userKeys.next();
                    testUserId = userKeys.getInt(1);
                }
            }


            int quizId;
            try (PreparedStatement quizStmt = conn.prepareStatement(
                    "INSERT INTO quizzes (owner_id, title) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                quizStmt.setInt(1, testUserId);
                quizStmt.setString(2, "QA Test Quiz");
                quizStmt.executeUpdate();
                try (ResultSet quizKeys = quizStmt.getGeneratedKeys()) {
                    quizKeys.next();
                    quizId = quizKeys.getInt(1);
                }
            }


            try (PreparedStatement questionStmt = conn.prepareStatement(
                    "INSERT INTO questions (quiz_id, question_text, question_type) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                questionStmt.setInt(1, quizId);
                questionStmt.setString(2, "What is AI?");
                questionStmt.setString(3, "question_response");
                questionStmt.executeUpdate();
                try (ResultSet questionKeys = questionStmt.getGeneratedKeys()) {
                    questionKeys.next();
                    testQuestionId = questionKeys.getInt(1);
                }
            }
        }
    }

    @AfterAll
    static void teardownClass() throws SQLException {
        try (Connection conn = DBConnector.getConnection()) {
            // Delete test answers first (because of foreign key constraints)
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM question_answers WHERE question_id = ?")) {
                ps.setInt(1, testQuestionId);
                ps.executeUpdate();
            }


            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM questions WHERE question_id = ?")) {
                ps.setInt(1, testQuestionId);
                ps.executeUpdate();
            }


            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM quizzes WHERE owner_id = ?")) {
                ps.setInt(1, testUserId);
                ps.executeUpdate();
            }


            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM users WHERE user_id = ?")) {
                ps.setInt(1, testUserId);
                ps.executeUpdate();
            }
        }
    }

    @BeforeEach
    void cleanAnswers() {
        dao.deleteAnswersForQuestion(testQuestionId);
    }

    @Test
    void testAddAndGetAnswers() {
        List<String> beforeAdd = dao.getAnswersForQuestion(testQuestionId);
        System.out.println("Before adding: " + beforeAdd.size());
        assertEquals(0, beforeAdd.size(), "Should start with no answers");

        dao.addAnswer(testQuestionId, "Answer A");
        dao.addAnswer(testQuestionId, "Answer B");

        List<String> answers = dao.getAnswersForQuestion(testQuestionId);
        System.out.println("After adding: " + answers.size());

        assertEquals(2, answers.size(), "There should be exactly 2 answers");
        assertTrue(answers.contains("Answer A"));
        assertTrue(answers.contains("Answer B"));
    }

    @Test
    void testIsCorrectAnswer_caseInsensitive_trimmed() {
        dao.addAnswer(testQuestionId, "  Correct Answer  ");

        assertTrue(dao.isCorrectAnswer(testQuestionId, "correct answer"));
        assertTrue(dao.isCorrectAnswer(testQuestionId, " Correct Answer "));
        assertFalse(dao.isCorrectAnswer(testQuestionId, "wrong answer"));
    }

    @Test
    void testDeleteAnswersForQuestion() {
        dao.addAnswer(testQuestionId, "To be deleted");
        assertFalse(dao.getAnswersForQuestion(testQuestionId).isEmpty());

        dao.deleteAnswersForQuestion(testQuestionId);
        assertTrue(dao.getAnswersForQuestion(testQuestionId).isEmpty());
    }
}
