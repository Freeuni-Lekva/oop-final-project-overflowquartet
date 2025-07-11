package Servlets;

import Bean.User;
import DB.QuizDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/create")
public class QuizCreateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Require login
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect("index.jsp");
            return;
        }
        req.getRequestDispatcher("/create.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect("index.jsp");
            return;
        }

        String title = req.getParameter("title");
        String description = req.getParameter("description");
        boolean randomOrder = req.getParameter("randomOrder") != null;
        boolean multiplePages = req.getParameter("multiplePages") != null;
        boolean immediateCorrection = req.getParameter("immediateCorrection") != null;

        // Basic validation
        if (title == null || title.trim().isEmpty() || title.length() > 100) {
            req.setAttribute("error", "Quiz title is required and must be under 100 characters.");
            req.getRequestDispatcher("/create.jsp").forward(req, resp);
            return;
        }

        QuizDAO quizDAO = new QuizDAO();
        Integer quizId = quizDAO.createQuiz(user.getUserId(), title.trim(), description, randomOrder, multiplePages, immediateCorrection);

        if (quizId == null) {
            req.setAttribute("error", "Failed to create quiz. Please try again.");
            req.getRequestDispatcher("/create.jsp").forward(req, resp);
        } else {
            // Check for quiz creation achievements
            DB.AchievementsService achievementsService = new DB.AchievementsService();
            achievementsService.checkQuizCreationAchievements(user.getUserId());
            
            // Redirect to add questions page
            resp.sendRedirect(req.getContextPath() + "/add-questions?quizId=" + quizId);
        }
    }
}
 