package DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionAnswerDAO {


    public void addAnswer(int questionId, String answerText) {
        String sql = "INSERT INTO question_answers (question_id, answer_text) VALUES (?, ?)";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            ps.setString(2, answerText.trim()); // Trim to normalize storage
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding answer for question " + questionId, e);
        }
    }


    public List<String> getAnswersForQuestion(int questionId) {
        List<String> answers = new ArrayList<>();
        String sql = "SELECT answer_text FROM question_answers WHERE question_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    answers.add(rs.getString("answer_text").trim());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching answers for question " + questionId, e);
        }
        return answers;
    }


    public boolean isCorrectAnswer(int questionId, String response) {
        String sql = "SELECT COUNT(*) FROM question_answers " +
                "WHERE question_id = ? AND LOWER(TRIM(answer_text)) = LOWER(TRIM(?))";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            ps.setString(2, response);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking answer for question " + questionId, e);
        }
    }


    public void deleteAnswersForQuestion(int questionId) {
        String sql = "DELETE FROM question_answers WHERE question_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting answers for question " + questionId, e);
        }
    }
}
