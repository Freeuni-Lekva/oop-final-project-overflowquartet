package Servlets;

import Bean.QuizResult;
import DAO.QuizResultDAO;
import DB.DBConnector;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet("/result")
public class ResultServlet extends HttpServlet {
    private QuizResultDAO quizResultDAO;

    @Override
    public void init() throws ServletException {
        Connection conn = DBConnector.getConnection();
        quizResultDAO = new QuizResultDAO(conn);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String quizIdStr = req.getParameter("quizId");
        String userIdStr = req.getParameter("userId");
        try {
            if (quizIdStr != null) {
                int quizId = Integer.parseInt(quizIdStr);
                List<QuizResult> results = quizResultDAO.findByQuizId(quizId);
                req.setAttribute("results", results);
            } else if (userIdStr != null) {
                int userId = Integer.parseInt(userIdStr);
                List<QuizResult> results = quizResultDAO.findByUserId(userId);
                req.setAttribute("results", results);
            }
        } catch (Exception e) {
            req.setAttribute("error", "Failed to load results: " + e.getMessage());
        }
        req.getRequestDispatcher("result.jsp").forward(req, resp);
    }
} 