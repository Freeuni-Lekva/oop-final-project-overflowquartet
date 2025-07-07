package DAO;

import Bean.Question;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {
    private Connection conn;

    public QuestionDAO(Connection conn) {
        this.conn = conn;
    }

    public void createQuestion(Question question) throws SQLException {
        String sql = "INSERT INTO questions (quiz_id, type, prompt, image_url, question_order) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, question.getQuizId());
            stmt.setString(2, question.getType());
            stmt.setString(3, question.getPrompt());
            stmt.setString(4, question.getImageUrl());
            stmt.setInt(5, question.getOrder());
            stmt.executeUpdate();
        }
    }

    public Question findById(int questionId) throws SQLException {
        String sql = "SELECT * FROM questions WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractQuestion(rs);
            }
        }
        return null;
    }

    public List<Question> findByQuizId(int quizId) throws SQLException {
        String sql = "SELECT * FROM questions WHERE quiz_id = ? ORDER BY question_order";
        List<Question> questions = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                questions.add(extractQuestion(rs));
            }
        }
        return questions;
    }

    public void updateQuestion(Question question) throws SQLException {
        String sql = "UPDATE questions SET type=?, prompt=?, image_url=?, question_order=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, question.getType());
            stmt.setString(2, question.getPrompt());
            stmt.setString(3, question.getImageUrl());
            stmt.setInt(4, question.getOrder());
            stmt.setInt(5, question.getQuestionId());
            stmt.executeUpdate();
        }
    }

    public void deleteQuestion(int questionId) throws SQLException {
        String sql = "DELETE FROM questions WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            stmt.executeUpdate();
        }
    }

    private Question extractQuestion(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setQuestionId(rs.getInt("id"));
        question.setQuizId(rs.getInt("quiz_id"));
        question.setType(rs.getString("type"));
        question.setPrompt(rs.getString("prompt"));
        question.setImageUrl(rs.getString("image_url"));
        question.setOrder(rs.getInt("question_order"));
        // question.setChoices(...) // To be implemented
        // question.setCorrectAnswers(...) // To be implemented
        return question;
    }
} 