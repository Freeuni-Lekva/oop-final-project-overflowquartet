package DB;

import Bean.QuizAttempt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizAttemptDAO {

    /**
     * Inserts a new quiz attempt. attemptDate is set by the DB to CURRENT_TIMESTAMP.
     */
    public boolean createQuizAttempt(QuizAttempt attempt) {
        String sql = "INSERT INTO quiz_attempts (user_id, quiz_id, score, duration_seconds) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, attempt.getUserId());
            ps.setInt(2, attempt.getQuizId());
            ps.setInt(3, attempt.getScore());
            ps.setInt(4, attempt.getDurationSeconds());

            int rows = ps.executeUpdate();
            if (rows == 0) return false;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    attempt.setAttemptId(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves a single attempt by its ID.
     */
    public QuizAttempt getQuizAttemptById(int attemptId) {
        String sql = "SELECT * FROM quiz_attempts WHERE attempt_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, attemptId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToQuizAttempt(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all attempts by a given user.
     */
    public List<QuizAttempt> getAttemptsByUser(int userId) {
        String sql = "SELECT * FROM quiz_attempts WHERE user_id = ? ORDER BY attempt_date DESC";
        List<QuizAttempt> list = new ArrayList<>();
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToQuizAttempt(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Retrieves all attempts for a given quiz.
     */
    public List<QuizAttempt> getAttemptsByQuiz(int quizId) {
        String sql = "SELECT * FROM quiz_attempts WHERE quiz_id = ? ORDER BY attempt_date DESC";
        List<QuizAttempt> list = new ArrayList<>();
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToQuizAttempt(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Updates score and duration for an existing attempt.
     */
    public boolean updateQuizAttempt(QuizAttempt attempt) {
        String sql = "UPDATE quiz_attempts SET score = ?, duration_seconds = ? WHERE attempt_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, attempt.getScore());
            ps.setInt(2, attempt.getDurationSeconds());
            ps.setInt(3, attempt.getAttemptId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes an attempt (and cascades to attempt_answers if your schema is set to CASCADE).
     */
    public boolean deleteQuizAttempt(int attemptId) {
        String sql = "DELETE FROM quiz_attempts WHERE attempt_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, attemptId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Helper to map a ResultSet row to a QuizAttempt bean.
     */
    private QuizAttempt mapRowToQuizAttempt(ResultSet rs) throws SQLException {
        QuizAttempt a = new QuizAttempt();
        a.setAttemptId(rs.getInt("attempt_id"));
        a.setUserId(rs.getInt("user_id"));
        a.setQuizId(rs.getInt("quiz_id"));
        a.setScore(rs.getInt("score"));
        a.setDurationSeconds(rs.getInt("duration_seconds"));
        a.setAttemptDate(rs.getTimestamp("attempt_date"));
        return a;
    }

}
