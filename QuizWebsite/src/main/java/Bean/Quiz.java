package Bean;

import java.sql.Timestamp;

public class Quiz {
    private int quizId;
    private int ownerId;
    private String title;
    private String description;
    private boolean randomOrder;
    private boolean multiplePages;
    private boolean immediateCorrection;
    private Timestamp creationDate;
    private int questionCount;

    public int getQuizId() {
        return quizId;
    }
    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }
    public int getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public boolean isRandomOrder() {
        return randomOrder;
    }
    public void setRandomOrder(boolean randomOrder) {
        this.randomOrder = randomOrder;
    }
    public boolean isMultiplePages() {
        return multiplePages;
    }
    public void setMultiplePages(boolean multiplePages) {
        this.multiplePages = multiplePages;
    }
    public boolean isImmediateCorrection() {
        return immediateCorrection;
    }
    public void setImmediateCorrection(boolean immediateCorrection) {
        this.immediateCorrection = immediateCorrection;
    }
    public Timestamp getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }
    public int getQuestionCount() {
        return questionCount;
    }
    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }
} 