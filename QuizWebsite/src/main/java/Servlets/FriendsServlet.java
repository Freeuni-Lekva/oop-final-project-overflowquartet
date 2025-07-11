package Servlets;

import Bean.User;
import DB.FriendsDAO;
import DB.UserDAO; // Assume you have one for looking up User by id/username

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/friends")
public class FriendsServlet extends HttpServlet {

    private FriendsDAO friendsDAO = new FriendsDAO();
    private UserDAO userDAO = new UserDAO(); // You'll need to implement this if not present

    // Helper class to hold user info with friendship status
    public static class UserWithFriendshipStatus {
        private User user;
        private String friendshipStatus; // "friend", "pending_sent", "pending_received", "none"
        
        public UserWithFriendshipStatus(User user, String status) {
            this.user = user;
            this.friendshipStatus = status;
        }
        
        public User getUser() { return user; }
        public String getFriendshipStatus() { return friendshipStatus; }
        public boolean isFriend() { return "friend".equals(friendshipStatus); }
        public boolean isPendingSent() { return "pending_sent".equals(friendshipStatus); }
        public boolean isPendingReceived() { return "pending_received".equals(friendshipStatus); }
        public boolean isNone() { return "none".equals(friendshipStatus); }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect("login");
            return;
        }

        int userId = currentUser.getUserId();

        // 1. All accepted friends (list of User)
        List<User> friends = friendsDAO.getFriendsAsUsers(userId);
        request.setAttribute("friends", friends);

        // 2. Pending requests you received (list of User)
        List<Integer> pendingReqFrom = friendsDAO.getPendingRequestsForUser(userId);
        List<User> pendingReceived = new ArrayList<>();
        for (int fromId : pendingReqFrom) {
            User u = userDAO.getUserById(fromId);
            if (u != null) {
                pendingReceived.add(u);
            } else {
                User fallback = new User();
                fallback.setUserId(fromId);
                fallback.setUsername("User#" + fromId);
                pendingReceived.add(fallback);
            }
        }
        request.setAttribute("pendingReceived", pendingReceived);

        // 3. Pending requests you sent (list of User)
        List<Integer> pendingSentIds = friendsDAO.getFriendIds(userId, FriendsDAO.FriendStatus.PENDING);
        List<User> pendingSent = new ArrayList<>();
        for (int sentId : pendingSentIds) {
            User u = userDAO.getUserById(sentId);
            if (u != null) {
                pendingSent.add(u);
            } else {
                User fallback = new User();
                fallback.setUserId(sentId);
                fallback.setUsername("User#" + sentId);
                pendingSent.add(fallback);
            }
        }
        request.setAttribute("pendingSent", pendingSent);

        // 4. Search for users with friendship status
        String search = request.getParameter("search");
        if (search != null && !search.trim().isEmpty()) {
            List<User> searchResults = userDAO.searchUsersByUsernameOrDisplayName(search.trim(), userId);
            List<UserWithFriendshipStatus> searchResultsWithStatus = new ArrayList<>();
            
            // Get all friend IDs for quick lookup
            List<Integer> friendIds = friendsDAO.getFriendIds(userId, FriendsDAO.FriendStatus.ACCEPTED);
            List<Integer> pendingSentIdsList = friendsDAO.getFriendIds(userId, FriendsDAO.FriendStatus.PENDING);
            List<Integer> pendingReceivedIdsList = friendsDAO.getPendingRequestsForUser(userId);
            
            for (User user : searchResults) {
                String status = "none";
                if (friendIds.contains(user.getUserId())) {
                    status = "friend";
                } else if (pendingSentIdsList.contains(user.getUserId())) {
                    status = "pending_sent";
                } else if (pendingReceivedIdsList.contains(user.getUserId())) {
                    status = "pending_received";
                }
                searchResultsWithStatus.add(new UserWithFriendshipStatus(user, status));
            }
            
            request.setAttribute("searchResults", searchResultsWithStatus);
        }

        request.getRequestDispatcher("/friends.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect("login");
            return;
        }

        int userId = currentUser.getUserId();
        String action = request.getParameter("action");
        int targetId = 0;
        try {
            targetId = Integer.parseInt(request.getParameter("targetId"));
        } catch (Exception e) {
            response.sendRedirect("friends");
            return;
        }

        if ("send".equals(action)) {
            friendsDAO.sendFriendRequest(userId, targetId);
        } else if ("accept".equals(action)) {
            friendsDAO.acceptFriendRequest(userId, targetId);
        } else if ("reject".equals(action)) {
            friendsDAO.rejectFriendRequest(userId, targetId);
        } else if ("remove".equals(action)) {
            friendsDAO.removeFriend(userId, targetId);
        }

        response.sendRedirect("friends");
    }
}
