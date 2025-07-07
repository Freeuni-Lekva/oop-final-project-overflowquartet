package DAO;

import Bean.Quiz;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO {
    private Connection conn;

    public QuizDAO(Connection conn) {
        this.conn = conn;
    }

    public void createQuiz(Quiz quiz) throws SQLException {
        String sql = "INSERT INTO quizzes (owner_id, title, description, random_order, multiple_pages, immediate_correction, practice_mode, creation_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, quiz.getOwnerId());
            stmt.setString(2, quiz.getTitle());
            stmt.setString(3, quiz.getDescription());
            stmt.setBoolean(4, quiz.isRandomOrder());
            stmt.setBoolean(5, quiz.isMultiplePages());
            stmt.setBoolean(6, quiz.isImmediateCorrection());
            stmt.setBoolean(7, quiz.isPracticeMode());
            stmt.setTimestamp(8, quiz.getCreationDate());
            stmt.executeUpdate();
        }
    }

    public Quiz findById(int quizId) throws SQLException {
        String sql = "SELECT * FROM quizzes WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractQuiz(rs);
            }
        }
        return null;
    }

    public List<Quiz> findAll() throws SQLException {
        String sql = "SELECT * FROM quizzes";
        List<Quiz> quizzes = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                quizzes.add(extractQuiz(rs));
            }
        }
        return quizzes;
    }

    public void updateQuiz(Quiz quiz) throws SQLException {
        String sql = "UPDATE quizzes SET title=?, description=?, random_order=?, multiple_pages=?, immediate_correction=?, practice_mode=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, quiz.getTitle());
            stmt.setString(2, quiz.getDescription());
            stmt.setBoolean(3, quiz.isRandomOrder());
            stmt.setBoolean(4, quiz.isMultiplePages());
            stmt.setBoolean(5, quiz.isImmediateCorrection());
            stmt.setBoolean(6, quiz.isPracticeMode());
            stmt.setInt(7, quiz.getQuizId());
            stmt.executeUpdate();
        }
    }

    public void deleteQuiz(int quizId) throws SQLException {
        String sql = "DELETE FROM quizzes WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            stmt.executeUpdate();
        }
    }

    private Quiz extractQuiz(ResultSet rs) throws SQLException {
        Quiz quiz = new Quiz();
        quiz.setQuizId(rs.getInt("id"));
        quiz.setOwnerId(rs.getInt("owner_id"));
        quiz.setTitle(rs.getString("title"));
        quiz.setDescription(rs.getString("description"));
        quiz.setRandomOrder(rs.getBoolean("random_order"));
        quiz.setMultiplePages(rs.getBoolean("multiple_pages"));
        quiz.setImmediateCorrection(rs.getBoolean("immediate_correction"));
        quiz.setPracticeMode(rs.getBoolean("practice_mode"));
        quiz.setCreationDate(rs.getTimestamp("creation_date"));
        // quiz.setQuestions(...) // To be implemented with QuestionDAO
        return quiz;
    }
} 