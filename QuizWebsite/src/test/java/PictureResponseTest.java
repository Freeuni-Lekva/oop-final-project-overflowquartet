import Bean.Question;
import DB.QuestionDAO;
import DB.DBConnector;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class PictureResponseTest {

    private int userId;
    private int quizId;

    @BeforeEach
    public void setup() throws SQLException {
        try (Connection conn = DBConnector.getConnection()) {
            // Insert user
            PreparedStatement userStmt = conn.prepareStatement(
                    "INSERT INTO users (username, password_hash, display_name) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, "testuser_" + System.nanoTime());
            userStmt.setString(2, "hash");
            userStmt.setString(3, "Picture Response Test User");
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
            quizStmt.setString(2, "Picture Response Test Quiz " + System.nanoTime());
            quizStmt.setString(3, "Testing picture response questions");
            quizStmt.executeUpdate();
            try (ResultSet rs = quizStmt.getGeneratedKeys()) {
                assertTrue(rs.next());
                quizId = rs.getInt(1);
            }
        }
    }

    @AfterEach
    public void cleanup() throws SQLException {
        try (Connection conn = DBConnector.getConnection()) {
            // Delete question answers first (child records)
            PreparedStatement delAnswers = conn.prepareStatement(
                    "DELETE qa FROM question_answers qa " +
                    "JOIN questions q ON qa.question_id = q.question_id " +
                    "WHERE q.quiz_id = ?");
            delAnswers.setInt(1, quizId);
            delAnswers.executeUpdate();
            
            // Delete question choices if any
            PreparedStatement delChoices = conn.prepareStatement(
                    "DELETE qc FROM question_choices qc " +
                    "JOIN questions q ON qc.question_id = q.question_id " +
                    "WHERE q.quiz_id = ?");
            delChoices.setInt(1, quizId);
            delChoices.executeUpdate();
            
            // Delete questions
            PreparedStatement delQuestions = conn.prepareStatement(
                    "DELETE FROM questions WHERE quiz_id = ?");
            delQuestions.setInt(1, quizId);
            delQuestions.executeUpdate();
            
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
    public void testAddAndGetPictureResponseQuestion() throws Exception {
        QuestionDAO dao = new QuestionDAO();

        Question question = new Question();
        question.setQuizId(quizId);
        question.setQuestionType("picture_response");
        question.setQuestionText("What type of bird is shown in this image?");
        question.setImageUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/4/45/Eopsaltria_australis_-_Mogo_Campground.jpg/800px-Eopsaltria_australis_-_Mogo_Campground.jpg");
        question.setQuestionOrder(1);
        question.setAnswers(Arrays.asList("Eastern Yellow Robin", "Yellow Robin", "Robin"));

        int id = dao.addQuestion(question);
        assertTrue(id > 0);
        Question fromDb = dao.getQuestion(id);

        assertNotNull(fromDb);
        assertEquals("picture_response", fromDb.getQuestionType());
        assertEquals("What type of bird is shown in this image?", fromDb.getQuestionText());
        assertEquals("https://upload.wikimedia.org/wikipedia/commons/thumb/4/45/Eopsaltria_australis_-_Mogo_Campground.jpg/800px-Eopsaltria_australis_-_Mogo_Campground.jpg", fromDb.getImageUrl());
        assertNotNull(fromDb.getAnswers());
        assertEquals(3, fromDb.getAnswers().size());
        assertTrue(fromDb.getAnswers().contains("Eastern Yellow Robin"));
        assertTrue(fromDb.getAnswers().contains("Yellow Robin"));
        assertTrue(fromDb.getAnswers().contains("Robin"));
        assertNull(fromDb.getChoices());
    }

    @Test
    public void testGetQuestionsForQuizWithPictureResponse() throws Exception {
        QuestionDAO dao = new QuestionDAO();

        // Add a picture response question
        Question pictureQuestion = new Question();
        pictureQuestion.setQuizId(quizId);
        pictureQuestion.setQuestionType("picture_response");
        pictureQuestion.setQuestionText("Which US President is shown?");
        pictureQuestion.setImageUrl("https://upload.wikimedia.org/wikipedia/commons/thumb/0/0b/George_Washington_by_John_Trumbull_1780.jpg/800px-George_Washington_by_John_Trumbull_1780.jpg");
        pictureQuestion.setQuestionOrder(1);
        pictureQuestion.setAnswers(Arrays.asList("George Washington", "Washington"));
        dao.addQuestion(pictureQuestion);

        // Add a regular question
        Question regularQuestion = new Question();
        regularQuestion.setQuizId(quizId);
        regularQuestion.setQuestionType("question_response");
        regularQuestion.setQuestionText("What is the capital of France?");
        regularQuestion.setImageUrl(null);
        regularQuestion.setQuestionOrder(2);
        regularQuestion.setAnswers(Arrays.asList("Paris"));
        dao.addQuestion(regularQuestion);

        List<Question> all = dao.getQuestionsForQuiz(quizId, false);
        assertNotNull(all);
        assertEquals(2, all.size());
        
        // Check picture response question
        Question picQ = all.get(0);
        assertEquals("picture_response", picQ.getQuestionType());
        assertNotNull(picQ.getImageUrl());
        assertTrue(picQ.getImageUrl().contains("George_Washington"));
        
        // Check regular question
        Question regQ = all.get(1);
        assertEquals("question_response", regQ.getQuestionType());
        assertNull(regQ.getImageUrl());
    }

    @Test
    public void testUpdatePictureResponseQuestion() throws Exception {
        QuestionDAO dao = new QuestionDAO();

        Question question = new Question();
        question.setQuizId(quizId);
        question.setQuestionType("picture_response");
        question.setQuestionText("What bird is this?");
        question.setImageUrl("https://example.com/bird1.jpg");
        question.setQuestionOrder(1);
        question.setAnswers(Arrays.asList("Robin"));
        int id = dao.addQuestion(question);

        // Update the question
        question.setQuestionText("What type of bird is shown in this image?");
        question.setImageUrl("https://example.com/bird2.jpg");
        question.setAnswers(Arrays.asList("Eastern Yellow Robin", "Yellow Robin"));

        dao.updateQuestion(question);

        Question updated = dao.getQuestion(id);
        assertNotNull(updated);
        assertEquals("What type of bird is shown in this image?", updated.getQuestionText());
        assertEquals("https://example.com/bird2.jpg", updated.getImageUrl());
        assertEquals(2, updated.getAnswers().size());
        assertTrue(updated.getAnswers().contains("Eastern Yellow Robin"));
        assertTrue(updated.getAnswers().contains("Yellow Robin"));
    }
} 