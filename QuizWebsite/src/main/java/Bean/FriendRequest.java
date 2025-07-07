package Bean;

import java.sql.Timestamp;

/**
 * Represents a friend request between users.
 */
public class FriendRequest {
    private int requestId;
    private int fromUserId;
    private int toUserId;
    private String status; // "PENDING", "ACCEPTED", "REJECTED"
    private Timestamp sentAt;

    public FriendRequest() {}

    public FriendRequest(int requestId, int fromUserId, int toUserId, String status, Timestamp sentAt) {
        this.requestId = requestId;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.status = status;
        this.sentAt = sentAt;
    }

    public int getRequestId() {
        return requestId;
    }
    public void setRequestId(int requestId) {
        this.requestId = requestId;
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
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Timestamp getSentAt() {
        return sentAt;
    }
    public void setSentAt(Timestamp sentAt) {
        this.sentAt = sentAt;
    }
} 