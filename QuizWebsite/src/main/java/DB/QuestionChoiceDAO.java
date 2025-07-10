package DB;

import Bean.Choice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for question_choices table.
 */
public class QuestionChoiceDAO {

    /**
     * Inserts a single choice for a question.
     * On success, sets generated choiceId in the Choice object.
     */
    public boolean addChoice(Choice choice) {
        String sql = "INSERT INTO question_choices (question_id, choice_text, is_correct) VALUES (?, ?, ?)";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, choice.getQuestionId());
            ps.setString(2, choice.getChoiceText());
            ps.setBoolean(3, choice.isCorrect());

            int rows = ps.executeUpdate();
            if (rows == 0) return false;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    choice.setChoiceId(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Inserts a list of choices for a question (batch).
     */
    public boolean addChoices(List<Choice> choices, int questionId) {
        if (choices == null || choices.isEmpty()) return true; // nothing to do
        String sql = "INSERT INTO question_choices (question_id, choice_text, is_correct) VALUES (?, ?, ?)";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (Choice choice : choices) {
                ps.setInt(1, questionId);
                ps.setString(2, choice.getChoiceText());
                ps.setBoolean(3, choice.isCorrect());
                ps.addBatch();
            }
            int[] results = ps.executeBatch();

            // Set IDs
            try (ResultSet keys = ps.getGeneratedKeys()) {
                int i = 0;
                while (keys.next() && i < choices.size()) {
                    choices.get(i++).setChoiceId(keys.getInt(1));
                }
            }

            // Check all inserted
            for (int res : results) if (res == Statement.EXECUTE_FAILED) return false;
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all choices for a given question.
     */
    public List<Choice> getChoicesForQuestion(int questionId) {
        String sql = "SELECT * FROM question_choices WHERE question_id = ?";
        List<Choice> list = new ArrayList<>();
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Choice c = new Choice();
                    c.setChoiceId(rs.getInt("choice_id"));
                    c.setQuestionId(rs.getInt("question_id"));
                    c.setChoiceText(rs.getString("choice_text"));
                    c.setCorrect(rs.getBoolean("is_correct"));
                    list.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Retrieves a single choice by its ID.
     */
    public Choice getChoiceById(int choiceId) {
        String sql = "SELECT * FROM question_choices WHERE choice_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, choiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Choice c = new Choice();
                    c.setChoiceId(rs.getInt("choice_id"));
                    c.setQuestionId(rs.getInt("question_id"));
                    c.setChoiceText(rs.getString("choice_text"));
                    c.setCorrect(rs.getBoolean("is_correct"));
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates the text and correctness of a choice.
     */
    public boolean updateChoice(Choice choice) {
        String sql = "UPDATE question_choices SET choice_text = ?, is_correct = ? WHERE choice_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, choice.getChoiceText());
            ps.setBoolean(2, choice.isCorrect());
            ps.setInt(3, choice.getChoiceId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a single choice by ID.
     */
    public boolean deleteChoice(int choiceId) {
        String sql = "DELETE FROM question_choices WHERE choice_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, choiceId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes all choices for a given question.
     */
    public boolean deleteChoicesForQuestion(int questionId) {
        String sql = "DELETE FROM question_choices WHERE question_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, questionId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
