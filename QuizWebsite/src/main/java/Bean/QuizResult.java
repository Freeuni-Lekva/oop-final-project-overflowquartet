package Bean;

import java.sql.Timestamp;

/**
 * Represents a user's attempt at a quiz, including score and time taken.
 */
public class QuizResult {
    private int resultId;
    private int userId;
    private int quizId;
    private int score;
    private int totalQuestions;
    private int timeTaken; // in seconds
    private Timestamp takenAt;

    public QuizResult() {}

    public QuizResult(int resultId, int userId, int quizId, int score, int totalQuestions, int timeTaken, Timestamp takenAt) {
        this.resultId = resultId;
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.timeTaken = timeTaken;
        this.takenAt = takenAt;
    }

    public int getResultId() {
        return resultId;
    }
    public void setResultId(int resultId) {
        this.resultId = resultId;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getQuizId() {
        return quizId;
    }
    public void setQuizId(int quizId) {
        this.quizId = quizId;
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
    public int getTimeTaken() {
        return timeTaken;
    }
    public void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }
    public Timestamp getTakenAt() {
        return takenAt;
    }
    public void setTakenAt(Timestamp takenAt) {
        this.takenAt = takenAt;
    }
} 