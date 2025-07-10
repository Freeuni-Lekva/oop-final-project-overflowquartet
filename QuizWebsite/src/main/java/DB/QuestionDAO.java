package DB;

import Bean.Question;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

    public boolean createQuestion(int quizId, String questionType, String prompt, String imageUrl, int questionOrder, int points) {
        String sql = "INSERT INTO questions (quiz_id, question_type, question_text, image_url, question_order) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            ps.setString(2, questionType);
            ps.setString(3, prompt);
            ps.setString(4, imageUrl);
            ps.setInt(5, questionOrder);
            ps.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Question getQuestionById(int questionId) {
        String sql = "SELECT * FROM questions WHERE question_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractQuestion(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateQuestion(Question question, String imageUrl, int questionOrder) {
        String sql = "UPDATE questions SET quiz_id = ?, question_type = ?, question_text = ?, image_url = ?, question_order = ? WHERE question_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, question.getQuizId());
            ps.setString(2, getQuestionType(question));
            ps.setString(3, question.getPrompt());
            ps.setString(4, imageUrl);
            ps.setInt(5, questionOrder);
            ps.setInt(6, question.getQuestionId());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteQuestion(int questionId) {
        String sql = "DELETE FROM questions WHERE question_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Question> getQuestionsForQuiz(int quizId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE quiz_id = ? ORDER BY question_order ASC";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                questions.add(extractQuestion(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }


    private Question extractQuestion(ResultSet rs) throws SQLException {

        return new Question() {{
            setQuestionId(rs.getInt("question_id"));
            setQuizId(rs.getInt("quiz_id"));
            setPrompt(rs.getString("question_text"));

            setPoints(1);
        }};
    }


    private String getQuestionType(Question question) {

        return "question_response";
    }
}
