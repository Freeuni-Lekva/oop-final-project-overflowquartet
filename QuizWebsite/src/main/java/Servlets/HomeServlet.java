package Servlets;

import Bean.User;
import DAO.AnnouncementDAO;
import DAO.QuizDAO;
import DAO.QuizResultDAO;
import DB.DBConnector;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private AnnouncementDAO announcementDAO;
    private QuizDAO quizDAO;
    private QuizResultDAO quizResultDAO;

    @Override
    public void init() throws ServletException {
        Connection conn = DBConnector.getConnection();
        announcementDAO = new AnnouncementDAO(conn);
        quizDAO = new QuizDAO(conn);
        quizResultDAO = new QuizResultDAO(conn);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("announcements", announcementDAO.findAll());
            req.setAttribute("quizzes", quizDAO.findAll());
            User user = (User) req.getSession().getAttribute("user");
            if (user != null) {
                req.setAttribute("userResults", quizResultDAO.findByUserId(user.getUserId()));
            }
        } catch (Exception e) {
            req.setAttribute("error", "Failed to load homepage data: " + e.getMessage());
        }
        req.getRequestDispatcher("index.jsp").forward(req, resp);
    }
} 