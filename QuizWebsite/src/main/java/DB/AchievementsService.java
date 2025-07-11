package DB;

import java.sql.*;
import java.util.List;

/**
 * Service class for managing achievements.
 * Handles checking and awarding achievements based on user actions.
 */
public class AchievementsService {
    
    // Achievement IDs (matching the database)
    public static final int AMATEUR_AUTHOR = 1;
    public static final int PROLIFIC_AUTHOR = 2;
    public static final int PRODIGIOUS_AUTHOR = 3;
    public static final int QUIZ_MACHINE = 4;
    public static final int I_AM_THE_GREATEST = 5;
    
    private final AchievementsDAO achievementsDAO;
    private final QuizDAO quizDAO;
    private final QuizAttemptDAO attemptDAO;
    
    public AchievementsService() {
        this.achievementsDAO = new AchievementsDAO();
        this.quizDAO = new QuizDAO();
        this.attemptDAO = new QuizAttemptDAO();
    }
    
    /**
     * Check and award quiz creation achievements.
     * Called after a user successfully creates a quiz.
     */
    public void checkQuizCreationAchievements(int userId) {
        try {
            // Count quizzes created by this user
            List<Bean.Quiz> userQuizzes = quizDAO.getQuizzesByOwner(userId);
            int quizCount = userQuizzes.size();
            
            // Check if user already has these achievements
            List<Bean.Achievement> existingAchievements = achievementsDAO.getAchievementsByUserId(userId);
            boolean hasAmateur = existingAchievements.stream().anyMatch(a -> a.getId() == AMATEUR_AUTHOR);
            boolean hasProlific = existingAchievements.stream().anyMatch(a -> a.getId() == PROLIFIC_AUTHOR);
            boolean hasProdigious = existingAchievements.stream().anyMatch(a -> a.getId() == PRODIGIOUS_AUTHOR);
            
            // Award achievements based on count
            if (quizCount >= 1 && !hasAmateur) {
                awardAchievement(userId, AMATEUR_AUTHOR);
            }
            if (quizCount >= 5 && !hasProlific) {
                awardAchievement(userId, PROLIFIC_AUTHOR);
            }
            if (quizCount >= 10 && !hasProdigious) {
                awardAchievement(userId, PRODIGIOUS_AUTHOR);
            }
            
        } catch (Exception e) {
            System.err.println("Error checking quiz creation achievements: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Check and award quiz attempt achievements.
     * Called after a user finishes a quiz.
     */
    public void checkQuizAttemptAchievements(int userId) {
        try {
            // Count quiz attempts by this user
            List<Bean.QuizAttempt> userAttempts = attemptDAO.getAttemptsByUser(userId);
            int attemptCount = userAttempts.size();
            
            // Check if user already has this achievement
            List<Bean.Achievement> existingAchievements = achievementsDAO.getAchievementsByUserId(userId);
            boolean hasQuizMachine = existingAchievements.stream().anyMatch(a -> a.getId() == QUIZ_MACHINE);
            
            // Award Quiz Machine achievement
            if (attemptCount >= 10 && !hasQuizMachine) {
                awardAchievement(userId, QUIZ_MACHINE);
            }
            
        } catch (Exception e) {
            System.err.println("Error checking quiz attempt achievements: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Check and award high score achievement.
     * Called after a user finishes a quiz to check if they got the highest score.
     */
    public void checkHighScoreAchievement(int userId, int quizId, int score) {
        try {
            // Check if user already has this achievement for this quiz
            if (hasHighScoreAchievementForQuiz(userId, quizId)) {
                return; // Already earned for this quiz
            }
            
            // Get all attempts for this quiz
            List<Bean.QuizAttempt> quizAttempts = attemptDAO.getAttemptsByQuiz(quizId);
            
            // Find the highest score for this quiz
            int highestScore = quizAttempts.stream()
                    .mapToInt(Bean.QuizAttempt::getScore)
                    .max()
                    .orElse(0);
            
            // If this user's score equals the highest score, award achievement
            if (score == highestScore && score > 0) {
                awardAchievement(userId, I_AM_THE_GREATEST);
                // Mark this achievement as earned for this specific quiz
                markHighScoreAchievementForQuiz(userId, quizId);
            }
            
        } catch (Exception e) {
            System.err.println("Error checking high score achievement: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Award an achievement to a user.
     */
    private void awardAchievement(int userId, int achievementId) {
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO user_achievements (user_id, achievement_id) VALUES (?, ?)")) {
            
            ps.setInt(1, userId);
            ps.setInt(2, achievementId);
            ps.executeUpdate();
            
            System.out.println("Achievement " + achievementId + " awarded to user " + userId);
            
        } catch (SQLException e) {
            System.err.println("Error awarding achievement: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Check if user already has the high score achievement for a specific quiz.
     * We'll use a simple approach: store the quiz ID in a separate table or use a different method.
     * For now, we'll just check if they have the achievement at all.
     */
    private boolean hasHighScoreAchievementForQuiz(int userId, int quizId) {
        // For simplicity, we'll just check if they have the achievement at all
        // In a more complex system, you might want to track per-quiz achievements
        try {
            List<Bean.Achievement> achievements = achievementsDAO.getAchievementsByUserId(userId);
            return achievements.stream().anyMatch(a -> a.getId() == I_AM_THE_GREATEST);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Mark that the user has earned the high score achievement for this quiz.
     * For now, we'll just award the achievement (it's already handled above).
     */
    private void markHighScoreAchievementForQuiz(int userId, int quizId) {
        // The achievement is already awarded in checkHighScoreAchievement
        // This method could be used for more complex tracking if needed
    }
} 