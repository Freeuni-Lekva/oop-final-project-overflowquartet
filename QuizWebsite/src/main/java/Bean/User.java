package Bean;

import java.sql.Timestamp;

public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String displayName;
    private Timestamp createdAt;
    private Timestamp lastLogin;
    private boolean isAdmin;
    private String salt;
    private String email; // optional, for future features

    /**
     * Default constructor
     */
    public User() {}

    /**
     * Parameterized constructor
     */
    public User(int userId, String username, String passwordHash, String salt, String displayName, String email, Timestamp createdAt, Timestamp lastLogin, boolean isAdmin) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.displayName = displayName;
        this.email = email;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
        this.isAdmin = isAdmin;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPasswordHash() {
        return passwordHash;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    public Timestamp getLastLogin() {
        return lastLogin;
    }
    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }
    public boolean isAdmin() {
        return isAdmin;
    }
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    /**
     * Get the salt for password hashing
     */
    public String getSalt() {
        return salt;
    }
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     * Get the user's email
     */
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}