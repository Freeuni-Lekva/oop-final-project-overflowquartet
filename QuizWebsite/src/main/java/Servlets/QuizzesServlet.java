package Servlets;

import Bean.Quiz;
import DB.QuizDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/quizzes")
public class QuizzesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        QuizDAO quizDAO = new QuizDAO();
        List<Quiz> allQuizzes = quizDAO.getAllQuizzesWithQuestionCount();
        request.setAttribute("allQuizzes", allQuizzes);
        request.getRequestDispatcher("quizzes.jsp").forward(request, response);
    }
}