import Bean.Choice;
import Bean.Question;
import DB.DBConnector;
import DB.QuestionDAO;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class QuestionDAOTest {

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
            quizStmt.setString(3, "For QuestionDAO testing");
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
            // Remove questions and children
            PreparedStatement delAnswers = conn.prepareStatement(
                    "DELETE FROM question_answers WHERE question_id IN (SELECT question_id FROM questions WHERE quiz_id = ?)");
            delAnswers.setInt(1, quizId);
            delAnswers.executeUpdate();

            PreparedStatement delChoices = conn.prepareStatement(
                    "DELETE FROM question_choices WHERE question_id IN (SELECT question_id FROM questions WHERE quiz_id = ?)");
            delChoices.setInt(1, quizId);
            delChoices.executeUpdate();

            PreparedStatement delQuestions = conn.prepareStatement(
                    "DELETE FROM questions WHERE quiz_id = ?");
            delQuestions.setInt(1, quizId);
            delQuestions.executeUpdate();

            PreparedStatement delQuiz = conn.prepareStatement(
                    "DELETE FROM quizzes WHERE quiz_id = ?");
            delQuiz.setInt(1, quizId);
            delQuiz.executeUpdate();

            PreparedStatement delUser = conn.prepareStatement(
                    "DELETE FROM users WHERE user_id = ?");
            delUser.setInt(1, userId);
            delUser.executeUpdate();
        }
    }

    @Test
    public void testAddAndGetQuestionResponse() throws Exception {
        QuestionDAO dao = new QuestionDAO();

        Question question = new Question();
        question.setQuizId(quizId);
        question.setQuestionType("question_response");
        question.setQuestionText("Who was the first US president?");
        question.setImageUrl(null);
        question.setQuestionOrder(1);
        question.setAnswers(Arrays.asList("George Washington", "Washington"));

        int id = dao.addQuestion(question);
        assertTrue(id > 0);
        Question fromDb = dao.getQuestion(id);

        assertNotNull(fromDb);
        assertEquals("question_response", fromDb.getQuestionType());
        assertEquals("Who was the first US president?", fromDb.getQuestionText());
        assertNotNull(fromDb.getAnswers());
        assertTrue(fromDb.getAnswers().contains("George Washington"));
        assertTrue(fromDb.getAnswers().contains("Washington"));
        assertNull(fromDb.getChoices());
    }

    @Test
    public void testAddAndGetMultipleChoiceQuestion() throws Exception {
        QuestionDAO dao = new QuestionDAO();

        Question question = new Question();
        question.setQuizId(quizId);
        question.setQuestionType("multiple_choice");
        question.setQuestionText("Which planet is known as the Red Planet?");
        question.setImageUrl(null);
        question.setQuestionOrder(2);
        question.setChoices(Arrays.asList(
                new Choice("Mars", true),
                new Choice("Venus", false),
                new Choice("Earth", false)
        ));

        int id = dao.addQuestion(question);
        assertTrue(id > 0);

        Question fromDb = dao.getQuestion(id);

        assertNotNull(fromDb);
        assertEquals("multiple_choice", fromDb.getQuestionType());
        assertEquals("Which planet is known as the Red Planet?", fromDb.getQuestionText());
        assertNotNull(fromDb.getChoices());
        assertEquals(3, fromDb.getChoices().size());
        assertTrue(fromDb.getChoices().stream().anyMatch(c -> c.getChoiceText().equals("Mars") && c.isCorrect()));
    }

    @Test
    public void testUpdateQuestion() throws Exception {
        QuestionDAO dao = new QuestionDAO();

        Question question = new Question();
        question.setQuizId(quizId);
        question.setQuestionType("question_response");
        question.setQuestionText("Who is CEO of Tesla?");
        question.setImageUrl(null);
        question.setQuestionOrder(3);
        question.setAnswers(Collections.singletonList("Elon Musk"));
        int id = dao.addQuestion(question);

        // Change to multiple_choice and update fields
        question.setQuestionType("multiple_choice");
        question.setQuestionText("Who is CEO of SpaceX?");
        question.setChoices(Arrays.asList(
                new Choice("Jeff Bezos", false),
                new Choice("Elon Musk", true),
                new Choice("Tim Cook", false)
        ));
        question.setAnswers(null); // Should be ignored

        dao.updateQuestion(question);

        Question updated = dao.getQuestion(id);
        assertNotNull(updated);
        assertEquals("multiple_choice", updated.getQuestionType());
        assertEquals("Who is CEO of SpaceX?", updated.getQuestionText());
        assertNotNull(updated.getChoices());
        assertEquals(3, updated.getChoices().size());
        assertTrue(updated.getChoices().stream().anyMatch(c -> c.getChoiceText().equals("Elon Musk") && c.isCorrect()));
    }

    @Test
    public void testDeleteQuestion() throws Exception {
        QuestionDAO dao = new QuestionDAO();

        Question question = new Question();
        question.setQuizId(quizId);
        question.setQuestionType("fill_blank");
        question.setQuestionText("Stanford was founded in ____.");
        question.setImageUrl(null);
        question.setQuestionOrder(4);
        question.setAnswers(Collections.singletonList("1891"));
        int id = dao.addQuestion(question);

        dao.deleteQuestion(id);

        Question shouldBeNull = dao.getQuestion(id);
        assertNull(shouldBeNull, "Question should be deleted");
    }

    @Test
    public void testGetQuestionsForQuiz() throws Exception {
        QuestionDAO dao = new QuestionDAO();

        Question q1 = new Question();
        q1.setQuizId(quizId);
        q1.setQuestionType("question_response");
        q1.setQuestionText("What's 2 + 2?");
        q1.setImageUrl(null);
        q1.setQuestionOrder(1);
        q1.setAnswers(Collections.singletonList("4"));
        dao.addQuestion(q1);

        Question q2 = new Question();
        q2.setQuizId(quizId);
        q2.setQuestionType("multiple_choice");
        q2.setQuestionText("Select a prime: ");
        q2.setImageUrl(null);
        q2.setQuestionOrder(2);
        q2.setChoices(Arrays.asList(
                new Choice("4", false),
                new Choice("5", true),
                new Choice("6", false)
        ));
        dao.addQuestion(q2);

        List<Question> all = dao.getQuestionsForQuiz(quizId);
        assertNotNull(all);
        assertEquals(2, all.size());
        assertTrue(all.stream().anyMatch(q -> "What's 2 + 2?".equals(q.getQuestionText())));
        assertTrue(all.stream().anyMatch(q -> "Select a prime: ".equals(q.getQuestionText())));
    }
}

