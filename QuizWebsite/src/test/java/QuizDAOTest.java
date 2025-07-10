//import Bean.Quiz;
//import DB.QuizDAO;
//import DB.DBConnector;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class QuizDAOTest {
//    private static QuizDAO quizDAO;
//    private static final int TEST_OWNER_ID = 1;
//
//    @BeforeEach
//    void setup() {
//        quizDAO = new QuizDAO();
//        try (Connection conn = DBConnector.getConnection();
//             Statement st = conn.createStatement()) {
//            st.executeUpdate("DELETE FROM quizzes WHERE title LIKE 'SampleQuiz%'");
//        } catch (SQLException e) {
//            System.err.println("Error in test setup cleanup: " + e.getMessage());
//            throw new RuntimeException("Test setup failed", e);
//        }
//    }
//
//    @Test
//    void testCreateQuiz_Success() {
//        Integer result = quizDAO.createQuiz(TEST_OWNER_ID, "SampleQuiz1", "A sample quiz", true, false, true);
//        assertNotNull(result);
//
//        List<Quiz> quizzes = quizDAO.searchQuizzes("SampleQuiz1");
//        assertFalse(quizzes.isEmpty());
//        Quiz quiz = quizzes.get(0);
//        assertEquals("SampleQuiz1", quiz.getTitle());
//        assertEquals("A sample quiz", quiz.getDescription());
//        assertEquals(TEST_OWNER_ID, quiz.getOwnerId());
//        assertTrue(quiz.isRandomOrder());
//        assertFalse(quiz.isMultiplePages());
//        assertTrue(quiz.isImmediateCorrection());
//    }
//
//    @Test
//    void testGetQuizById_Success() {
//        quizDAO.createQuiz(TEST_OWNER_ID, "SampleQuiz2", "Another quiz", false, true, false);
//        List<Quiz> quizzes = quizDAO.searchQuizzes("SampleQuiz2");
//        assertFalse(quizzes.isEmpty());
//        Quiz quiz = quizzes.get(0);
//        Quiz found = quizDAO.getQuizById(quiz.getQuizId());
//        assertNotNull(found);
//        assertEquals(quiz.getQuizId(), found.getQuizId());
//    }
//
//    @Test
//    void testGetQuizById_NonExistent() {
//        Quiz quiz = quizDAO.getQuizById(99999);
//        assertNull(quiz);
//    }
//
//    @Test
//    void testUpdateQuiz_Success() {
//        quizDAO.createQuiz(TEST_OWNER_ID, "SampleQuiz3", "Quiz 3", false, false, false);
//        List<Quiz> quizzes = quizDAO.searchQuizzes("SampleQuiz3");
//        assertFalse(quizzes.isEmpty());
//        Quiz quiz = quizzes.get(0);
//        quiz.setTitle("SampleQuiz3Updated");
//        quiz.setDescription("Updated description");
//        quiz.setRandomOrder(true);
//        quiz.setMultiplePages(true);
//        quiz.setImmediateCorrection(true);
//        boolean updated = quizDAO.updateQuiz(quiz);
//        assertTrue(updated);
//        Quiz updatedQuiz = quizDAO.getQuizById(quiz.getQuizId());
//        assertEquals("SampleQuiz3Updated", updatedQuiz.getTitle());
//        assertEquals("Updated description", updatedQuiz.getDescription());
//        assertTrue(updatedQuiz.isRandomOrder());
//        assertTrue(updatedQuiz.isMultiplePages());
//        assertTrue(updatedQuiz.isImmediateCorrection());
//    }
//
//    @Test
//    void testUpdateQuiz_NonExistent() {
//        Quiz quiz = new Quiz();
//        quiz.setQuizId(99999);
//        quiz.setOwnerId(TEST_OWNER_ID);
//        quiz.setTitle("NonExistent");
//        quiz.setDescription("none");
//        quiz.setRandomOrder(false);
//        quiz.setMultiplePages(false);
//        quiz.setImmediateCorrection(false);
//        boolean updated = quizDAO.updateQuiz(quiz);
//        assertFalse(updated);
//    }
//
//    @Test
//    void testDeleteQuiz_Success() {
//        quizDAO.createQuiz(TEST_OWNER_ID, "SampleQuiz4", "Quiz 4", false, false, false);
//        List<Quiz> quizzes = quizDAO.searchQuizzes("SampleQuiz4");
//        assertFalse(quizzes.isEmpty());
//        Quiz quiz = quizzes.get(0);
//        boolean deleted = quizDAO.deleteQuiz(quiz.getQuizId());
//        assertTrue(deleted);
//        Quiz deletedQuiz = quizDAO.getQuizById(quiz.getQuizId());
//        assertNull(deletedQuiz);
//    }
//
//    @Test
//    void testDeleteQuiz_NonExistent() {
//        boolean deleted = quizDAO.deleteQuiz(99999);
//        assertFalse(deleted);
//    }
//
//    @Test
//    void testSearchQuizzes_Success() {
//        quizDAO.createQuiz(TEST_OWNER_ID, "SampleQuiz5", "Quiz 5", false, false, false);
//        quizDAO.createQuiz(TEST_OWNER_ID, "SampleQuiz6", "Quiz 6", false, false, false);
//        List<Quiz> quizzes = quizDAO.searchQuizzes("SampleQuiz");
//        assertTrue(quizzes.size() >= 2);
//        assertTrue(quizzes.stream().anyMatch(q -> q.getTitle().equals("SampleQuiz5")));
//        assertTrue(quizzes.stream().anyMatch(q -> q.getTitle().equals("SampleQuiz6")));
//    }
//
//    @Test
//    void testSearchQuizzes_NoMatch() {
//        List<Quiz> quizzes = quizDAO.searchQuizzes("NonExistentQuiz");
//        assertTrue(quizzes.isEmpty());
//    }
//}

import Bean.Quiz;
import DB.DBConnector;
import DB.QuizDAO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuizDAOTest {

    private static QuizDAO quizDAO;
    private static int testUserId;

    @BeforeAll
    static void setupClass() throws SQLException {
        quizDAO = new QuizDAO();

        try (Connection conn = DBConnector.getConnection()) {
            // Check if user exists
            try (PreparedStatement checkPs = conn.prepareStatement("SELECT user_id FROM users WHERE username = ?")) {
                checkPs.setString(1, "testuser");
                try (var rs = checkPs.executeQuery()) {
                    if (rs.next()) {
                        testUserId = rs.getInt("user_id");
                        return; // user exists, done
                    }
                }
            }
            // Insert user if not found
            try (PreparedStatement insertPs = conn.prepareStatement(
                    "INSERT INTO users (username, password_hash, display_name) VALUES (?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                insertPs.setString(1, "testuser");
                insertPs.setString(2, "testhash");
                insertPs.setString(3, "Test User");
                insertPs.executeUpdate();

                try (var rs = insertPs.getGeneratedKeys()) {
                    if (rs.next()) {
                        testUserId = rs.getInt(1);
                    } else {
                        throw new RuntimeException("Failed to insert test user");
                    }
                }
            }
        }
    }


    @BeforeEach
    void cleanup() throws Exception {
        // Clean quizzes with titles starting with "Test Quiz" to avoid conflicts
        try (var conn = DBConnector.getConnection();
             var stmt = conn.prepareStatement("DELETE FROM quizzes WHERE title LIKE ?")) {
            stmt.setString(1, "Test Quiz%");
            stmt.executeUpdate();
        }
    }

    @Test
    void testCreateQuiz_success() {
        Integer id = quizDAO.createQuiz(testUserId, "Test Quiz 1", "desc", true, false, true);
        assertNotNull(id);

        Quiz quiz = quizDAO.getQuizById(id);
        assertNotNull(quiz);
        assertEquals("Test Quiz 1", quiz.getTitle());
        assertEquals(testUserId, quiz.getOwnerId());
        assertTrue(quiz.isRandomOrder());
        assertFalse(quiz.isMultiplePages());
        assertTrue(quiz.isImmediateCorrection());
    }

    @Test
    void testCreateQuiz_invalidOwner_returnsNull() {
        Integer id = quizDAO.createQuiz(-1, "Test Quiz Invalid", "desc", false, false, false);
        assertNull(id);
    }

    @Test
    void testGetQuizById_notFound() {
        Quiz quiz = quizDAO.getQuizById(-9999);
        assertNull(quiz);
    }

    @Test
    void testUpdateQuiz_success() {
        Integer id = quizDAO.createQuiz(testUserId, "Test Quiz Update", "desc", false, false, false);
        assertNotNull(id);

        Quiz quiz = quizDAO.getQuizById(id);
        quiz.setTitle("Updated Title");
        quiz.setRandomOrder(true);
        quiz.setMultiplePages(true);
        quiz.setImmediateCorrection(true);

        assertTrue(quizDAO.updateQuiz(quiz));

        Quiz updated = quizDAO.getQuizById(id);
        assertEquals("Updated Title", updated.getTitle());
        assertTrue(updated.isRandomOrder());
        assertTrue(updated.isMultiplePages());
        assertTrue(updated.isImmediateCorrection());
    }

    @Test
    void testUpdateQuiz_nonExistent_returnsFalse() {
        Quiz quiz = new Quiz();
        quiz.setQuizId(-9999);
        quiz.setOwnerId(testUserId);
        quiz.setTitle("Non-existent");
        quiz.setDescription("desc");
        quiz.setRandomOrder(false);
        quiz.setMultiplePages(false);
        quiz.setImmediateCorrection(false);

        assertFalse(quizDAO.updateQuiz(quiz));
    }

    @Test
    void testDeleteQuiz_success() {
        Integer id = quizDAO.createQuiz(testUserId, "Test Quiz Delete", "desc", false, false, false);
        assertNotNull(id);

        assertTrue(quizDAO.deleteQuiz(id));
        assertNull(quizDAO.getQuizById(id));
    }

    @Test
    void testDeleteQuiz_nonExistent_returnsFalse() {
        assertFalse(quizDAO.deleteQuiz(-9999));
    }

    @Test
    void testSearchQuizzes_found() {
        quizDAO.createQuiz(testUserId, "Test Quiz Search One", "desc", false, false, false);
        quizDAO.createQuiz(testUserId, "Test Quiz Search Two", "desc", false, false, false);

        List<Quiz> results = quizDAO.searchQuizzes("Test Quiz Search");
        assertNotNull(results);
        assertTrue(results.size() >= 2);
    }

    @Test
    void testSearchQuizzes_notFound() {
        List<Quiz> results = quizDAO.searchQuizzes("NoSuchTitleXYZ");
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
}

