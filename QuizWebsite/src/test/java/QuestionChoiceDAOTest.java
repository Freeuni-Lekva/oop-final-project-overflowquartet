import Bean.Choice;
import DB.DBConnector;
import DB.QuestionChoiceDAO;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class QuestionChoiceDAOTest {

    private int userId;
    private int quizId;
    private int questionId;

    @BeforeEach
    public void setup() throws SQLException {
        try (Connection conn = DBConnector.getConnection()) {
            // Insert user
            PreparedStatement userStmt = conn.prepareStatement(
                    "INSERT INTO users (username, password_hash, display_name) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, "testuser_" + System.nanoTime());
            userStmt.setString(2, "hash");
            userStmt.setString(3, "Choice Test User");
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
            quizStmt.setString(2, "Test Quiz for Choices " + System.nanoTime());
            quizStmt.setString(3, "ChoiceDAO Testing");
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
            qStmt.setString(2, "multiple_choice");
            qStmt.setString(3, "Pick the right answer");
            qStmt.setString(4, null);
            qStmt.setInt(5, 1);
            qStmt.executeUpdate();
            try (ResultSet rs = qStmt.getGeneratedKeys()) {
                assertTrue(rs.next());
                questionId = rs.getInt(1);
            }
        }
    }

    @AfterEach
    public void cleanup() throws SQLException {
        try (Connection conn = DBConnector.getConnection()) {
            // Delete choices
            PreparedStatement delChoices = conn.prepareStatement(
                    "DELETE FROM question_choices WHERE question_id = ?");
            delChoices.setInt(1, questionId);
            delChoices.executeUpdate();
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
    public void testAddAndGetSingleChoice() {
        QuestionChoiceDAO dao = new QuestionChoiceDAO();
        Choice choice = new Choice("Choice A", true);
        choice.setQuestionId(questionId);
        boolean added = dao.addChoice(choice);
        assertTrue(added);
        assertTrue(choice.getChoiceId() > 0);

        Choice fromDb = dao.getChoiceById(choice.getChoiceId());
        assertNotNull(fromDb);
        assertEquals("Choice A", fromDb.getChoiceText());
        assertTrue(fromDb.isCorrect());
        assertEquals(questionId, fromDb.getQuestionId());
    }

    @Test
    public void testAddAndGetMultipleChoices() {
        QuestionChoiceDAO dao = new QuestionChoiceDAO();
        Choice c1 = new Choice("Choice 1", false); c1.setQuestionId(questionId);
        Choice c2 = new Choice("Choice 2", true);  c2.setQuestionId(questionId);
        Choice c3 = new Choice("Choice 3", false); c3.setQuestionId(questionId);
        List<Choice> choices = Arrays.asList(c1, c2, c3);
        boolean added = dao.addChoices(choices, questionId);
        assertTrue(added);
        // Check IDs set
        choices.forEach(c -> assertTrue(c.getChoiceId() > 0));
        // Get all for question
        List<Choice> fromDb = dao.getChoicesForQuestion(questionId);
        assertNotNull(fromDb);
        assertEquals(3, fromDb.size());
        assertTrue(fromDb.stream().anyMatch(c -> c.getChoiceText().equals("Choice 2") && c.isCorrect()));
    }

    @Test
    public void testUpdateChoice() {
        QuestionChoiceDAO dao = new QuestionChoiceDAO();
        Choice choice = new Choice("To update", false);
        choice.setQuestionId(questionId);
        assertTrue(dao.addChoice(choice));
        int id = choice.getChoiceId();

        // Change values
        choice.setChoiceText("Updated Text");
        choice.setCorrect(true);
        boolean updated = dao.updateChoice(choice);
        assertTrue(updated);

        Choice updatedChoice = dao.getChoiceById(id);
        assertNotNull(updatedChoice);
        assertEquals("Updated Text", updatedChoice.getChoiceText());
        assertTrue(updatedChoice.isCorrect());
    }

    @Test
    public void testDeleteChoice() {
        QuestionChoiceDAO dao = new QuestionChoiceDAO();
        Choice choice = new Choice("To delete", false);
        choice.setQuestionId(questionId);
        assertTrue(dao.addChoice(choice));
        int id = choice.getChoiceId();

        boolean deleted = dao.deleteChoice(id);
        assertTrue(deleted);

        Choice shouldBeNull = dao.getChoiceById(id);
        assertNull(shouldBeNull);
    }

    @Test
    public void testDeleteChoicesForQuestion() {
        QuestionChoiceDAO dao = new QuestionChoiceDAO();
        Choice c1 = new Choice("A", false); c1.setQuestionId(questionId);
        Choice c2 = new Choice("B", false); c2.setQuestionId(questionId);
        assertTrue(dao.addChoices(Arrays.asList(c1, c2), questionId));
        List<Choice> before = dao.getChoicesForQuestion(questionId);
        assertEquals(2, before.size());

        boolean allDeleted = dao.deleteChoicesForQuestion(questionId);
        assertTrue(allDeleted);
        List<Choice> after = dao.getChoicesForQuestion(questionId);
        assertTrue(after.isEmpty());
    }
}
