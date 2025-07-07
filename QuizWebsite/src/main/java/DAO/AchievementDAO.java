package DAO;

import Bean.Achievement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AchievementDAO {
    private Connection conn;

    public AchievementDAO(Connection conn) {
        this.conn = conn;
    }

    public void createAchievement(Achievement achievement) throws SQLException {
        String sql = "INSERT INTO achievements (user_id, name, description, icon_url) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, achievement.getUserId());
            stmt.setString(2, achievement.getName());
            stmt.setString(3, achievement.getDescription());
            stmt.setString(4, achievement.getIconUrl());
            stmt.executeUpdate();
        }
    }

    public List<Achievement> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM achievements WHERE user_id = ?";
        List<Achievement> achievements = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                achievements.add(extractAchievement(rs));
            }
        }
        return achievements;
    }

    public void deleteAchievement(int achievementId) throws SQLException {
        String sql = "DELETE FROM achievements WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, achievementId);
            stmt.executeUpdate();
        }
    }

    private Achievement extractAchievement(ResultSet rs) throws SQLException {
        Achievement achievement = new Achievement();
        achievement.setAchievementId(rs.getInt("id"));
        achievement.setUserId(rs.getInt("user_id"));
        achievement.setName(rs.getString("name"));
        achievement.setDescription(rs.getString("description"));
        achievement.setIconUrl(rs.getString("icon_url"));
        return achievement;
    }
} 