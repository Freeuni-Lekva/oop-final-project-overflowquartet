//
//
//import Bean.Question;
//import Bean.Choice;
//import DB.DBConnector;
//import DB.QuestionDAO;
//import org.junit.jupiter.api.*;
//
//import java.sql.Connection;
//import java.sql.Statement;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class QuestionDAOTest {
//
//    private QuestionDAO dao;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        dao = new QuestionDAO();
//        // clean out all tables before each test
//        try (Connection conn = DBConnector.getConnection();
//             Statement st = conn.createStatement()) {
//            st.executeUpdate("DELETE FROM question_choices");
//            st.executeUpdate("DELETE FROM questions");
//        }
//    }
//
//    @Test
//    void testAddAndGetMultipleChoiceQuestion() throws Exception {
//        // prepare a multiple-choice question
//        Question q = new Question();
//        q.setQuizId(100);
//        q.setQuestionType("multiple_choice");
//        q.setQuestionText("What is 2+2?");
//        q.setImageUrl(null);
//        q.setQuestionOrder(1);
//        Choice c1 = new Choice(); c1.setChoiceText("3"); c1.setCorrect(false);
//        Choice c2 = new Choice(); c2.setChoiceText("4"); c2.setCorrect(true);
//        q.setChoices(Arrays.asList(c1, c2));
//
//        int id = dao.addQuestion(q);
//        assertTrue(id > 0, "addQuestion should return generated key > 0");
//
//        Question fetched = dao.getQuestion(id);
//        assertNotNull(fetched, "getQuestion should return a Question object");
//        assertEquals("What is 2+2?", fetched.getQuestionText());
//        List<Choice> choices = fetched.getChoices();
//        assertEquals(2, choices.size(), "There should be exactly 2 choices");
//        assertTrue(choices.stream().anyMatch(ch -> ch.isCorrect() && "4".equals(ch.getChoiceText())));
//    }
//
//    @Test
//    void testAddAndGetQuestionResponse() throws Exception {
//        // prepare a free-response question
//        Question q = new Question();
//        q.setQuizId(200);
//        q.setQuestionType("question_response");
//        q.setQuestionText("Name a primary color.");
//        q.setImageUrl(null);
//        q.setQuestionOrder(2);
//        q.setAnswers(Arrays.asList("red", "blue", "yellow"));
//
//        int id = dao.addQuestion(q);
//        assertTrue(id > 0);
//
//        Question fetched = dao.getQuestion(id);
//        assertNotNull(fetched);
//        assertEquals("Name a primary color.", fetched.getQuestionText());
//        List<String> answers = fetched.getAnswers();
//        assertEquals(3, answers.size());
//        assertTrue(answers.contains("red"));
//    }
//
//    @Test
//    void testGetQuestionsForQuiz() throws Exception {
//        // Add two questions under the same quiz
//        Question q1 = new Question();
//        q1.setQuizId(300);
//        q1.setQuestionType("question_response");
//        q1.setQuestionText("First question?");
//        q1.setImageUrl(null);
//        q1.setQuestionOrder(1);
//        q1.setAnswers(Arrays.asList("a1"));
//        int id1 = dao.addQuestion(q1);
//
//        Question q2 = new Question();
//        q2.setQuizId(300);
//        q2.setQuestionType("question_response");
//        q2.setQuestionText("Second question?");
//        q2.setImageUrl(null);
//        q2.setQuestionOrder(2);
//        q2.setAnswers(Arrays.asList("a2"));
//        int id2 = dao.addQuestion(q2);
//
//        List<Question> list = dao.getQuestionsForQuiz(300);
//        assertEquals(2, list.size(), "Should retrieve exactly two questions");
//        assertEquals(id1, list.get(0).getQuestionId(), "Questions should be ordered by question_order");
//        assertEquals(id2, list.get(1).getQuestionId());
//    }
//
//    @Test
//    void testUpdateQuestion() throws Exception {
//        // start by adding a multiple-choice question
//        Question q = new Question();
//        q.setQuizId(400);
//        q.setQuestionType("multiple_choice");
//        q.setQuestionText("Old text?");
//        q.setImageUrl(null);
//        q.setQuestionOrder(1);
//        Choice oldC = new Choice(); oldC.setChoiceText("old"); oldC.setCorrect(true);
//        q.setChoices(Arrays.asList(oldC));
//        int id = dao.addQuestion(q);
//
//        // modify it: change text, replace choices
//        q.setQuestionId(id);
//        q.setQuestionText("New text?");
//        Choice newC1 = new Choice(); newC1.setChoiceText("n1"); newC1.setCorrect(false);
//        Choice newC2 = new Choice(); newC2.setChoiceText("n2"); newC2.setCorrect(true);
//        q.setChoices(Arrays.asList(newC1, newC2));
//        dao.updateQuestion(q);
//
//        Question updated = dao.getQuestion(id);
//        assertEquals("New text?", updated.getQuestionText());
//        List<Choice> choices = updated.getChoices();
//        assertEquals(2, choices.size(), "Old choices should be deleted and new ones inserted");
//        assertTrue(choices.stream().anyMatch(ch -> "n2".equals(ch.getChoiceText()) && ch.isCorrect()));
//    }
//
//    @Test
//    void testDeleteQuestion() throws Exception {
//        // add a fill-in-the-blank question
//        Question q = new Question();
//        q.setQuizId(500);
//        q.setQuestionType("fill_blank");
//        q.setQuestionText("Blank?");
//        q.setImageUrl(null);
//        q.setQuestionOrder(1);
//        q.setAnswers(Arrays.asList("ans"));
//        int id = dao.addQuestion(q);
//
//        // now delete it
//        dao.deleteQuestion(id);
//
//        Question deleted = dao.getQuestion(id);
//        assertNull(deleted, "After deletion, getQuestion should return null");
//    }
//}
