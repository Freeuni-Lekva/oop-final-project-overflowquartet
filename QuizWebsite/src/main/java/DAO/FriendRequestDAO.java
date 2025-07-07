package DAO;

import Bean.FriendRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendRequestDAO {
    private Connection conn;

    public FriendRequestDAO(Connection conn) {
        this.conn = conn;
    }

    public void createFriendRequest(FriendRequest request) throws SQLException {
        String sql = "INSERT INTO friend_requests (from_user_id, to_user_id, status, sent_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, request.getFromUserId());
            stmt.setInt(2, request.getToUserId());
            stmt.setString(3, request.getStatus());
            stmt.setTimestamp(4, request.getSentAt());
            stmt.executeUpdate();
        }
    }

    public List<FriendRequest> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM friend_requests WHERE from_user_id = ? OR to_user_id = ?";
        List<FriendRequest> requests = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(extractFriendRequest(rs));
            }
        }
        return requests;
    }

    public void updateFriendRequest(FriendRequest request) throws SQLException {
        String sql = "UPDATE friend_requests SET status=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, request.getStatus());
            stmt.setInt(2, request.getRequestId());
            stmt.executeUpdate();
        }
    }

    public void deleteFriendRequest(int requestId) throws SQLException {
        String sql = "DELETE FROM friend_requests WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, requestId);
            stmt.executeUpdate();
        }
    }

    private FriendRequest extractFriendRequest(ResultSet rs) throws SQLException {
        FriendRequest request = new FriendRequest();
        request.setRequestId(rs.getInt("id"));
        request.setFromUserId(rs.getInt("from_user_id"));
        request.setToUserId(rs.getInt("to_user_id"));
        request.setStatus(rs.getString("status"));
        request.setSentAt(rs.getTimestamp("sent_at"));
        return request;
    }
} 