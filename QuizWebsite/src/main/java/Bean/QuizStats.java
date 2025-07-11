package Bean;

import java.sql.Timestamp;

public class QuizStats {
    private int quizId;
    private String quizTitle;
    private String quizDescription;
    private int totalAttempts;
    private double averageScore;
    private double averageTimeMinutes;
    private int totalQuestions;
    private Timestamp lastAttemptDate;
    private int highestScore;
    private int lowestScore;
    private double completionRate;
    
    // Constructor
    public QuizStats() {}
    
    // Getters and Setters
    public int getQuizId() {
        return quizId;
    }
    
    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }
    
    public String getQuizTitle() {
        return quizTitle;
    }
    
    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }
    
    public String getQuizDescription() {
        return quizDescription;
    }
    
    public void setQuizDescription(String quizDescription) {
        this.quizDescription = quizDescription;
    }
    
    public int getTotalAttempts() {
        return totalAttempts;
    }
    
    public void setTotalAttempts(int totalAttempts) {
        this.totalAttempts = totalAttempts;
    }
    
    public double getAverageScore() {
        return averageScore;
    }
    
    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }
    
    public double getAverageTimeMinutes() {
        return averageTimeMinutes;
    }
    
    public void setAverageTimeMinutes(double averageTimeMinutes) {
        this.averageTimeMinutes = averageTimeMinutes;
    }
    
    public int getTotalQuestions() {
        return totalQuestions;
    }
    
    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
    
    public Timestamp getLastAttemptDate() {
        return lastAttemptDate;
    }
    
    public void setLastAttemptDate(Timestamp lastAttemptDate) {
        this.lastAttemptDate = lastAttemptDate;
    }
    
    public int getHighestScore() {
        return highestScore;
    }
    
    public void setHighestScore(int highestScore) {
        this.highestScore = highestScore;
    }
    
    public int getLowestScore() {
        return lowestScore;
    }
    
    public void setLowestScore(int lowestScore) {
        this.lowestScore = lowestScore;
    }
    
    public double getCompletionRate() {
        return completionRate;
    }
    
    public void setCompletionRate(double completionRate) {
        this.completionRate = completionRate;
    }
} 