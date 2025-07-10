package Bean;

/**
 * Java Bean representing an attempt_answers record.
 */
public class AttemptAnswer {
    private int attemptAnswerId;
    private int attemptId;
    private int questionId;
    private String userAnswerText;
    private Boolean isCorrect; // nullable, because DB allows NULL

    public AttemptAnswer() {}

    public AttemptAnswer(int attemptId, int questionId, String userAnswerText, Boolean isCorrect) {
        this.attemptId = attemptId;
        this.questionId = questionId;
        this.userAnswerText = userAnswerText;
        this.isCorrect = isCorrect;
    }

    public int getAttemptAnswerId() {
        return attemptAnswerId;
    }

    public void setAttemptAnswerId(int attemptAnswerId) {
        this.attemptAnswerId = attemptAnswerId;
    }

    public int getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(int attemptId) {
        this.attemptId = attemptId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getUserAnswerText() {
        return userAnswerText;
    }

    public void setUserAnswerText(String userAnswerText) {
        this.userAnswerText = userAnswerText;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    @Override
    public String toString() {
        return "AttemptAnswer{" +
                "attemptAnswerId=" + attemptAnswerId +
                ", attemptId=" + attemptId +
                ", questionId=" + questionId +
                ", userAnswerText='" + userAnswerText + '\'' +
                ", isCorrect=" + isCorrect +
                '}';
    }
}
