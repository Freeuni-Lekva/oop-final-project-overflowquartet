import Bean.Announcement;
import Bean.User;
import DB.AnnouncementDAO;
import DB.UserDAO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AdminSystemTest {
    
    @Test
    public void testUserAdminField() {
        User user = new User();
        user.setAdmin(true);
        assertTrue(user.isAdmin());
        
        user.setAdmin(false);
        assertFalse(user.isAdmin());
    }
    
    @Test
    public void testAnnouncementCreation() {
        Announcement announcement = new Announcement("Test Title", "Test Content", 1);
        assertEquals("Test Title", announcement.getTitle());
        assertEquals("Test Content", announcement.getContent());
        assertEquals(1, announcement.getCreatedBy());
        assertTrue(announcement.isActive()); // Should be active by default
    }
    
    @Test
    public void testAnnouncementToggle() {
        Announcement announcement = new Announcement("Test", "Content", 1);
        announcement.setActive(false);
        assertFalse(announcement.isActive());
        
        announcement.setActive(true);
        assertTrue(announcement.isActive());
    }
} 