import Bean.Quiz;
import DB.QuizDAO;
import DB.DBConnector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QuizDAOTest {
    private static QuizDAO quizDAO;
    private static final int TEST_OWNER_ID = 1;

    @BeforeEach
    void setup() {
        quizDAO = new QuizDAO();
        try (Connection conn = DBConnector.getConnection();
             Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM quizzes WHERE title LIKE 'SampleQuiz%'");
        } catch (SQLException e) {
            System.err.println("Error in test setup cleanup: " + e.getMessage());
            throw new RuntimeException("Test setup failed", e);
        }
    }

    @Test
    void testCreateQuiz_Success() {
        boolean result = quizDAO.createQuiz(TEST_OWNER_ID, "SampleQuiz1", "A sample quiz", true, false, true);
        assertTrue(result);

        List<Quiz> quizzes = quizDAO.searchQuizzes("SampleQuiz1");
        assertFalse(quizzes.isEmpty());
        Quiz quiz = quizzes.get(0);
        assertEquals("SampleQuiz1", quiz.getTitle());
        assertEquals("A sample quiz", quiz.getDescription());
        assertEquals(TEST_OWNER_ID, quiz.getOwnerId());
        assertTrue(quiz.isRandomOrder());
        assertFalse(quiz.isMultiplePages());
        assertTrue(quiz.isImmediateCorrection());
    }

    @Test
    void testGetQuizById_Success() {
        quizDAO.createQuiz(TEST_OWNER_ID, "SampleQuiz2", "Another quiz", false, true, false);
        List<Quiz> quizzes = quizDAO.searchQuizzes("SampleQuiz2");
        assertFalse(quizzes.isEmpty());
        Quiz quiz = quizzes.get(0);
        Quiz found = quizDAO.getQuizById(quiz.getQuizId());
        assertNotNull(found);
        assertEquals(quiz.getQuizId(), found.getQuizId());
    }

    @Test
    void testGetQuizById_NonExistent() {
        Quiz quiz = quizDAO.getQuizById(99999);
        assertNull(quiz);
    }

    @Test
    void testUpdateQuiz_Success() {
        quizDAO.createQuiz(TEST_OWNER_ID, "SampleQuiz3", "Quiz 3", false, false, false);
        List<Quiz> quizzes = quizDAO.searchQuizzes("SampleQuiz3");
        assertFalse(quizzes.isEmpty());
        Quiz quiz = quizzes.get(0);
        quiz.setTitle("SampleQuiz3Updated");
        quiz.setDescription("Updated description");
        quiz.setRandomOrder(true);
        quiz.setMultiplePages(true);
        quiz.setImmediateCorrection(true);
        boolean updated = quizDAO.updateQuiz(quiz);
        assertTrue(updated);
        Quiz updatedQuiz = quizDAO.getQuizById(quiz.getQuizId());
        assertEquals("SampleQuiz3Updated", updatedQuiz.getTitle());
        assertEquals("Updated description", updatedQuiz.getDescription());
        assertTrue(updatedQuiz.isRandomOrder());
        assertTrue(updatedQuiz.isMultiplePages());
        assertTrue(updatedQuiz.isImmediateCorrection());
    }

    @Test
    void testUpdateQuiz_NonExistent() {
        Quiz quiz = new Quiz();
        quiz.setQuizId(99999);
        quiz.setOwnerId(TEST_OWNER_ID);
        quiz.setTitle("NonExistent");
        quiz.setDescription("none");
        quiz.setRandomOrder(false);
        quiz.setMultiplePages(false);
        quiz.setImmediateCorrection(false);
        boolean updated = quizDAO.updateQuiz(quiz);
        assertFalse(updated);
    }

    @Test
    void testDeleteQuiz_Success() {
        quizDAO.createQuiz(TEST_OWNER_ID, "SampleQuiz4", "Quiz 4", false, false, false);
        List<Quiz> quizzes = quizDAO.searchQuizzes("SampleQuiz4");
        assertFalse(quizzes.isEmpty());
        Quiz quiz = quizzes.get(0);
        boolean deleted = quizDAO.deleteQuiz(quiz.getQuizId());
        assertTrue(deleted);
        Quiz deletedQuiz = quizDAO.getQuizById(quiz.getQuizId());
        assertNull(deletedQuiz);
    }

    @Test
    void testDeleteQuiz_NonExistent() {
        boolean deleted = quizDAO.deleteQuiz(99999);
        assertFalse(deleted);
    }

    @Test
    void testSearchQuizzes_Success() {
        quizDAO.createQuiz(TEST_OWNER_ID, "SampleQuiz5", "Quiz 5", false, false, false);
        quizDAO.createQuiz(TEST_OWNER_ID, "SampleQuiz6", "Quiz 6", false, false, false);
        List<Quiz> quizzes = quizDAO.searchQuizzes("SampleQuiz");
        assertTrue(quizzes.size() >= 2);
        assertTrue(quizzes.stream().anyMatch(q -> q.getTitle().equals("SampleQuiz5")));
        assertTrue(quizzes.stream().anyMatch(q -> q.getTitle().equals("SampleQuiz6")));
    }

    @Test
    void testSearchQuizzes_NoMatch() {
        List<Quiz> quizzes = quizDAO.searchQuizzes("NonExistentQuiz");
        assertTrue(quizzes.isEmpty());
    }
} 