package DB;

import Bean.Announcement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDAO {
    
    public boolean createAnnouncement(Announcement announcement) {
        String sql = "INSERT INTO announcements (title, content, created_by) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, announcement.getTitle());
            stmt.setString(2, announcement.getContent());
            stmt.setInt(3, announcement.getCreatedBy());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Announcement> getAllActiveAnnouncements() {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT * FROM announcements WHERE is_active = TRUE ORDER BY created_at DESC";
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Announcement announcement = new Announcement();
                announcement.setAnnouncementId(rs.getInt("announcement_id"));
                announcement.setTitle(rs.getString("title"));
                announcement.setContent(rs.getString("content"));
                announcement.setCreatedBy(rs.getInt("created_by"));
                announcement.setCreatedAt(rs.getTimestamp("created_at"));
                announcement.setActive(rs.getBoolean("is_active"));
                announcements.add(announcement);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return announcements;
    }
    
    public List<Announcement> getAllAnnouncements() {
        List<Announcement> announcements = new ArrayList<>();
        String sql = "SELECT * FROM announcements ORDER BY created_at DESC";
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Announcement announcement = new Announcement();
                announcement.setAnnouncementId(rs.getInt("announcement_id"));
                announcement.setTitle(rs.getString("title"));
                announcement.setContent(rs.getString("content"));
                announcement.setCreatedBy(rs.getInt("created_by"));
                announcement.setCreatedAt(rs.getTimestamp("created_at"));
                announcement.setActive(rs.getBoolean("is_active"));
                announcements.add(announcement);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return announcements;
    }
    
    public boolean deleteAnnouncement(int announcementId) {
        String sql = "DELETE FROM announcements WHERE announcement_id = ?";
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, announcementId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean toggleAnnouncementStatus(int announcementId) {
        String sql = "UPDATE announcements SET is_active = NOT is_active WHERE announcement_id = ?";
        
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, announcementId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 