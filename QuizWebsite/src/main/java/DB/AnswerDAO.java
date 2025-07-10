package DB;

import Bean.Answer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnswerDAO {
    public void addAnswer(Answer answer) {
        String sql = "INSERT INTO answers (question_id, user_id, response_text, response_option_id, timestamp) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, answer.getQuestionId());
            ps.setInt(2, answer.getUserId());
            ps.setString(3, answer.getResponseText());
            if (answer.getResponseOptionId() != null) {
                ps.setInt(4, answer.getResponseOptionId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setTimestamp(5, answer.getTimestamp());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Answer> getAnswersForQuestion(int questionId) {
        List<Answer> answers = new ArrayList<>();
        String sql = "SELECT * FROM answers WHERE question_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                answers.add(extractAnswer(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answers;
    }

    public List<Answer> getAnswersForUser(int userId) {
        List<Answer> answers = new ArrayList<>();
        String sql = "SELECT * FROM answers WHERE user_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                answers.add(extractAnswer(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answers;
    }

    public void deleteAnswersForQuestion(int questionId) {
        String sql = "DELETE FROM answers WHERE question_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Answer extractAnswer(ResultSet rs) throws SQLException {
        Answer answer = new Answer();
        answer.setAnswerId(rs.getInt("answer_id"));
        answer.setQuestionId(rs.getInt("question_id"));
        answer.setUserId(rs.getInt("user_id"));
        answer.setResponseText(rs.getString("response_text"));
        int optionId = rs.getInt("response_option_id");
        answer.setResponseOptionId(rs.wasNull() ? null : optionId);
        answer.setTimestamp(rs.getTimestamp("timestamp"));
        return answer;
    }
}
