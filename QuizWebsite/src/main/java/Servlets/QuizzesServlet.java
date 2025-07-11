package Servlets;

import Bean.Quiz;
import Bean.User;
import DB.QuizDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/quizzes")
public class QuizzesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        QuizDAO quizDAO = new QuizDAO();
        
        // Get popular quizzes (top 3) to show at the top
        List<Quiz> popularQuizzes = quizDAO.getPopularQuizzes(3);
        request.setAttribute("popularQuizzes", popularQuizzes);
        
        // Get all quizzes for the main list
        List<Quiz> allQuizzes = quizDAO.getAllQuizzesWithQuestionCount();
        request.setAttribute("allQuizzes", allQuizzes);
        
        // Get current user's quizzes if logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User currentUser = (User) session.getAttribute("user");
            List<Quiz> myQuizzes = quizDAO.getQuizzesByOwnerWithQuestionCount(currentUser.getUserId());
            request.setAttribute("myQuizzes", myQuizzes);
        }
        
        request.getRequestDispatcher("quizzes.jsp").forward(request, response);
    }
}