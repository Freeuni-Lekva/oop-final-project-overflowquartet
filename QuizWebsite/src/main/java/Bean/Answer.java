package Bean;

import java.sql.Timestamp;

public class Answer {
    private int answerId;
    private int questionId;
    private int userId;
    private String responseText;
    private Integer responseOptionId; // nullable for open-ended
    private Timestamp timestamp;

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
    public String getResponseText() {
        return responseText;
    }
    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }
    public Integer getResponseOptionId() {
        return responseOptionId;
    }
    public void setResponseOptionId(Integer responseOptionId) {
        this.responseOptionId = responseOptionId;
    }
    public Timestamp getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}