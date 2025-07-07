package Servlets;

import DAO.UserDAO;
import DAO.QuizDAO;
import DAO.QuizResultDAO;
import DB.DBConnector;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    private UserDAO userDAO;
    private QuizDAO quizDAO;
    private QuizResultDAO quizResultDAO;

    @Override
    public void init() throws ServletException {
        Connection conn = DBConnector.getConnection();
        userDAO = new UserDAO(conn);
        quizDAO = new QuizDAO(conn);
        quizResultDAO = new QuizResultDAO(conn);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.getRequestDispatcher("admin.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            if ("removeUser".equals(action)) {
                int userId = Integer.parseInt(req.getParameter("userId"));
                userDAO.deleteUser(userId);
            } else if ("removeQuiz".equals(action)) {
                int quizId = Integer.parseInt(req.getParameter("quizId"));
                quizDAO.deleteQuiz(quizId);
            } else if ("promoteUser".equals(action)) {
                int userId = Integer.parseInt(req.getParameter("userId"));
                var user = userDAO.findById(userId);
                user.setAdmin(true);
                userDAO.updateUser(user);
            } else if ("clearQuizHistory".equals(action)) {
                int quizId = Integer.parseInt(req.getParameter("quizId"));
                var results = quizResultDAO.findByQuizId(quizId);
                for (var result : results) {
                    quizResultDAO.deleteQuizResult(result.getResultId());
                }
            }
            resp.sendRedirect("admin");
        } catch (Exception e) {
            req.setAttribute("error", "Failed to process admin action: " + e.getMessage());
            req.getRequestDispatcher("admin.jsp").forward(req, resp);
        }
    }
} 