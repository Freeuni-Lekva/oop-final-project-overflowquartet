package DB;

import Bean.Achievement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AchievementsDAO {

    public List<Achievement> getAchievementsByUserId(int userId) {
        List<Achievement> achievements = new ArrayList<>();

        String sql = """
            SELECT a.achievement_id, a.name, a.description, a.icon_url
            FROM achievements a
            JOIN user_achievements ua ON a.achievement_id = ua.achievement_id
            WHERE ua.user_id = ?
        """;

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Achievement achievement = new Achievement();
                achievement.setId(rs.getInt("achievement_id"));
                achievement.setName(rs.getString("name"));
                achievement.setDescription(rs.getString("description"));
                achievement.setIconUrl(rs.getString("icon_url"));
                achievements.add(achievement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return achievements;
    }
}
