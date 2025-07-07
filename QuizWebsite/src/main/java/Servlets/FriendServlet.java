package Servlets;

import Bean.User;
import Bean.FriendRequest;
import DAO.FriendRequestDAO;
import DB.DBConnector;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;

@WebServlet("/friend")
public class FriendServlet extends HttpServlet {
    private FriendRequestDAO friendRequestDAO;

    @Override
    public void init() throws ServletException {
        Connection conn = DBConnector.getConnection();
        friendRequestDAO = new FriendRequestDAO(conn);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect("login.jsp");
            return;
        }
        try {
            List<FriendRequest> requests = friendRequestDAO.findByUserId(user.getUserId());
            req.setAttribute("friendRequests", requests);
        } catch (Exception e) {
            req.setAttribute("error", "Failed to load friend requests: " + e.getMessage());
        }
        req.getRequestDispatcher("friend.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect("login.jsp");
            return;
        }
        String action = req.getParameter("action");
        String toUserIdStr = req.getParameter("toUserId");
        String requestIdStr = req.getParameter("requestId");
        try {
            if ("send".equals(action)) {
                FriendRequest request = new FriendRequest();
                request.setFromUserId(user.getUserId());
                request.setToUserId(Integer.parseInt(toUserIdStr));
                request.setStatus("PENDING");
                request.setSentAt(new Timestamp(System.currentTimeMillis()));
                friendRequestDAO.createFriendRequest(request);
            } else if ("accept".equals(action) && requestIdStr != null) {
                int requestId = Integer.parseInt(requestIdStr);
                FriendRequest request = friendRequestDAO.findByUserId(user.getUserId()).stream().filter(r -> r.getRequestId() == requestId).findFirst().orElse(null);
                if (request != null) {
                    request.setStatus("ACCEPTED");
                    friendRequestDAO.updateFriendRequest(request);
                }
            } else if ("remove".equals(action) && requestIdStr != null) {
                int requestId = Integer.parseInt(requestIdStr);
                friendRequestDAO.deleteFriendRequest(requestId);
            }
            resp.sendRedirect("friend");
        } catch (Exception e) {
            req.setAttribute("error", "Failed to process friend request: " + e.getMessage());
            req.getRequestDispatcher("friend.jsp").forward(req, resp);
        }
    }
} 