

import DB.DBConnector;
import DB.FriendsDAO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FriendsDAOTest {
    private FriendsDAO dao;

    @BeforeAll
    void initSchema() throws SQLException {
        try (Connection conn = DBConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            // Ensure a clean slate
            stmt.execute("DROP TABLE IF EXISTS friends");
            // Create table with same DDL as production
            stmt.execute(
                    "CREATE TABLE friends (" +
                            "  user_id INT NOT NULL," +
                            "  friend_id INT NOT NULL," +
                            "  status ENUM('pending','accepted','rejected') NOT NULL," +
                            "  request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "  PRIMARY KEY (user_id, friend_id)" +
                            ")"
            );
        }
        dao = new FriendsDAO();
    }

    @BeforeEach
    void cleanTable() throws SQLException {
        try (Connection conn = DBConnector.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM friends");
        }
    }

    @Test
    void testSendFriendRequest() throws SQLException {
        assertTrue(dao.sendFriendRequest(1, 2), "should insert new pending");
        List<Integer> pending = dao.getFriendIds(1, FriendsDAO.FriendStatus.PENDING);
        assertEquals(List.of(2), pending, "1→2 should be pending");
    }

    @Test
    void testAcceptFriendRequest() throws SQLException {
        dao.sendFriendRequest(1, 2);
        assertTrue(dao.acceptFriendRequest(2, 1), "should accept existing request");
        var friendsOf1 = dao.getFriendIds(1, FriendsDAO.FriendStatus.ACCEPTED);
        var friendsOf2 = dao.getFriendIds(2, FriendsDAO.FriendStatus.ACCEPTED);
        assertEquals(List.of(2), friendsOf1);
        assertEquals(List.of(1), friendsOf2);
    }

    @Test
    void testRejectFriendRequest() throws SQLException {
        dao.sendFriendRequest(3, 4);
        assertTrue(dao.rejectFriendRequest(4, 3), "should reject existing request");
        var rejected = dao.getFriendIds(3, FriendsDAO.FriendStatus.REJECTED);
        assertEquals(List.of(4), rejected);
    }

    @Test
    void testRemoveFriend() throws SQLException {
        dao.sendFriendRequest(5, 6);
        dao.acceptFriendRequest(6, 5);
        assertTrue(dao.removeFriend(5, 6), "should delete both directions");
        assertTrue(dao.getFriendIds(5, FriendsDAO.FriendStatus.ACCEPTED).isEmpty());
        assertTrue(dao.getFriendIds(6, FriendsDAO.FriendStatus.ACCEPTED).isEmpty());
    }

    @Test
    void testGetPendingRequestsForUser() throws SQLException {
        dao.sendFriendRequest(7, 8);
        dao.sendFriendRequest(9, 8);
        var pending = dao.getPendingRequestsForUser(8);
        assertTrue(pending.containsAll(List.of(7, 9)));
        assertEquals(2, pending.size());
    }

    @Test
    void testReSendAfterRejection() throws SQLException {
        // reject then re-send should flip back to pending
        dao.sendFriendRequest(11, 12);
        assertTrue(dao.rejectFriendRequest(12, 11));
        assertEquals(List.of(12), dao.getFriendIds(11, FriendsDAO.FriendStatus.REJECTED));
        assertTrue(dao.sendFriendRequest(11, 12), "upsert should reset to pending");
        assertEquals(List.of(12), dao.getFriendIds(11, FriendsDAO.FriendStatus.PENDING));
    }

    @Test
    void testCannotFriendYourself() {
        assertThrows(IllegalArgumentException.class, () -> dao.sendFriendRequest(10, 10));
        assertThrows(IllegalArgumentException.class, () -> dao.acceptFriendRequest(10, 10));
        assertThrows(IllegalArgumentException.class, () -> dao.rejectFriendRequest(10, 10));
        assertThrows(IllegalArgumentException.class, () -> dao.removeFriend(10, 10));
    }
}
