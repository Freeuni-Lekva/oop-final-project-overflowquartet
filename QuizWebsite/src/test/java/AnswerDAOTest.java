

import Bean.Answer;
import DB.AnswerDAO;
import DB.DBConnector;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnswerDAOTest {
    private static AnswerDAO answerDAO;
    private static int userId;
    private static int quizId;

    @BeforeAll
    static void setupClass() throws SQLException {
        answerDAO = new AnswerDAO();

        try (Connection conn = DBConnector.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT user_id FROM users WHERE username = ?")) {
                ps.setString(1, "ans_test_user");
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt("user_id");
                    } else {
                        try (PreparedStatement ins = conn.prepareStatement(
                                "INSERT INTO users (username, password_hash, display_name) "
                                        + "VALUES (?, ?, ?)",
                                Statement.RETURN_GENERATED_KEYS)) {
                            ins.setString(1, "ans_test_user");
                            ins.setString(2, "hash");
                            ins.setString(3, "Answer Tester");
                            ins.executeUpdate();
                            try (ResultSet ks = ins.getGeneratedKeys()) {
                                assertTrue(ks.next());
                                userId = ks.getInt(1);
                            }
                        }
                    }
                }
            }


            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT quiz_id FROM quizzes WHERE owner_id = ? AND title = ?")) {
                ps.setInt(1, userId);
                ps.setString(2, "AnswerDAO Quiz");
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        quizId = rs.getInt("quiz_id");
                    } else {
                        try (PreparedStatement ins = conn.prepareStatement(
                                "INSERT INTO quizzes (owner_id, title, description) "
                                        + "VALUES (?, ?, ?)",
                                Statement.RETURN_GENERATED_KEYS)) {
                            ins.setInt(1, userId);
                            ins.setString(2, "AnswerDAO Quiz");
                            ins.setString(3, "Quiz for testing AnswerDAO");
                            ins.executeUpdate();
                            try (ResultSet ks = ins.getGeneratedKeys()) {
                                assertTrue(ks.next());
                                quizId = ks.getInt(1);
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
             PreparedStatement ps1 = conn.prepareStatement(
                     "DELETE a FROM answers a " +
                             "JOIN questions q ON a.question_id = q.question_id " +
                             "WHERE q.quiz_id = ?");
             PreparedStatement ps2 = conn.prepareStatement(
                     "DELETE FROM questions WHERE quiz_id = ?")) {
            ps1.setInt(1, quizId);
            ps1.executeUpdate();
            ps2.setInt(1, quizId);
            ps2.executeUpdate();
        }
    }

    @AfterAll
    static void teardownClass() throws SQLException {
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps1 = conn.prepareStatement(
                     "DELETE a FROM answers a " +
                             "JOIN questions q ON a.question_id = q.question_id " +
                             "WHERE q.quiz_id = ?");
             PreparedStatement ps2 = conn.prepareStatement(
                     "DELETE FROM questions WHERE quiz_id = ?");
             PreparedStatement ps3 = conn.prepareStatement(
                     "DELETE FROM quizzes WHERE quiz_id = ?");
             PreparedStatement ps4 = conn.prepareStatement(
                     "DELETE FROM users WHERE user_id = ?")) {

            ps1.setInt(1, quizId);
            ps1.executeUpdate();

            ps2.setInt(1, quizId);
            ps2.executeUpdate();

            ps3.setInt(1, quizId);
            ps3.executeUpdate();

            ps4.setInt(1, userId);
            ps4.executeUpdate();
        }
    }

    @Test
    void testAddAndGetAnswersForQuestion() throws SQLException {
        int questionId = insertQuestion("What is 2+2?", 1);

        Answer a = new Answer();
        a.setQuestionId(questionId);
        a.setUserId(userId);
        a.setResponseText("4");
        a.setResponseOptionId(null);
        a.setTimestamp(new Timestamp(System.currentTimeMillis()));
        answerDAO.addAnswer(a);

        List<Answer> answers = answerDAO.getAnswersForQuestion(questionId);
        assertEquals(1, answers.size());
        assertEquals("4", answers.get(0).getResponseText());
    }

    @Test
    void testGetAnswersForUser() throws SQLException {
        int q1 = insertQuestion("Q1?", 1);
        int q2 = insertQuestion("Q2?", 2);

        Timestamp now = new Timestamp(System.currentTimeMillis());
        for (int i = 1; i <= 2; i++) {
            Answer a = new Answer();
            a.setQuestionId(i == 1 ? q1 : q2);
            a.setUserId(userId);
            a.setResponseText(i == 1 ? "A" : "B");
            a.setResponseOptionId(i * 10);
            a.setTimestamp(now);
            answerDAO.addAnswer(a);
        }

        List<Answer> byUser = answerDAO.getAnswersForUser(userId);
        assertEquals(2, byUser.size());
        assertTrue(byUser.stream().anyMatch(x -> "A".equals(x.getResponseText())));
        assertTrue(byUser.stream().anyMatch(x -> "B".equals(x.getResponseText())));
    }

    @Test
    void testDeleteAnswersForQuestion() throws SQLException {
        int questionId = insertQuestion("Del?", 1);
        for (int i = 0; i < 3; i++) {
            Answer a = new Answer();
            a.setQuestionId(questionId);
            a.setUserId(userId);
            a.setResponseText("v" + i);
            a.setResponseOptionId(null);
            a.setTimestamp(new Timestamp(System.currentTimeMillis()));
            answerDAO.addAnswer(a);
        }

        assertEquals(3, answerDAO.getAnswersForQuestion(questionId).size());
        answerDAO.deleteAnswersForQuestion(questionId);
        assertTrue(answerDAO.getAnswersForQuestion(questionId).isEmpty());
    }

    @Test
    void testEmptyResults() throws SQLException {
        int questionId = insertQuestion("Empty?", 1);
        assertTrue(answerDAO.getAnswersForQuestion(questionId).isEmpty());
        assertTrue(answerDAO.getAnswersForUser(userId).isEmpty());
    }


    private int insertQuestion(String text, int order) throws SQLException {
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO questions (quiz_id, question_type, question_text, question_order) " +
                             "VALUES (?, 'question_response', ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, quizId);
            ps.setString(2, text);
            ps.setInt(3, order);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}
