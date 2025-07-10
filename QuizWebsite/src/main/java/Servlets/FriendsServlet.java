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
        List<Integer> friendIds = friendsDAO.getFriendIds(userId, FriendsDAO.FriendStatus.ACCEPTED);
        List<User> friends = new ArrayList<>();
        for (int fid : friendIds) {
            User friend = userDAO.getUserById(fid);
            if (friend != null) friends.add(friend);
        }
        request.setAttribute("friends", friends);

        // 2. Pending requests you received (list of User)
        List<Integer> pendingReqFrom = friendsDAO.getPendingRequestsForUser(userId);
        List<User> pendingReceived = new ArrayList<>();
        for (int fromId : pendingReqFrom) {
            User u = userDAO.getUserById(fromId);
            if (u != null) pendingReceived.add(u);
        }
        request.setAttribute("pendingReceived", pendingReceived);

        // 3. Pending requests you sent (list of User)
        List<Integer> pendingSentIds = friendsDAO.getFriendIds(userId, FriendsDAO.FriendStatus.PENDING);
        List<User> pendingSent = new ArrayList<>();
        for (int sentId : pendingSentIds) {
            User u = userDAO.getUserById(sentId);
            if (u != null) pendingSent.add(u);
        }
        request.setAttribute("pendingSent", pendingSent);

        // 4. Optional: Users you can send requests to (not already friends or pending)
        // Not required for basic, but can implement "search" or "suggested friends"

        // Optionally: Support search for users
        String search = request.getParameter("search");
        if (search != null && !search.trim().isEmpty()) {
            List<User> searchResults = userDAO.searchUsersByUsernameOrDisplayName(search.trim(), userId);
            request.setAttribute("searchResults", searchResults);
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
