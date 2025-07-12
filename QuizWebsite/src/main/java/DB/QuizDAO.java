package DB;

import Bean.Quiz;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Quiz entities.
 * Adds validation for owner existence, better error handling,
 * and additional retrieval methods.
 */
public class QuizDAO {

    /**
     * Checks whether a user with the given ID exists.
     */
    private boolean ownerExists(int ownerId) {
        String sql = "SELECT 1 FROM users WHERE user_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            // Log or rethrow as needed
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Inserts a new Quiz, returning its generated ID, or null on failure or invalid owner.
     */
    public Integer createQuiz(int ownerId,
                              String title,
                              String description,
                              boolean randomOrder,
                              boolean multiplePages,
                              boolean immediateCorrection) {
        if (!ownerExists(ownerId)) {
            return null;
        }
        String sql = "INSERT INTO quizzes (owner_id, title, description, random_order, multiple_pages, immediate_correction) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, ownerId);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setBoolean(4, randomOrder);
            ps.setBoolean(5, multiplePages);
            ps.setBoolean(6, immediateCorrection);

            int affected = ps.executeUpdate();
            if (affected == 0) {
                return null;
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            // Handle specific constraint violations if desired
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a Quiz by its ID.
     */
    public Quiz getQuizById(int quizId) {
        String sql = "SELECT q.*, u.username AS owner_username FROM quizzes q LEFT JOIN users u ON q.owner_id = u.user_id WHERE q.quiz_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Quiz quiz = extractQuiz(rs);
                    quiz.setOwnerUsername(rs.getString("owner_username"));
                    return quiz;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all quizzes owned by the given user with question counts.
     */
    public List<Quiz> getQuizzesByOwnerWithQuestionCount(int ownerId) {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT q.*, u.username AS owner_username, COUNT(ques.question_id) AS question_count " +
                     "FROM quizzes q " +
                     "LEFT JOIN users u ON q.owner_id = u.user_id " +
                     "LEFT JOIN questions ques ON q.quiz_id = ques.quiz_id " +
                     "WHERE q.owner_id = ? " +
                     "GROUP BY q.quiz_id, u.username " +
                     "ORDER BY q.creation_date DESC";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Quiz quiz = extractQuiz(rs);
                    quiz.setQuestionCount(rs.getInt("question_count"));
                    quiz.setOwnerUsername(rs.getString("owner_username"));
                    list.add(quiz);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Retrieves all quizzes owned by the given user.
     */
    public List<Quiz> getQuizzesByOwner(int ownerId) {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT q.*, u.username AS owner_username FROM quizzes q LEFT JOIN users u ON q.owner_id = u.user_id WHERE q.owner_id = ? ORDER BY q.creation_date DESC";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Quiz quiz = extractQuiz(rs);
                    quiz.setOwnerUsername(rs.getString("owner_username"));
                    list.add(quiz);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Updates an existing quiz. Returns true if update was successful.
     */
    public boolean updateQuiz(Quiz quiz) {
        if (!ownerExists(quiz.getOwnerId())) {
            return false;
        }
        String sql = "UPDATE quizzes SET owner_id = ?, title = ?, description = ?, random_order = ?, "
                + "multiple_pages = ?, immediate_correction = ? WHERE quiz_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quiz.getOwnerId());
            ps.setString(2, quiz.getTitle());
            ps.setString(3, quiz.getDescription());
            ps.setBoolean(4, quiz.isRandomOrder());
            ps.setBoolean(5, quiz.isMultiplePages());
            ps.setBoolean(6, quiz.isImmediateCorrection());
            ps.setInt(7, quiz.getQuizId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a quiz by ID. Returns true if deletion was successful.
     */
    public boolean deleteQuiz(int quizId) {
        String sql = "DELETE FROM quizzes WHERE quiz_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Searches quizzes by title substring.
     */
    public List<Quiz> searchQuizzes(String query) {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT * FROM quizzes WHERE title LIKE ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractQuiz(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Retrieves all quizzes with their question counts (for featured quizzes).
     */
    public List<Quiz> getAllQuizzesWithQuestionCount() {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT q.*, u.username AS owner_username, COUNT(ques.question_id) AS question_count " +
                     "FROM quizzes q " +
                     "LEFT JOIN users u ON q.owner_id = u.user_id " +
                     "LEFT JOIN questions ques ON q.quiz_id = ques.quiz_id " +
                     "GROUP BY q.quiz_id, u.username " +
                     "ORDER BY q.creation_date DESC";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Quiz quiz = extractQuiz(rs);
                    quiz.setQuestionCount(rs.getInt("question_count"));
                    quiz.setOwnerUsername(rs.getString("owner_username"));
                    list.add(quiz);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Helper to map a ResultSet row to a Quiz object.
     */
    private Quiz extractQuiz(ResultSet rs) throws SQLException {
        Quiz quiz = new Quiz();
        quiz.setQuizId(rs.getInt("quiz_id"));
        quiz.setOwnerId(rs.getInt("owner_id"));
        quiz.setTitle(rs.getString("title"));
        quiz.setDescription(rs.getString("description"));
        quiz.setRandomOrder(rs.getBoolean("random_order"));
        quiz.setMultiplePages(rs.getBoolean("multiple_pages"));
        quiz.setImmediateCorrection(rs.getBoolean("immediate_correction"));
        quiz.setCreationDate(rs.getTimestamp("creation_date"));
        try { quiz.setOwnerUsername(rs.getString("owner_username")); } catch (SQLException ignore) {}
        return quiz;
    }

    public List<Quiz> getAllQuizzes() {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT q.*, u.username AS owner_username FROM quizzes q LEFT JOIN users u ON q.owner_id = u.user_id ORDER BY q.creation_date DESC";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Quiz quiz = extractQuiz(rs);
                    quiz.setOwnerUsername(rs.getString("owner_username"));
                    list.add(quiz);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Get the most popular quizzes (by number of attempts) with question count and attempt count.
     */
    public List<Quiz> getPopularQuizzes(int limit) {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT q.*, u.username AS owner_username, " +
                     "COUNT(DISTINCT ques.question_id) AS question_count, " +
                     "COALESCE(attempt_counts.attempt_count, 0) AS attempt_count " +
                     "FROM quizzes q " +
                     "LEFT JOIN users u ON q.owner_id = u.user_id " +
                     "LEFT JOIN questions ques ON q.quiz_id = ques.quiz_id " +
                     "LEFT JOIN (SELECT quiz_id, COUNT(*) AS attempt_count FROM quiz_attempts GROUP BY quiz_id) attempt_counts ON q.quiz_id = attempt_counts.quiz_id " +
                     "GROUP BY q.quiz_id, u.username, attempt_counts.attempt_count " +
                     "ORDER BY attempt_count DESC, q.creation_date DESC " +
                     "LIMIT ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Quiz quiz = extractQuiz(rs);
                    quiz.setQuestionCount(rs.getInt("question_count"));
                    quiz.setOwnerUsername(rs.getString("owner_username"));
                    quiz.setAttemptCount(rs.getInt("attempt_count"));
                    list.add(quiz);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Clear all history information for a particular quiz (admin function).
     */
    public boolean clearQuizHistory(int quizId) {
        String sql = "DELETE FROM attempt_answers WHERE attempt_id IN (SELECT attempt_id FROM quiz_attempts WHERE quiz_id = ?)";
        String sql2 = "DELETE FROM quiz_attempts WHERE quiz_id = ?";
        
        try (Connection conn = DBConnector.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Delete attempt answers first
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, quizId);
                    ps.executeUpdate();
                }
                
                // Then delete quiz attempts
                try (PreparedStatement ps = conn.prepareStatement(sql2)) {
                    ps.setInt(1, quizId);
                    ps.executeUpdate();
                }
                
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
