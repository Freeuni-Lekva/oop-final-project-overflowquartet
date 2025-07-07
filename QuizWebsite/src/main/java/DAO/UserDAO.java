package DAO;

import Bean.User;
import java.sql.*;

public class UserDAO {
    private Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    public void createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, salt, display_name, email, created_at, last_login, is_admin) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getSalt());
            stmt.setString(4, user.getDisplayName());
            stmt.setString(5, user.getEmail());
            stmt.setTimestamp(6, user.getCreatedAt());
            stmt.setTimestamp(7, user.getLastLogin());
            stmt.setBoolean(8, user.isAdmin());
            stmt.executeUpdate();
        }
    }

    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractUser(rs);
            }
        }
        return null;
    }

    public User findById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractUser(rs);
            }
        }
        return null;
    }

    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET password_hash=?, salt=?, display_name=?, email=?, last_login=?, is_admin=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getPasswordHash());
            stmt.setString(2, user.getSalt());
            stmt.setString(3, user.getDisplayName());
            stmt.setString(4, user.getEmail());
            stmt.setTimestamp(5, user.getLastLogin());
            stmt.setBoolean(6, user.isAdmin());
            stmt.setInt(7, user.getUserId());
            stmt.executeUpdate();
        }
    }

    public void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    private User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setSalt(rs.getString("salt"));
        user.setDisplayName(rs.getString("display_name"));
        user.setEmail(rs.getString("email"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        user.setAdmin(rs.getBoolean("is_admin"));
        return user;
    }
} 