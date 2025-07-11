package Servlets;

import Bean.Quiz;
import Bean.QuizStats;
import Bean.User;
import Bean.UserPerformance;
import DB.QuizDAO;
import DB.QuizStatsDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/quiz-summary")
public class QuizSummaryServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Check if user is logged in
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        // Get quiz ID from request
        String quizIdStr = request.getParameter("quizId");
        if (quizIdStr == null || quizIdStr.trim().isEmpty()) {
            response.sendRedirect("quizzes");
            return;
        }
        
        try {
            int quizId = Integer.parseInt(quizIdStr);
            
            // Get quiz details
            QuizDAO quizDAO = new QuizDAO();
            Quiz quiz = quizDAO.getQuizById(quizId);
            
            if (quiz == null) {
                request.setAttribute("error", "Quiz not found.");
                request.getRequestDispatcher("/error.jsp").forward(request, response);
                return;
            }
            
            // Get quiz statistics
            QuizStatsDAO statsDAO = new QuizStatsDAO();
            QuizStats stats = statsDAO.getQuizStats(quizId);
            
            // Get user's past performance
            String sortBy = request.getParameter("sortBy");
            if (sortBy == null) {
                sortBy = "date"; // default sort
            }
            List<UserPerformance> userPerformance = statsDAO.getUserPerformanceOnQuiz(user.getUserId(), quizId, sortBy);
            
            // Get top performers (all time)
            List<UserPerformance> topPerformers = statsDAO.getTopPerformersAllTime(quizId, 10);
            
            // Get top performers (last day)
            List<UserPerformance> topPerformersLastDay = statsDAO.getTopPerformersLastDay(quizId, 5);
            
            // Get recent test takers
            List<UserPerformance> recentTestTakers = statsDAO.getRecentTestTakers(quizId, 10);
            
            // Set attributes for JSP
            request.setAttribute("quiz", quiz);
            request.setAttribute("stats", stats);
            request.setAttribute("userPerformance", userPerformance);
            request.setAttribute("topPerformers", topPerformers);
            request.setAttribute("topPerformersLastDay", topPerformersLastDay);
            request.setAttribute("recentTestTakers", recentTestTakers);
            request.setAttribute("currentSort", sortBy);
            request.setAttribute("isOwner", user.getUserId() == quiz.getOwnerId());
            
            // Forward to JSP
            request.getRequestDispatcher("/quiz_summary.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid quiz ID.");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
} 