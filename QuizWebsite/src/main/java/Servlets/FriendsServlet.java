package Servlets;

import Bean.User;
import DB.FriendsDAO;
import DB.MessageDAO;
import DB.UserDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/friends")
public class FriendsServlet extends HttpServlet {

    private final FriendsDAO friendsDAO = new FriendsDAO();
    private final UserDAO userDAO = new UserDAO();

    public static class UserWithFriendshipStatus {
        private final User user;
        private final String friendshipStatus;

        public UserWithFriendshipStatus(User user, String status) {
            this.user = user;
            this.friendshipStatus = status;
        }

        public User getUser() { return user; }

        public boolean isFriend() { return "friend".equals(friendshipStatus); }
        public boolean isPendingSent() { return "pending_sent".equals(friendshipStatus); }
        public boolean isPendingReceived() { return "pending_received".equals(friendshipStatus); }
        public boolean isNone() { return "none".equals(friendshipStatus); }

        public String getFriendshipStatus() {
            return friendshipStatus;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (User) (session != null ? session.getAttribute("user") : null);
        if (currentUser == null) {
            response.sendRedirect("login");
            return;
        }

        int userId = currentUser.getUserId();

        // 1. Accepted Friends
        List<User> friends = friendsDAO.getFriendsAsUsers(userId);
        request.setAttribute("friends", friends);

        // 2. Pending friend requests received
        List<Integer> pendingReceivedIds = friendsDAO.getPendingRequestsForUser(userId);
        List<User> pendingReceived = new ArrayList<>();
        for (int fromId : pendingReceivedIds) {
            User u = userDAO.getUserById(fromId);
            pendingReceived.add(u != null ? u : fallbackUser(fromId));
        }
        request.setAttribute("pendingReceived", pendingReceived);

        // 3. Pending friend requests sent
        List<Integer> pendingSentIds = friendsDAO.getFriendIds(userId, FriendsDAO.FriendStatus.PENDING);
        List<User> pendingSent = new ArrayList<>();
        for (int sentId : pendingSentIds) {
            User u = userDAO.getUserById(sentId);
            pendingSent.add(u != null ? u : fallbackUser(sentId));
        }
        request.setAttribute("pendingSent", pendingSent);

        // 4. Search functionality
        String search = request.getParameter("search");
        if (search != null && !search.trim().isEmpty()) {
            List<User> searchResults = userDAO.searchUsersByUsernameOrDisplayName(search.trim(), userId);
            List<UserWithFriendshipStatus> searchResultsWithStatus = new ArrayList<>();

            List<Integer> friendIds = friendsDAO.getFriendIds(userId, FriendsDAO.FriendStatus.ACCEPTED);
            List<Integer> sentIds = friendsDAO.getFriendIds(userId, FriendsDAO.FriendStatus.PENDING);
            List<Integer> receivedIds = friendsDAO.getPendingRequestsForUser(userId);

            for (User user : searchResults) {
                String status = "none";
                if (friendIds.contains(user.getUserId())) {
                    status = "friend";
                } else if (sentIds.contains(user.getUserId())) {
                    status = "pending_sent";
                } else if (receivedIds.contains(user.getUserId())) {
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

        HttpSession session = request.getSession(false);
        User currentUser = (User) (session != null ? session.getAttribute("user") : null);
        if (currentUser == null) {
            response.sendRedirect("login");
            return;
        }

        int userId = currentUser.getUserId();
        String action = request.getParameter("action");
        int targetId;

        try {
            targetId = Integer.parseInt(request.getParameter("targetId"));
        } catch (Exception e) {
            response.sendRedirect("friends");
            return;
        }

        switch (action) {
            case "send" -> {
                friendsDAO.sendFriendRequest(userId, targetId);

                // ✅ Send internal message as well
                MessageDAO msgDAO = new MessageDAO();
                msgDAO.sendFriendRequestMessage(userId, targetId);
            }
            case "accept" -> friendsDAO.acceptFriendRequest(userId, targetId);
            case "reject" -> friendsDAO.rejectFriendRequest(userId, targetId);
            case "remove" -> friendsDAO.removeFriend(userId, targetId);
        }

        response.sendRedirect("friends");
    }

    private User fallbackUser(int id) {
        User u = new User();
        u.setUserId(id);
        u.setUsername("User#" + id);
        return u;
    }
}
