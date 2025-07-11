package Servlets;

import Bean.User;
import DB.FriendsDAO;
import DB.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }
        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.sendRedirect(req.getContextPath() + "/HomeServlet");
            return;
        }
        int profileId;
        try {
            profileId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/HomeServlet");
            return;
        }
        UserDAO userDAO = new UserDAO();
        User profileUser = userDAO.getUserById(profileId);
        if (profileUser == null) {
            req.setAttribute("error", "User not found.");
            req.getRequestDispatcher("/home_page.jsp").forward(req, resp);
            return;
        }
        boolean isSelf = (profileUser.getUserId() == currentUser.getUserId());
        boolean isFriend = false;
        boolean isPending = false;
        boolean canAddFriend = false;
        if (!isSelf) {
            FriendsDAO friendsDAO = new FriendsDAO();
            var friends = friendsDAO.getFriendIds(currentUser.getUserId(), FriendsDAO.FriendStatus.ACCEPTED);
            isFriend = friends.contains(profileUser.getUserId());
            var pending = friendsDAO.getFriendIds(currentUser.getUserId(), FriendsDAO.FriendStatus.PENDING);
            isPending = pending.contains(profileUser.getUserId());
            canAddFriend = !isFriend && !isPending;
        }
        req.setAttribute("profileUser", profileUser);
        req.setAttribute("isSelf", isSelf);
        req.setAttribute("isFriend", isFriend);
        req.setAttribute("isPending", isPending);
        req.setAttribute("canAddFriend", canAddFriend);
        req.getRequestDispatcher("/profile.jsp").forward(req, resp);
    }
} 