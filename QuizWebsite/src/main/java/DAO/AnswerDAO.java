package DAO;

import Bean.Answer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnswerDAO {
    private Connection conn;

    public AnswerDAO(Connection conn) {
        this.conn = conn;
    }

    public void createAnswer(Answer answer) throws SQLException {
        String sql = "INSERT INTO answers (question_id, user_id, answer_text, is_correct) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, answer.getQuestionId());
            stmt.setInt(2, answer.getUserId());
            stmt.setString(3, answer.getAnswerText());
            stmt.setBoolean(4, answer.isCorrect());
            stmt.executeUpdate();
        }
    }

    public List<Answer> findByQuestionId(int questionId) throws SQLException {
        String sql = "SELECT * FROM answers WHERE question_id = ?";
        List<Answer> answers = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                answers.add(extractAnswer(rs));
            }
        }
        return answers;
    }

    public List<Answer> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM answers WHERE user_id = ?";
        List<Answer> answers = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                answers.add(extractAnswer(rs));
            }
        }
        return answers;
    }

    public void updateAnswer(Answer answer) throws SQLException {
        String sql = "UPDATE answers SET answer_text=?, is_correct=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, answer.getAnswerText());
            stmt.setBoolean(2, answer.isCorrect());
            stmt.setInt(3, answer.getAnswerId());
            stmt.executeUpdate();
        }
    }

    public void deleteAnswer(int answerId) throws SQLException {
        String sql = "DELETE FROM answers WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, answerId);
            stmt.executeUpdate();
        }
    }

    private Answer extractAnswer(ResultSet rs) throws SQLException {
        Answer answer = new Answer();
        answer.setAnswerId(rs.getInt("id"));
        answer.setQuestionId(rs.getInt("question_id"));
        answer.setUserId(rs.getInt("user_id"));
        answer.setAnswerText(rs.getString("answer_text"));
        answer.setCorrect(rs.getBoolean("is_correct"));
        return answer;
    }
} 