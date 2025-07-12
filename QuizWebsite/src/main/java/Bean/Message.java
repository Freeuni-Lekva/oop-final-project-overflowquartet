package Bean;

import java.sql.Timestamp;

public class Message {
    private Timestamp sentAt;
    private String content;
    private boolean isRead;
    private int messageId;
    private int senderId;
    private String messageType;
    private int receiverId;
    private Integer quizId;

    public String getContent() {
        return content;
    }
    public void  setContent(String content){
        this.content = content;
    }

    public boolean isRead() {
        return isRead;
    }
    public void setRead(boolean read) {
        isRead = read;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
            this.messageId = messageId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }
    public int getSenderId() {
        return senderId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }
    public int getReceiverId() {
        return receiverId;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    public String getMessageType() {
        return messageType;
    }

    public void setSentAt(Timestamp sentAt) {
        this.sentAt = sentAt;
    }
    public Timestamp getSentAt() {
        return sentAt;
    }

    public Integer getQuizId() {
        return quizId;
    }

    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }
}
