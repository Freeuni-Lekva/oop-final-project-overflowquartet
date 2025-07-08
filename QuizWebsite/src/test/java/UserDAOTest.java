
import Bean.User;
import DB.UserDAO;
import DB.DBConnector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
    private static UserDAO userDAO;

    @BeforeEach
    void setup() {
        userDAO = new UserDAO();
        try (Connection conn = DBConnector.getConnection();
             Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM users WHERE username LIKE 'Kate%'");
        } catch (SQLException e) {
            System.err.println("Error in test setup cleanup: " + e.getMessage());
            throw new RuntimeException("Test setup failed", e);
        }
    }

    @Test
    void testRegisterUser_Success() {
        boolean result = userDAO.registerUser("Kate", "april12", "Kate rine");
        assertTrue(result);

        User user = userDAO.getUserByUsername("Kate");
        assertNotNull(user, "Registered user should be found");
        assertEquals("Kate", user.getUsername());
        assertEquals("april12", user.getPasswordHash());
        assertEquals("Kate rine", user.getDisplayName());
    }

    @Test
    void testRegisterUser_DuplicateUsername() {
        userDAO.registerUser("Kate", "april12", "Kate rine");
        boolean result = userDAO.registerUser("Kate", "differenthash", "Another User");
        assertFalse(result);
    }

    @Test
    void testAuthenticateUser_Success() {
        userDAO.registerUser("Kate", "april12", "Kate rine");
        User user = userDAO.authenticateUser("Kate", "april12");
        assertNotNull(user);
        assertEquals("Kate", user.getUsername());
        assertEquals("Kate rine", user.getDisplayName());
    }

    @Test
    void testAuthenticateUser_WrongPassword() {
        userDAO.registerUser("Kate", "april12", "Kate rine");
        User user = userDAO.authenticateUser("Kate", "wrongpassword");
        assertNull(user);
    }

    @Test
    void testAuthenticateUser_NonExistentUser() {
        User user = userDAO.authenticateUser("nonexistent", "april12");
        assertNull(user);
    }

    @Test
    void testGetUserByUsername_Success() {
        userDAO.registerUser("Kate", "april12", "Kate rine");
        User user = userDAO.getUserByUsername("Kate");
        assertNotNull(user);
        assertEquals("Kate", user.getUsername());
    }

    @Test
    void testGetUserByUsername_NonExistent() {
        User user = userDAO.getUserByUsername("nonexistent");
        assertNull(user);
    }

    @Test
    void testGetUserById_Success() {
        userDAO.registerUser("Kate", "april12", "Kate rine");
        User user = userDAO.getUserByUsername("Kate"); // Get user_id
        User found = userDAO.getUserById(user.getUserId());
        assertNotNull(found);
        assertEquals(user.getUserId(), found.getUserId());
    }

    @Test
    void testGetUserById_NonExistent() {
        User user = userDAO.getUserById(999);
        assertNull(user);
    }



    @Test
    void testSearchUsers_Success() {
        userDAO.registerUser("Kate1", "hash1", "Kate rine One");
        userDAO.registerUser("Kate2", "hash2", "Kate rine Two");
        userDAO.registerUser("otheruser", "hash3", "Other User");

        List<User> users = userDAO.searchUsers("Kate");
        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("Kate1")));
        assertTrue(users.stream().anyMatch(u -> u.getUsername().equals("Kate2")));
    }

    @Test
    void testSearchUsers_NoMatch() {
        userDAO.registerUser("Kate1", "hash1", "Kate rine One");
        List<User> users = userDAO.searchUsers("nonexistent");
        assertTrue(users.isEmpty());
    }
}