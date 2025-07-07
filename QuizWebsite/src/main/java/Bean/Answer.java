package Bean;

//users answer
public class Answer {
    private int answerId;
    private int questionId;
    private int userId;
    private String answerText;
    private boolean isCorrect;

    public Answer() {}

    public Answer(int answerId, int questionId, int userId, String answerText, boolean isCorrect) {
        this.answerId = answerId;
        this.questionId = questionId;
        this.userId = userId;
        this.answerText = answerText;
        this.isCorrect = isCorrect;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public int getQuestionId() {
        return questionId;
    }
    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getAnswerText() {
        return answerText;
    }
    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }
    public boolean isCorrect() {
        return isCorrect;
    }
    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
} 