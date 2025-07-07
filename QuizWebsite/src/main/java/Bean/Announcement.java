package Bean;

import java.sql.Timestamp;

//admin's announcement
public class Announcement {
    private int announcementId;
    private String content;
    private Timestamp postedAt;

    public Announcement() {}

    public Announcement(int announcementId, String content, Timestamp postedAt) {
        this.announcementId = announcementId;
        this.content = content;
        this.postedAt = postedAt;
    }

    public int getAnnouncementId() {
        return announcementId;
    }
    public void setAnnouncementId(int announcementId) {
        this.announcementId = announcementId;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Timestamp getPostedAt() {
        return postedAt;
    }
    public void setPostedAt(Timestamp postedAt) {
        this.postedAt = postedAt;
    }
} 