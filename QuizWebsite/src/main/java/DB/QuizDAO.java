package DB;

import Bean.Quiz;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO {

    public boolean createQuiz(int ownerId, String title, String description, boolean randomOrder, boolean multiplePages, boolean immediateCorrection) {
        String sql = "INSERT INTO quizzes (owner_id, title, description, random_order, multiple_pages, immediate_correction) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setBoolean(4, randomOrder);
            ps.setBoolean(5, multiplePages);
            ps.setBoolean(6, immediateCorrection);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public Quiz getQuizById(int quizId) {
        String sql = "SELECT * FROM quizzes WHERE quiz_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractQuiz(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateQuiz(Quiz quiz) {
        String sql = "UPDATE quizzes SET owner_id = ?, title = ?, description = ?, random_order = ?, multiple_pages = ?, immediate_correction = ? WHERE quiz_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quiz.getOwnerId());
            ps.setString(2, quiz.getTitle());
            ps.setString(3, quiz.getDescription());
            ps.setBoolean(4, quiz.isRandomOrder());
            ps.setBoolean(5, quiz.isMultiplePages());
            ps.setBoolean(6, quiz.isImmediateCorrection());
            ps.setInt(7, quiz.getQuizId());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteQuiz(int quizId) {
        String sql = "DELETE FROM quizzes WHERE quiz_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Quiz> searchQuizzes(String query) {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT * FROM quizzes WHERE title LIKE ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                quizzes.add(extractQuiz(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quizzes;
    }

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