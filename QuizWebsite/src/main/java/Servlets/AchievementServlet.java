package Servlets;

import Bean.User;
import Bean.Achievement;
import DAO.AchievementDAO;
import DB.DBConnector;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet("/achievement")
public class AchievementServlet extends HttpServlet {
    private AchievementDAO achievementDAO;

    @Override
    public void init() throws ServletException {
        Connection conn = DBConnector.getConnection();
        achievementDAO = new AchievementDAO(conn);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect("login.jsp");
            return;
        }
        try {
            List<Achievement> achievements = achievementDAO.findByUserId(user.getUserId());
            req.setAttribute("achievements", achievements);
        } catch (Exception e) {
            req.setAttribute("error", "Failed to load achievements: " + e.getMessage());
        }
        req.getRequestDispatcher("achievement.jsp").forward(req, resp);
    }
} 