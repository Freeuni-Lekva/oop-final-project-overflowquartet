import Bean.Achievement;
import DB.AchievementsDAO;
import DB.DBConnector;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AchievementDAOTest {

    private AchievementsDAO achievementDAO;
    private int testUserId;
    private int testAchievementId;

    @BeforeEach
    void setup() throws SQLException {

        achievementDAO = new AchievementsDAO();

        try (Connection conn = DBConnector.getConnection()) {
            Statement stmt = conn.createStatement();

            // Clean up from previous runs if needed
            stmt.executeUpdate("DELETE FROM user_achievements WHERE user_id IN (SELECT user_id FROM users WHERE username = 'test_user_achievement')");
            stmt.executeUpdate("DELETE FROM users WHERE username = 'test_user_achievement'");
            stmt.executeUpdate("DELETE FROM achievements WHERE name = 'Test Achievement'");


            PreparedStatement insertUser = conn.prepareStatement(
                    "INSERT INTO users (username, password_hash) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            insertUser.setString(1, "test_user_achievement");
            insertUser.setString(2, "test_hash");
            insertUser.executeUpdate();

            ResultSet rs = insertUser.getGeneratedKeys();
            if (rs.next()) testUserId = rs.getInt(1);


            PreparedStatement insertAch = conn.prepareStatement(
                    "INSERT INTO achievements (name, description, icon_url) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            insertAch.setString(1, "Test Achievement");
            insertAch.setString(2, "Earned for testing.");
            insertAch.setString(3, "http://example.com/icon.png");
            insertAch.executeUpdate();

            rs = insertAch.getGeneratedKeys();
            if (rs.next()) testAchievementId = rs.getInt(1);


            PreparedStatement earnAchievement = conn.prepareStatement(
                    "INSERT INTO user_achievements (user_id, achievement_id) VALUES (?, ?)"
            );
            earnAchievement.setInt(1, testUserId);
            earnAchievement.setInt(2, testAchievementId);
            earnAchievement.executeUpdate();
        }
    }

    @Test
    void testGetAchievementsByUserId() {
        List<Achievement> achievements = achievementDAO.getAchievementsByUserId(testUserId);

        assertNotNull(achievements, "Achievements list should not be null");
        assertFalse(achievements.isEmpty(), "User should have at least one achievement");

        Achievement a = achievements.get(0);
        assertEquals(testAchievementId, a.getId());
        assertEquals("Test Achievement", a.getName());
        assertEquals("Earned for testing.", a.getDescription());
        assertEquals("http://example.com/icon.png", a.getIconUrl());
    }

    @AfterAll
    void cleanup() throws SQLException {
        try (Connection conn = DBConnector.getConnection()) {
            conn.createStatement().executeUpdate("DELETE FROM user_achievements WHERE user_id = " + testUserId);
            conn.createStatement().executeUpdate("DELETE FROM achievements WHERE achievement_id = " + testAchievementId);
            conn.createStatement().executeUpdate("DELETE FROM users WHERE user_id = " + testUserId);
        }
    }
}
