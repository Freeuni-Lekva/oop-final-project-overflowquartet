package DB;

import Bean.AttemptAnswer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the attempt_answers table.
 */
public class AttemptAnswerDAO {

    /**
     * Inserts a new attempt answer.
     * On success, sets the generated attemptAnswerId in the AttemptAnswer object.
     */
    public boolean addAttemptAnswer(AttemptAnswer answer) {
        String sql = "INSERT INTO attempt_answers (attempt_id, question_id, user_answer_text, is_correct) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, answer.getAttemptId());
            ps.setInt(2, answer.getQuestionId());
            ps.setString(3, answer.getUserAnswerText());
            if (answer.getIsCorrect() != null) {
                ps.setBoolean(4, answer.getIsCorrect());
            } else {
                ps.setNull(4, Types.BOOLEAN);
            }

            int rows = ps.executeUpdate();
            if (rows == 0) return false;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    answer.setAttemptAnswerId(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adds a batch of AttemptAnswers for a single attempt.
     */
    public boolean addAttemptAnswers(List<AttemptAnswer> answers) {
        if (answers == null || answers.isEmpty()) return true;
        String sql = "INSERT INTO attempt_answers (attempt_id, question_id, user_answer_text, is_correct) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (AttemptAnswer answer : answers) {
                ps.setInt(1, answer.getAttemptId());
                ps.setInt(2, answer.getQuestionId());
                ps.setString(3, answer.getUserAnswerText());
                if (answer.getIsCorrect() != null) {
                    ps.setBoolean(4, answer.getIsCorrect());
                } else {
                    ps.setNull(4, Types.BOOLEAN);
                }
                ps.addBatch();
            }
            int[] results = ps.executeBatch();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                int i = 0;
                while (keys.next() && i < answers.size()) {
                    answers.get(i++).setAttemptAnswerId(keys.getInt(1));
                }
            }
            for (int res : results) if (res == Statement.EXECUTE_FAILED) return false;
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all answers for a given attempt.
     */
    public List<AttemptAnswer> getAnswersForAttempt(int attemptId) {
        String sql = "SELECT * FROM attempt_answers WHERE attempt_id = ?";
        List<AttemptAnswer> list = new ArrayList<>();
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, attemptId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToAttemptAnswer(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Retrieves all answers for a given attempt with question text included.
     */
    public List<AttemptAnswer> getAnswersForAttemptWithQuestions(int attemptId) {
        String sql = "SELECT aa.*, q.question_text FROM attempt_answers aa " +
                     "JOIN questions q ON aa.question_id = q.question_id " +
                     "WHERE aa.attempt_id = ?";
        List<AttemptAnswer> list = new ArrayList<>();
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, attemptId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToAttemptAnswerWithQuestion(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Retrieves all answers for a given question across all attempts (e.g., for stats).
     */
    public List<AttemptAnswer> getAnswersForQuestion(int questionId) {
        String sql = "SELECT * FROM attempt_answers WHERE question_id = ?";
        List<AttemptAnswer> list = new ArrayList<>();
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToAttemptAnswer(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Retrieves a single AttemptAnswer by its ID.
     */
    public AttemptAnswer getAttemptAnswerById(int attemptAnswerId) {
        String sql = "SELECT * FROM attempt_answers WHERE attempt_answer_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, attemptAnswerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAttemptAnswer(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates the user's answer and correctness for an attempt answer.
     */
    public boolean updateAttemptAnswer(AttemptAnswer answer) {
        String sql = "UPDATE attempt_answers SET user_answer_text = ?, is_correct = ? WHERE attempt_answer_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, answer.getUserAnswerText());
            if (answer.getIsCorrect() != null) {
                ps.setBoolean(2, answer.getIsCorrect());
            } else {
                ps.setNull(2, Types.BOOLEAN);
            }
            ps.setInt(3, answer.getAttemptAnswerId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a single attempt answer.
     */
    public boolean deleteAttemptAnswer(int attemptAnswerId) {
        String sql = "DELETE FROM attempt_answers WHERE attempt_answer_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, attemptAnswerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes all attempt answers for a given attempt.
     */
    public boolean deleteAnswersForAttempt(int attemptId) {
        String sql = "DELETE FROM attempt_answers WHERE attempt_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, attemptId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Helper to map a row to an AttemptAnswer bean.
     */
    private AttemptAnswer mapRowToAttemptAnswer(ResultSet rs) throws SQLException {
        AttemptAnswer a = new AttemptAnswer();
        a.setAttemptAnswerId(rs.getInt("attempt_answer_id"));
        a.setAttemptId(rs.getInt("attempt_id"));
        a.setQuestionId(rs.getInt("question_id"));
        a.setUserAnswerText(rs.getString("user_answer_text"));
        // Handle NULL for is_correct
        boolean correct = rs.getBoolean("is_correct");
        if (rs.wasNull()) {
            a.setIsCorrect(null);
        } else {
            a.setIsCorrect(correct);
        }
        return a;
    }

    /**
     * Helper to map a row to an AttemptAnswer bean with question text.
     */
    private AttemptAnswer mapRowToAttemptAnswerWithQuestion(ResultSet rs) throws SQLException {
        AttemptAnswer a = new AttemptAnswer();
        a.setAttemptAnswerId(rs.getInt("attempt_answer_id"));
        a.setAttemptId(rs.getInt("attempt_id"));
        a.setQuestionId(rs.getInt("question_id"));
        a.setUserAnswerText(rs.getString("user_answer_text"));
        a.setQuestionText(rs.getString("question_text"));
        // Handle NULL for is_correct
        boolean correct = rs.getBoolean("is_correct");
        if (rs.wasNull()) {
            a.setIsCorrect(null);
        } else {
            a.setIsCorrect(correct);
        }
        return a;
    }
}
