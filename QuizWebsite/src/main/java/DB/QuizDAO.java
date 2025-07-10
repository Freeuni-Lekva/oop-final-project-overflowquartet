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
        String sql = "SELECT * FROM quizzes WHERE quiz_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractQuiz(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all quizzes owned by the given user.
     */
    public List<Quiz> getQuizzesByOwner(int ownerId) {
        List<Quiz> list = new ArrayList<>();
        String sql = "SELECT * FROM quizzes WHERE owner_id = ? ORDER BY creation_date DESC";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
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
        return quiz;
    }
}
