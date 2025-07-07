package Bean;

import java.sql.Timestamp;

public class Message {
    private int messageId;
    private int fromUserId;
    private int toUserId;
    private String type; // "FRIEND_REQUEST", "CHALLENGE", "NOTE"
    private String content;
    private Timestamp sentAt;
    private boolean isRead;

    public Message() {}

    public Message(int messageId, int fromUserId, int toUserId, String type, String content, Timestamp sentAt, boolean isRead) {
        this.messageId = messageId;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.type = type;
        this.content = content;
        this.sentAt = sentAt;
        this.isRead = isRead;
    }

    public int getMessageId() {
        return messageId;
    }
    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
    public int getFromUserId() {
        return fromUserId;
    }
    public void setFromUserId(int fromUserId) {
        this.fromUserId = fromUserId;
    }
    public int getToUserId() {
        return toUserId;
    }
    public void setToUserId(int toUserId) {
        this.toUserId = toUserId;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getSentAt() {
        return sentAt;
    }
    public void setSentAt(Timestamp sentAt) {
        this.sentAt = sentAt;
    }
    public boolean isRead() {
        return isRead;
    }
    public void setRead(boolean read) {
        isRead = read;
    }
} 