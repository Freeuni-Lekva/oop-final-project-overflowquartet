package Bean;

import java.sql.Timestamp;

public class UserPerformance {
    private int attemptId;
    private int userId;
    private String username;
    private String displayName;
    private int score;
    private int totalQuestions;
    private double percentage;
    private int durationSeconds;
    private Timestamp attemptDate;
    private int rank;
    
    // Constructor
    public UserPerformance() {}
    
    // Getters and Setters
    public int getAttemptId() {
        return attemptId;
    }
    
    public void setAttemptId(int attemptId) {
        this.attemptId = attemptId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public int getTotalQuestions() {
        return totalQuestions;
    }
    
    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
    
    public double getPercentage() {
        return percentage;
    }
    
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
    
    public int getDurationSeconds() {
        return durationSeconds;
    }
    
    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
    
    public Timestamp getAttemptDate() {
        return attemptDate;
    }
    
    public void setAttemptDate(Timestamp attemptDate) {
        this.attemptDate = attemptDate;
    }
    
    public int getRank() {
        return rank;
    }
    
    public void setRank(int rank) {
        this.rank = rank;
    }
    
    // Helper methods
    public String getFormattedDuration() {
        int minutes = durationSeconds / 60;
        int seconds = durationSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    
    public String getFormattedPercentage() {
        return String.format("%.1f%%", percentage);
    }
} 