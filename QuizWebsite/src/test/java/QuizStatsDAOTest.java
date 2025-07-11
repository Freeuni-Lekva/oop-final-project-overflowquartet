import Bean.QuizStats;
import Bean.UserPerformance;
import DB.QuizStatsDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class QuizStatsDAOTest {
    
    private QuizStatsDAO statsDAO;
    private int testQuizId;
    private int testUserId;
    
    @BeforeEach
    void setUp() {
        statsDAO = new QuizStatsDAO();
        
        // Create test data
        try (Connection conn = DB.DBConnector.getConnection()) {
            // Create test user
            String createUserSQL = "INSERT INTO users (username, password_hash, display_name) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(createUserSQL, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "testuser_stats");
                ps.setString(2, "password_hash");
                ps.setString(3, "Test User Stats");
                ps.executeUpdate();
                
                try (java.sql.ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        testUserId = rs.getInt(1);
                    }
                }
            }
            
            // Create test quiz
            String createQuizSQL = "INSERT INTO quizzes (owner_id, title, description) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(createQuizSQL, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, testUserId);
                ps.setString(2, "Test Quiz for Stats");
                ps.setString(3, "A test quiz for statistics");
                ps.executeUpdate();
                
                try (java.sql.ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        testQuizId = rs.getInt(1);
                    }
                }
            }
            
            // Create test questions
            String createQuestionSQL = "INSERT INTO questions (quiz_id, question_type, question_text, question_order) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(createQuestionSQL)) {
                for (int i = 1; i <= 5; i++) {
                    ps.setInt(1, testQuizId);
                    ps.setString(2, "multiple_choice");
                    ps.setString(3, "Test question " + i);
                    ps.setInt(4, i);
                    ps.executeUpdate();
                }
            }
            
            // Create test attempts
            String createAttemptSQL = "INSERT INTO quiz_attempts (user_id, quiz_id, score, duration_seconds) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(createAttemptSQL)) {
                // First attempt: 4/5 correct, 120 seconds
                ps.setInt(1, testUserId);
                ps.setInt(2, testQuizId);
                ps.setInt(3, 4);
                ps.setInt(4, 120);
                ps.executeUpdate();
                
                // Second attempt: 3/5 correct, 90 seconds
                ps.setInt(1, testUserId);
                ps.setInt(2, testQuizId);
                ps.setInt(3, 3);
                ps.setInt(4, 90);
                ps.executeUpdate();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @AfterEach
    void tearDown() {
        // Clean up test data
        try (Connection conn = DB.DBConnector.getConnection()) {
            // Delete attempts
            String deleteAttemptsSQL = "DELETE FROM quiz_attempts WHERE quiz_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteAttemptsSQL)) {
                ps.setInt(1, testQuizId);
                ps.executeUpdate();
            }
            
            // Delete questions
            String deleteQuestionsSQL = "DELETE FROM questions WHERE quiz_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteQuestionsSQL)) {
                ps.setInt(1, testQuizId);
                ps.executeUpdate();
            }
            
            // Delete quiz
            String deleteQuizSQL = "DELETE FROM quizzes WHERE quiz_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteQuizSQL)) {
                ps.setInt(1, testQuizId);
                ps.executeUpdate();
            }
            
            // Delete user
            String deleteUserSQL = "DELETE FROM users WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteUserSQL)) {
                ps.setInt(1, testUserId);
                ps.executeUpdate();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    void testGetQuizStats() {
        QuizStats stats = statsDAO.getQuizStats(testQuizId);
        
        assertNotNull(stats);
        assertEquals(testQuizId, stats.getQuizId());
        assertEquals("Test Quiz for Stats", stats.getQuizTitle());
        assertEquals("A test quiz for statistics", stats.getQuizDescription());
        assertEquals(5, stats.getTotalQuestions());
        assertTrue(stats.getTotalAttempts() >= 2); // At least our 2 attempts, but there might be others
        assertEquals(3.5, stats.getAverageScore(), 0.1); // (4+3)/2 = 3.5
        assertEquals(1.75, stats.getAverageTimeMinutes(), 0.1); // (120+90)/2/60 = 1.75
        assertEquals(4, stats.getHighestScore());
        assertEquals(3, stats.getLowestScore());
    }
    
    @Test
    void testGetUserPerformanceOnQuiz() {
        List<UserPerformance> performances = statsDAO.getUserPerformanceOnQuiz(testUserId, testQuizId, "date");
        
        assertNotNull(performances);
        assertEquals(2, performances.size());
        
        // Check that we have our test performances
        boolean foundScore3 = false;
        boolean foundScore4 = false;
        
        for (UserPerformance perf : performances) {
            if (perf.getScore() == 3 && perf.getDurationSeconds() == 90) {
                foundScore3 = true;
                assertEquals(60.0, perf.getPercentage(), 0.1); // 3/5 * 100
            }
            if (perf.getScore() == 4 && perf.getDurationSeconds() == 120) {
                foundScore4 = true;
                assertEquals(80.0, perf.getPercentage(), 0.1); // 4/5 * 100
            }
        }
        
        assertTrue(foundScore3, "Should find performance with score 3");
        assertTrue(foundScore4, "Should find performance with score 4");
    }
    
    @Test
    void testGetUserPerformanceOnQuizSortByScore() {
        List<UserPerformance> performances = statsDAO.getUserPerformanceOnQuiz(testUserId, testQuizId, "score");
        
        assertNotNull(performances);
        assertEquals(2, performances.size());
        
        // Should be sorted by score descending
        assertEquals(4, performances.get(0).getScore());
        assertEquals(3, performances.get(1).getScore());
    }
    
    @Test
    void testGetUserPerformanceOnQuizSortByTime() {
        List<UserPerformance> performances = statsDAO.getUserPerformanceOnQuiz(testUserId, testQuizId, "time");
        
        assertNotNull(performances);
        assertEquals(2, performances.size());
        
        // Should be sorted by time ascending
        assertEquals(90, performances.get(0).getDurationSeconds());
        assertEquals(120, performances.get(1).getDurationSeconds());
    }
    
    @Test
    void testGetTopPerformersAllTime() {
        List<UserPerformance> performers = statsDAO.getTopPerformersAllTime(testQuizId, 10);
        
        assertNotNull(performers);
        assertEquals(2, performers.size());
        
        // Should be sorted by score descending
        assertTrue(performers.size() >= 2);
        
        // Check that our test performances are included
        boolean foundScore3 = false;
        boolean foundScore4 = false;
        
        for (UserPerformance perf : performers) {
            if (perf.getScore() == 3) foundScore3 = true;
            if (perf.getScore() == 4) foundScore4 = true;
        }
        
        assertTrue(foundScore3, "Should find performance with score 3");
        assertTrue(foundScore4, "Should find performance with score 4");
    }
    
    @Test
    void testGetRecentTestTakers() {
        List<UserPerformance> recent = statsDAO.getRecentTestTakers(testQuizId, 10);
        
        assertNotNull(recent);
        assertEquals(2, recent.size());
        
        // Should be sorted by attempt date descending (most recent first)
        assertTrue(recent.size() >= 2);
        
        // Check that our test performances are included
        boolean foundScore3 = false;
        boolean foundScore4 = false;
        
        for (UserPerformance perf : recent) {
            if (perf.getScore() == 3) foundScore3 = true;
            if (perf.getScore() == 4) foundScore4 = true;
        }
        
        assertTrue(foundScore3, "Should find performance with score 3");
        assertTrue(foundScore4, "Should find performance with score 4");
    }
    
    @Test
    void testGetQuizStatsNoAttempts() {
        // Create a quiz with no attempts
        int emptyQuizId = -1;
        try (Connection conn = DB.DBConnector.getConnection()) {
            String createQuizSQL = "INSERT INTO quizzes (owner_id, title, description) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(createQuizSQL, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, testUserId);
                ps.setString(2, "Empty Quiz");
                ps.setString(3, "A quiz with no attempts");
                ps.executeUpdate();
                
                try (java.sql.ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        emptyQuizId = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        QuizStats stats = statsDAO.getQuizStats(emptyQuizId);
        
        assertNotNull(stats);
        assertEquals(0, stats.getTotalAttempts());
        assertEquals(0, stats.getTotalQuestions());
        
        // Clean up
        try (Connection conn = DB.DBConnector.getConnection()) {
            String deleteQuizSQL = "DELETE FROM quizzes WHERE quiz_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteQuizSQL)) {
                ps.setInt(1, emptyQuizId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 