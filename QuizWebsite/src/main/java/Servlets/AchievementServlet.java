package Servlets;

import Bean.User;
import DB.AchievementsDAO;
import Bean.Achievement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/achievements")
public class AchievementServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        AchievementsDAO dao = new AchievementsDAO();
        List<Achievement> achievements = dao.getAchievementsByUserId(user.getUserId());

        request.setAttribute("achievements", achievements);
        request.getRequestDispatcher("/achievements.jsp").forward(request, response);
    }
}
