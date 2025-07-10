package Bean;

import java.sql.Timestamp;

/**
 * Java Bean representing a quiz_attempt record.
 */
public class QuizAttempt {
    private int attemptId;
    private int userId;
    private int quizId;
    private int score;
    private int durationSeconds;
    private Timestamp attemptDate;

    /**
     * Default constructor.
     */
    public QuizAttempt() {
    }

    /**
     * Constructor for creating a new attempt (attemptId and attemptDate populated by DB).
     */
    public QuizAttempt(int userId, int quizId, int score, int durationSeconds) {
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
        this.durationSeconds = durationSeconds;
    }

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

    @Override
    public String toString() {
        return "QuizAttempt{" +
                "attemptId=" + attemptId +
                ", userId=" + userId +
                ", quizId=" + quizId +
                ", score=" + score +
                ", durationSeconds=" + durationSeconds +
                ", attemptDate=" + attemptDate +
                '}';
    }
}
