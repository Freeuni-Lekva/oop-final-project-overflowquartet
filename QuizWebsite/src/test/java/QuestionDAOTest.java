import Bean.Question;
import DB.QuestionDAO;
import DB.DBConnector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QuestionDAOTest {
    private static QuestionDAO questionDAO;
    private static int TEST_QUIZ_ID = 1;

    @BeforeEach
    void setup() {
        questionDAO = new QuestionDAO();
        try (Connection conn = DBConnector.getConnection();
             Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM questions WHERE question_text LIKE 'SampleQuestion%'");
            st.executeUpdate("DELETE FROM quizzes WHERE title = 'TestQuizForQuestions'");

            st.executeUpdate("INSERT INTO quizzes (owner_id, title, description, random_order, multiple_pages, immediate_correction) VALUES (1, 'TestQuizForQuestions', 'desc', false, false, false)", Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()) {
                TEST_QUIZ_ID = rs.getInt(1);
            } else {
                throw new RuntimeException("Failed to create test quiz");
            }
        } catch (SQLException e) {
            System.err.println("Error in test setup cleanup: " + e.getMessage());
            throw new RuntimeException("Test setup failed", e);
        }
    }

    @Test
    void testCreateQuestion_Success() {
        boolean result = questionDAO.createQuestion(TEST_QUIZ_ID, "question_response", "SampleQuestion1", null, 1, 2);
        assertTrue(result);
        List<Question> questions = questionDAO.getQuestionsForQuiz(TEST_QUIZ_ID);
        assertTrue(questions.stream().anyMatch(q -> q.getPrompt().equals("SampleQuestion1")));
    }

    @Test
    void testGetQuestionById_Success() {
        questionDAO.createQuestion(TEST_QUIZ_ID, "question_response", "SampleQuestion2", null, 2, 1);
        List<Question> questions = questionDAO.getQuestionsForQuiz(TEST_QUIZ_ID);
        assertFalse(questions.isEmpty());
        Question question = questions.get(0);
        Question found = questionDAO.getQuestionById(question.getQuestionId());
        assertNotNull(found);
        assertEquals(question.getPrompt(), found.getPrompt());
    }

    @Test
    void testGetQuestionById_NonExistent() {
        Question question = questionDAO.getQuestionById(99999);
        assertNull(question);
    }

    @Test
    void testUpdateQuestion_Success() {
        questionDAO.createQuestion(TEST_QUIZ_ID, "question_response", "SampleQuestion3", null, 3, 1);
        List<Question> questions = questionDAO.getQuestionsForQuiz(TEST_QUIZ_ID);
        assertFalse(questions.isEmpty());
        Question question = questions.get(0);
        question.setPrompt("SampleQuestion3Updated");
        boolean updated = questionDAO.updateQuestion(question, "http://example.com/image.jpg", 4);
        assertTrue(updated);
        Question updatedQuestion = questionDAO.getQuestionById(question.getQuestionId());
        assertEquals("SampleQuestion3Updated", updatedQuestion.getPrompt());
    }

    @Test
    void testUpdateQuestion_NonExistent() {
        Question question = new Question() {
        };
        question.setQuestionId(99999);
        question.setQuizId(TEST_QUIZ_ID);
        question.setPrompt("NonExistent");
        question.setPoints(1);
        boolean updated = questionDAO.updateQuestion(question, null, 1);
        assertFalse(updated);
    }

    @Test
    void testDeleteQuestion_Success() {
        questionDAO.createQuestion(TEST_QUIZ_ID, "question_response", "SampleQuestion4", null, 5, 1);
        List<Question> questions = questionDAO.getQuestionsForQuiz(TEST_QUIZ_ID);
        assertFalse(questions.isEmpty());
        Question question = questions.get(0);
        boolean deleted = questionDAO.deleteQuestion(question.getQuestionId());
        assertTrue(deleted);
        Question deletedQuestion = questionDAO.getQuestionById(question.getQuestionId());
        assertNull(deletedQuestion);
    }

    @Test
    void testDeleteQuestion_NonExistent() {
        boolean deleted = questionDAO.deleteQuestion(99999);
        assertFalse(deleted);
    }

    @Test
    void testGetQuestionsForQuiz_Success() {
        questionDAO.createQuestion(TEST_QUIZ_ID, "question_response", "SampleQuestion5", null, 6, 1);
        questionDAO.createQuestion(TEST_QUIZ_ID, "question_response", "SampleQuestion6", null, 7, 1);
        List<Question> questions = questionDAO.getQuestionsForQuiz(TEST_QUIZ_ID);
        assertTrue(questions.size() >= 2);
        assertTrue(questions.stream().anyMatch(q -> q.getPrompt().equals("SampleQuestion5")));
        assertTrue(questions.stream().anyMatch(q -> q.getPrompt().equals("SampleQuestion6")));
    }

    @Test
    void testGetQuestionsForQuiz_NoMatch() {
        List<Question> questions = questionDAO.getQuestionsForQuiz(99999);
        assertTrue(questions.isEmpty());
    }
} 