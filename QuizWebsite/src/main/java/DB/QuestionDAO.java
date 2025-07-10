package DB;
import Bean.Choice;
import Bean.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO  {


    public int addQuestion(Question question) throws SQLException {
        String insertQuestionSQL = "INSERT INTO questions (quiz_id, question_type, question_text, image_url, question_order) VALUES (?, ?, ?, ?, ?);";
        Connection conn = null;
        PreparedStatement questionStmt = null;
        ResultSet generatedKeys = null;
        int questionId = -1;

        try {
            conn = DBConnector.getConnection();
            conn.setAutoCommit(false); // Start transaction

            questionStmt = conn.prepareStatement(insertQuestionSQL, Statement.RETURN_GENERATED_KEYS);
            questionStmt.setInt(1, question.getQuizId());
            questionStmt.setString(2, question.getQuestionType());
            questionStmt.setString(3, question.getQuestionText());
            questionStmt.setString(4, question.getImageUrl());
            questionStmt.setInt(5, question.getQuestionOrder());
            questionStmt.executeUpdate();

            generatedKeys = questionStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                questionId = generatedKeys.getInt(1);
                question.setQuestionId(questionId);
            } else {
                throw new SQLException("Creating question failed, no ID obtained.");
            }

            // Handle different question types
            if ("multiple_choice".equals(question.getQuestionType())) {
                addChoices(conn, question);
            } else { // 'question_response', 'fill_blank', etc.
                addAnswers(conn, question);
            }

            conn.commit(); // Commit transaction
            return questionId;

        } catch (SQLException e) {
            if (conn != null) conn.rollback(); // Rollback on error
            throw e;
        } finally {
            if (generatedKeys != null) generatedKeys.close();
            if (questionStmt != null) questionStmt.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public Question getQuestion(int questionId) throws SQLException {
        String sql = "SELECT * FROM questions WHERE question_id = ?;";
        Question question = null;

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    question = mapRowToQuestion(rs);
                    // Fetch associated answers or choices
                    if ("multiple_choice".equals(question.getQuestionType())) {
                        question.setChoices(getChoicesForQuestion(conn, questionId));
                    } else {
                        question.setAnswers(getAnswersForQuestion(conn, questionId));
                    }
                }
            }
        }
        return question;
    }


    public List<Question> getQuestionsForQuiz(int quizId) throws SQLException {
        String sql = "SELECT * FROM questions WHERE quiz_id = ? ORDER BY question_order;";
        List<Question> questions = new ArrayList<>();

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quizId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Question question = mapRowToQuestion(rs);
                    // Fetch associated answers or choices
                    if ("multiple_choice".equals(question.getQuestionType())) {
                        question.setChoices(getChoicesForQuestion(conn, question.getQuestionId()));
                    } else {
                        question.setAnswers(getAnswersForQuestion(conn, question.getQuestionId()));
                    }
                    questions.add(question);
                }
            }
        }
        return questions;
    }


    public void updateQuestion(Question question) throws SQLException {
        String sql = "UPDATE questions SET question_type = ?, question_text = ?, image_url = ?, question_order = ? WHERE question_id = ?;";
        Connection conn = null;

        try {
            conn = DBConnector.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Update main question table
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, question.getQuestionType());
                stmt.setString(2, question.getQuestionText());
                stmt.setString(3, question.getImageUrl());
                stmt.setInt(4, question.getQuestionOrder());
                stmt.setInt(5, question.getQuestionId());
                stmt.executeUpdate();
            }

            // 2. Delete old answers/choices
            deleteAnswers(conn, question.getQuestionId());
            deleteChoices(conn, question.getQuestionId());

            // 3. Add new answers/choices
            if ("multiple_choice".equals(question.getQuestionType())) {
                addChoices(conn, question);
            } else {
                addAnswers(conn, question);
            }

            conn.commit(); // Commit transaction

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }


    public void deleteQuestion(int questionId) throws SQLException {
        String sql = "DELETE FROM questions WHERE question_id = ?;";
        Connection conn = null;

        try {
            conn = DBConnector.getConnection();
            conn.setAutoCommit(false); // Transaction

            // Must delete from child tables first due to foreign key constraints
            deleteAnswers(conn, questionId);
            deleteChoices(conn, questionId);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, questionId);
                stmt.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    // --- Helper Methods ---

    private Question mapRowToQuestion(ResultSet rs) throws SQLException {
        Question q = new Question();
        q.setQuestionId(rs.getInt("question_id"));
        q.setQuizId(rs.getInt("quiz_id"));
        q.setQuestionType(rs.getString("question_type"));
        q.setQuestionText(rs.getString("question_text"));
        q.setImageUrl(rs.getString("image_url"));
        q.setQuestionOrder(rs.getInt("question_order"));
        return q;
    }

    private void addChoices(Connection conn, Question question) throws SQLException {
        String sql = "INSERT INTO question_choices (question_id, choice_text, is_correct) VALUES (?, ?, ?);";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Choice choice : question.getChoices()) {
                stmt.setInt(1, question.getQuestionId());
                stmt.setString(2, choice.getChoiceText());
                stmt.setBoolean(3, choice.isCorrect());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private void addAnswers(Connection conn, Question question) throws SQLException {
        String sql = "INSERT INTO Youtubes (question_id, answer_text) VALUES (?, ?);";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (String answer : question.getAnswers()) {
                stmt.setInt(1, question.getQuestionId());
                stmt.setString(2, answer);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private List<Choice> getChoicesForQuestion(Connection conn, int questionId) throws SQLException {
        String sql = "SELECT * FROM question_choices WHERE question_id = ?;";
        List<Choice> choices = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Choice choice = new Choice();
                    choice.setChoiceId(rs.getInt("choice_id"));
                    choice.setChoiceText(rs.getString("choice_text"));
                    choice.setCorrect(rs.getBoolean("is_correct"));
                    choices.add(choice);
                }
            }
        }
        return choices;
    }

    private List<String> getAnswersForQuestion(Connection conn, int questionId) throws SQLException {
        String sql = "SELECT answer_text FROM Youtubes WHERE question_id = ?;";
        List<String> answers = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    answers.add(rs.getString("answer_text"));
                }
            }
        }
        return answers;
    }

    private void deleteChoices(Connection conn, int questionId) throws SQLException {
        String sql = "DELETE FROM question_choices WHERE question_id = ?;";
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            stmt.executeUpdate();
        }
    }

    private void deleteAnswers(Connection conn, int questionId) throws SQLException {
        String sql = "DELETE FROM Youtubes WHERE question_id = ?;";
        try(PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            stmt.executeUpdate();
        }
    }
}