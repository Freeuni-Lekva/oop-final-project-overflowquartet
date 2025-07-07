package DAO;

import Bean.Announcement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDAO {
    private Connection conn;

    public AnnouncementDAO(Connection conn) {
        this.conn = conn;
    }

    public void createAnnouncement(Announcement announcement) throws SQLException {
        String sql = "INSERT INTO announcements (content, posted_at) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, announcement.getContent());
            stmt.setTimestamp(2, announcement.getPostedAt());
            stmt.executeUpdate();
        }
    }

    public List<Announcement> findAll() throws SQLException {
        String sql = "SELECT * FROM announcements ORDER BY posted_at DESC";
        List<Announcement> announcements = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                announcements.add(extractAnnouncement(rs));
            }
        }
        return announcements;
    }

    public void deleteAnnouncement(int announcementId) throws SQLException {
        String sql = "DELETE FROM announcements WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, announcementId);
            stmt.executeUpdate();
        }
    }

    private Announcement extractAnnouncement(ResultSet rs) throws SQLException {
        Announcement announcement = new Announcement();
        announcement.setAnnouncementId(rs.getInt("id"));
        announcement.setContent(rs.getString("content"));
        announcement.setPostedAt(rs.getTimestamp("posted_at"));
        return announcement;
    }
} 