package Bean;


public class Achievement {
    private int achievementId;
    private int userId;
    private String name;
    private String description;
    private String iconUrl;

    public Achievement() {}

    public Achievement(int achievementId, int userId, String name, String description, String iconUrl) {
        this.achievementId = achievementId;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.iconUrl = iconUrl;
    }

    public int getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(int achievementId) {
        this.achievementId = achievementId;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getIconUrl() {
        return iconUrl;
    }
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
} 