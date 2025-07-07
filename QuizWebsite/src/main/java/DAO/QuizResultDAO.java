package DAO;

import Bean.QuizResult;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizResultDAO {
    private Connection conn;

    public QuizResultDAO(Connection conn) {
        this.conn = conn;
    }

    public void createQuizResult(QuizResult result) throws SQLException {
        String sql = "INSERT INTO quiz_results (user_id, quiz_id, score, total_questions, time_taken, taken_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, result.getUserId());
            stmt.setInt(2, result.getQuizId());
            stmt.setInt(3, result.getScore());
            stmt.setInt(4, result.getTotalQuestions());
            stmt.setInt(5, result.getTimeTaken());
            stmt.setTimestamp(6, result.getTakenAt());
            stmt.executeUpdate();
        }
    }

    public List<QuizResult> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM quiz_results WHERE user_id = ?";
        List<QuizResult> results = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(extractQuizResult(rs));
            }
        }
        return results;
    }

    public List<QuizResult> findByQuizId(int quizId) throws SQLException {
        String sql = "SELECT * FROM quiz_results WHERE quiz_id = ?";
        List<QuizResult> results = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(extractQuizResult(rs));
            }
        }
        return results;
    }

    public void updateQuizResult(QuizResult result) throws SQLException {
        String sql = "UPDATE quiz_results SET score=?, total_questions=?, time_taken=?, taken_at=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, result.getScore());
            stmt.setInt(2, result.getTotalQuestions());
            stmt.setInt(3, result.getTimeTaken());
            stmt.setTimestamp(4, result.getTakenAt());
            stmt.setInt(5, result.getResultId());
            stmt.executeUpdate();
        }
    }

    public void deleteQuizResult(int resultId) throws SQLException {
        String sql = "DELETE FROM quiz_results WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, resultId);
            stmt.executeUpdate();
        }
    }

    private QuizResult extractQuizResult(ResultSet rs) throws SQLException {
        QuizResult result = new QuizResult();
        result.setResultId(rs.getInt("id"));
        result.setUserId(rs.getInt("user_id"));
        result.setQuizId(rs.getInt("quiz_id"));
        result.setScore(rs.getInt("score"));
        result.setTotalQuestions(rs.getInt("total_questions"));
        result.setTimeTaken(rs.getInt("time_taken"));
        result.setTakenAt(rs.getTimestamp("taken_at"));
        return result;
    }
} 