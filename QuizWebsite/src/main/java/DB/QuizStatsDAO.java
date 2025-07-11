package DB;

import Bean.QuizStats;
import Bean.UserPerformance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizStatsDAO {
    
    /**
     * Get comprehensive statistics for a specific quiz
     */
    public QuizStats getQuizStats(int quizId) {
        String sql = "SELECT " +
                    "q.quiz_id, q.title, q.description, " +
                    "COUNT(DISTINCT ques.question_id) as total_questions, " +
                    "COUNT(qa.attempt_id) as total_attempts, " +
                    "AVG(qa.score) as avg_score, " +
                    "AVG(qa.duration_seconds) as avg_duration, " +
                    "MAX(qa.score) as highest_score, " +
                    "MIN(qa.score) as lowest_score, " +
                    "MAX(qa.attempt_date) as last_attempt " +
                    "FROM quizzes q " +
                    "LEFT JOIN questions ques ON q.quiz_id = ques.quiz_id " +
                    "LEFT JOIN quiz_attempts qa ON q.quiz_id = qa.quiz_id " +
                    "WHERE q.quiz_id = ? " +
                    "GROUP BY q.quiz_id, q.title, q.description";
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    QuizStats stats = new QuizStats();
                    stats.setQuizId(rs.getInt("quiz_id"));
                    stats.setQuizTitle(rs.getString("title"));
                    stats.setQuizDescription(rs.getString("description"));
                    stats.setTotalQuestions(rs.getInt("total_questions"));
                    stats.setTotalAttempts(rs.getInt("total_attempts"));
                    
                    double avgScore = rs.getDouble("avg_score");
                    if (!rs.wasNull()) {
                        stats.setAverageScore(avgScore);
                    }
                    
                    double avgDuration = rs.getDouble("avg_duration");
                    if (!rs.wasNull()) {
                        stats.setAverageTimeMinutes(avgDuration / 60.0);
                    }
                    
                    int highestScore = rs.getInt("highest_score");
                    if (!rs.wasNull()) {
                        stats.setHighestScore(highestScore);
                    }
                    
                    int lowestScore = rs.getInt("lowest_score");
                    if (!rs.wasNull()) {
                        stats.setLowestScore(lowestScore);
                    }
                    
                    Timestamp lastAttempt = rs.getTimestamp("last_attempt");
                    if (!rs.wasNull()) {
                        stats.setLastAttemptDate(lastAttempt);
                    }
                    
                    return stats;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get user's past performance on a specific quiz
     */
    public List<UserPerformance> getUserPerformanceOnQuiz(int userId, int quizId, String sortBy) {
        String orderBy;
        switch (sortBy) {
            case "date":
                orderBy = "qa.attempt_date DESC";
                break;
            case "score":
                orderBy = "qa.score DESC";
                break;
            case "time":
                orderBy = "qa.duration_seconds ASC";
                break;
            default:
                orderBy = "qa.attempt_date DESC";
        }
        
        String sql = "SELECT " +
                    "qa.attempt_id, qa.user_id, qa.score, qa.duration_seconds, qa.attempt_date, " +
                    "u.username, u.display_name, " +
                    "COUNT(DISTINCT ques.question_id) as total_questions " +
                    "FROM quiz_attempts qa " +
                    "JOIN users u ON qa.user_id = u.user_id " +
                    "JOIN questions ques ON qa.quiz_id = ques.quiz_id " +
                    "WHERE qa.user_id = ? AND qa.quiz_id = ? " +
                    "GROUP BY qa.attempt_id, qa.user_id, qa.score, qa.duration_seconds, qa.attempt_date, u.username, u.display_name " +
                    "ORDER BY " + orderBy;
        
        List<UserPerformance> performances = new ArrayList<>();
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserPerformance perf = mapRowToUserPerformance(rs);
                    performances.add(perf);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return performances;
    }
    
    /**
     * Get highest performers of all time for a quiz
     */
    public List<UserPerformance> getTopPerformersAllTime(int quizId, int limit) {
        String sql = "SELECT " +
                    "qa.attempt_id, qa.user_id, qa.score, qa.duration_seconds, qa.attempt_date, " +
                    "u.username, u.display_name, " +
                    "COUNT(DISTINCT ques.question_id) as total_questions, " +
                    "RANK() OVER (ORDER BY qa.score DESC, qa.duration_seconds ASC) as `rank` " +
                    "FROM quiz_attempts qa " +
                    "JOIN users u ON qa.user_id = u.user_id " +
                    "JOIN questions ques ON qa.quiz_id = ques.quiz_id " +
                    "WHERE qa.quiz_id = ? " +
                    "GROUP BY qa.attempt_id, qa.user_id, qa.score, qa.duration_seconds, qa.attempt_date, u.username, u.display_name " +
                    "ORDER BY qa.score DESC, qa.duration_seconds ASC " +
                    "LIMIT ?";
        
        List<UserPerformance> performers = new ArrayList<>();
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, quizId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserPerformance perf = mapRowToUserPerformance(rs);
                    performers.add(perf);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return performers;
    }
    
    /**
     * Get top performers in the last day
     */
    public List<UserPerformance> getTopPerformersLastDay(int quizId, int limit) {
        String sql = "SELECT " +
                    "qa.attempt_id, qa.user_id, qa.score, qa.duration_seconds, qa.attempt_date, " +
                    "u.username, u.display_name, " +
                    "COUNT(DISTINCT ques.question_id) as total_questions, " +
                    "RANK() OVER (ORDER BY qa.score DESC, qa.duration_seconds ASC) as `rank` " +
                    "FROM quiz_attempts qa " +
                    "JOIN users u ON qa.user_id = u.user_id " +
                    "JOIN questions ques ON qa.quiz_id = ques.quiz_id " +
                    "WHERE qa.quiz_id = ? AND qa.attempt_date >= DATE_SUB(NOW(), INTERVAL 1 DAY) " +
                    "GROUP BY qa.attempt_id, qa.user_id, qa.score, qa.duration_seconds, qa.attempt_date, u.username, u.display_name " +
                    "ORDER BY qa.score DESC, qa.duration_seconds ASC " +
                    "LIMIT ?";
        
        List<UserPerformance> performers = new ArrayList<>();
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, quizId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserPerformance perf = mapRowToUserPerformance(rs);
                    performers.add(perf);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return performers;
    }
    
    /**
     * Get recent test takers (both good and bad performance)
     */
    public List<UserPerformance> getRecentTestTakers(int quizId, int limit) {
        String sql = "SELECT " +
                    "qa.attempt_id, qa.user_id, qa.score, qa.duration_seconds, qa.attempt_date, " +
                    "u.username, u.display_name, " +
                    "COUNT(DISTINCT ques.question_id) as total_questions " +
                    "FROM quiz_attempts qa " +
                    "JOIN users u ON qa.user_id = u.user_id " +
                    "JOIN questions ques ON qa.quiz_id = ques.quiz_id " +
                    "WHERE qa.quiz_id = ? " +
                    "GROUP BY qa.attempt_id, qa.user_id, qa.score, qa.duration_seconds, qa.attempt_date, u.username, u.display_name " +
                    "ORDER BY qa.attempt_date DESC " +
                    "LIMIT ?";
        
        List<UserPerformance> performers = new ArrayList<>();
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, quizId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UserPerformance perf = mapRowToUserPerformance(rs);
                    performers.add(perf);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return performers;
    }
    
    /**
     * Helper method to map ResultSet to UserPerformance
     */
    private UserPerformance mapRowToUserPerformance(ResultSet rs) throws SQLException {
        UserPerformance perf = new UserPerformance();
        perf.setAttemptId(rs.getInt("attempt_id"));
        perf.setUserId(rs.getInt("user_id"));
        perf.setScore(rs.getInt("score"));
        perf.setDurationSeconds(rs.getInt("duration_seconds"));
        perf.setAttemptDate(rs.getTimestamp("attempt_date"));
        perf.setUsername(rs.getString("username"));
        perf.setDisplayName(rs.getString("display_name"));
        
        int totalQuestions = rs.getInt("total_questions");
        perf.setTotalQuestions(totalQuestions);
        
        if (totalQuestions > 0) {
            double percentage = (double) perf.getScore() / totalQuestions * 100;
            perf.setPercentage(percentage);
        }
        
        try {
            perf.setRank(rs.getInt("`rank`"));
        } catch (SQLException e) {
            // rank column might not exist in some queries
        }
        
        return perf;
    }
} 