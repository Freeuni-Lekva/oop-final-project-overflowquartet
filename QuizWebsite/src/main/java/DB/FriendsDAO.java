package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO for all operations on the <code>friends</code> table.
 *
 * Table schema (MySQL):
 * <pre>
 * CREATE TABLE friends (
 *     user_id   INT  NOT NULL,
 *     friend_id INT  NOT NULL,
 *     status    ENUM('pending','accepted','rejected') NOT NULL,
 *     request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
 *     PRIMARY KEY (user_id, friend_id)
 * );
 * </pre>
 *
 * Status semantics:
 * <ul>
 *   <li><b>pending</b> – <code>user_id</code> sent a request to <code>friend_id</code>.</li>
 *   <li><b>accepted</b> – friendship established; DAO inserts reciprocal row.</li>
 *   <li><b>rejected</b> – request denied.</li>
 * </ul>
 */
public class FriendsDAO {
    private static final Logger LOGGER = Logger.getLogger(FriendsDAO.class.getName());

    // === SQL strings ===================================================== //
    private static final String SEND_REQUEST_SQL =
            "INSERT INTO friends (user_id, friend_id, status, request_date) " +
                    "VALUES (?, ?, 'pending', CURRENT_TIMESTAMP) " +
                    "ON DUPLICATE KEY UPDATE status='pending', request_date=CURRENT_TIMESTAMP";

    private static final String ACCEPT_REQUEST_SQL =
            "UPDATE friends SET status='accepted' " +
                    "WHERE user_id=? AND friend_id=? AND status='pending'";

    private static final String INSERT_ACCEPTED_ROW_SQL =
            "INSERT INTO friends (user_id, friend_id, status, request_date) " +
                    "VALUES (?, ?, 'accepted', CURRENT_TIMESTAMP) " +
                    "ON DUPLICATE KEY UPDATE status='accepted'";

    private static final String REJECT_REQUEST_SQL =
            "UPDATE friends SET status='rejected' " +
                    "WHERE user_id=? AND friend_id=? AND status='pending'";

    private static final String REMOVE_FRIEND_SQL =
            "DELETE FROM friends " +
                    "WHERE (user_id=? AND friend_id=?) OR (user_id=? AND friend_id=?)";

    private static final String FRIEND_IDS_BY_STATUS_SQL =
            "SELECT friend_id FROM friends WHERE user_id=? AND status=?";

    private static final String PENDING_REQUESTS_FOR_USER_SQL =
            "SELECT user_id FROM friends WHERE friend_id=? AND status='pending'";

    /**
     * Send a friend request. Returns true if inserted or updated to pending.
     */
    public boolean sendFriendRequest(int fromUserId, int toUserId) {
        validateDifferentUsers(fromUserId, toUserId);
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(SEND_REQUEST_SQL)) {
            ps.setInt(1, fromUserId);
            ps.setInt(2, toUserId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error sending friend request from {0} to {1}", new Object[]{fromUserId, toUserId});
            throw new FriendshipException("Unable to send friend request", e);
        }
    }

    /**
     * Accept a pending request. Returns true if accepted.
     */
    public boolean acceptFriendRequest(int toUserId, int fromUserId) {
        validateDifferentUsers(fromUserId, toUserId);
        try (Connection conn = DBConnector.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement accept = conn.prepareStatement(ACCEPT_REQUEST_SQL)) {
                accept.setInt(1, fromUserId);
                accept.setInt(2, toUserId);
                int updated = accept.executeUpdate();
                if (updated == 0) {
                    conn.rollback();
                    return false;
                }
            }
            try (PreparedStatement insert = conn.prepareStatement(INSERT_ACCEPTED_ROW_SQL)) {
                insert.setInt(1, toUserId);
                insert.setInt(2, fromUserId);
                insert.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error accepting friend request from {0} to {1}", new Object[]{fromUserId, toUserId});
            throw new FriendshipException("Unable to accept friend request", e);
        }
    }

    /**
     * Reject a pending friend request. Returns true if status set to rejected.
     */
    public boolean rejectFriendRequest(int toUserId, int fromUserId) {
        validateDifferentUsers(fromUserId, toUserId);
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(REJECT_REQUEST_SQL)) {
            ps.setInt(1, fromUserId);
            ps.setInt(2, toUserId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error rejecting friend request from {0} to {1}", new Object[]{fromUserId, toUserId});
            throw new FriendshipException("Unable to reject friend request", e);
        }
    }

    /**
     * Remove an existing friendship in both directions. Returns true if any rows deleted.
     */
    public boolean removeFriend(int userId, int friendId) {
        validateDifferentUsers(userId, friendId);
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(REMOVE_FRIEND_SQL)) {
            ps.setInt(1, userId);
            ps.setInt(2, friendId);
            ps.setInt(3, friendId);
            ps.setInt(4, userId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error removing friendship between {0} and {1}", new Object[]{userId, friendId});
            throw new FriendshipException("Unable to remove friend", e);
        }
    }

    /**
     * Return list of friend IDs by status.
     */
    public List<Integer> getFriendIds(int userId, FriendStatus status) {
        List<Integer> ids = new ArrayList<>();
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(FRIEND_IDS_BY_STATUS_SQL)) {
            ps.setInt(1, userId);
            ps.setString(2, status.name().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching {0} friends for user {1}", new Object[]{status, userId});
            throw new FriendshipException("Unable to fetch friend IDs", e);
        }
        return ids;
    }

    /**
     * Return list of user IDs who have sent a request to userId that is still pending.
     */
    public List<Integer> getPendingRequestsForUser(int userId) {
        List<Integer> ids = new ArrayList<>();
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(PENDING_REQUESTS_FOR_USER_SQL)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching pending requests for user {0}", userId);
            throw new FriendshipException("Unable to fetch pending requests", e);
        }
        return ids;
    }

    private void validateDifferentUsers(int userA, int userB) {
        if (userA == userB) {
            throw new IllegalArgumentException("User IDs must be different");
        }
    }



    /**
     * Possible statuses in the friends table.
     */
    public enum FriendStatus {
        PENDING, ACCEPTED, REJECTED
    }

    /**
     * Unchecked exception for DAO errors.
     */
    public static class FriendshipException extends RuntimeException {
        public FriendshipException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

