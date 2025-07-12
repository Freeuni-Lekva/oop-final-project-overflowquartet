package Bean;

import java.sql.Timestamp;

public class Announcement {
    private int announcementId;
    private String title;
    private String content;
    private int createdBy;
    private Timestamp createdAt;
    private boolean isActive;

    public Announcement() {}

    public Announcement(String title, String content, int createdBy) {
        this.title = title;
        this.content = content;
        this.createdBy = createdBy;
        this.isActive = true; // Default to active
    }

    public int getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(int announcementId) {
        this.announcementId = announcementId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
} 