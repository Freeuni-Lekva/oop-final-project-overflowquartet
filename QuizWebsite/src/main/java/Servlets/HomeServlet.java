package Servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.annotation.WebServlet;
import Bean.User;
import Bean.Quiz;
import DB.QuizDAO;
import DB.UserDAO;

@WebServlet("/HomeServlet")
public class HomeServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Check if user is logged in
        Object userObj = req.getSession().getAttribute("user");
        if (userObj == null) {
            resp.sendRedirect("index.jsp");
            return;
        }

        User currentUser = (User) userObj;
        int userId = currentUser.getUserId();

        // Initialize DAOs
        QuizDAO quizDAO = new QuizDAO();

        try {
            // Prepare featured quizzes data
            List<Quiz> featuredQuizzes = prepareFeaturedQuizzes(quizDAO);
            req.setAttribute("featuredQuizzes", featuredQuizzes);

            // Prepare user-specific data
            req.setAttribute("currentUser", currentUser);
            req.setAttribute("userDisplayName", currentUser.getDisplayName());

            // Prepare recent activities
            List<String> recentActivities = prepareRecentActivities(quizDAO, userId);
            req.setAttribute("recentActivities", recentActivities);

            // Prepare user statistics
            req.setAttribute("totalQuizzesTaken", getTotalQuizzesTaken(quizDAO, userId));
            req.setAttribute("totalQuizzesCreated", getTotalQuizzesCreated(quizDAO, userId));
            req.setAttribute("averageScore", getAverageScore(quizDAO, userId));

            // Prepare announcements
            List<String> announcements = prepareAnnouncements();
            req.setAttribute("announcements", announcements);

            // Forward to home page
            RequestDispatcher dispatcher = req.getRequestDispatcher("/home_page.jsp");
            dispatcher.forward(req, resp);

        } catch (Exception e) {
            // Handle any errors gracefully
            req.setAttribute("error", "Unable to load home page data. Please try again.");
            RequestDispatcher dispatcher = req.getRequestDispatcher("/home_page.jsp");
            dispatcher.forward(req, resp);
        }
    }

    private List<Quiz> prepareFeaturedQuizzes(QuizDAO quizDAO) {
        // Use actual database query to get all quizzes with question counts
        return quizDAO.getAllQuizzesWithQuestionCount();
    }

    private List<String> prepareRecentActivities(QuizDAO quizDAO, int userId) {
        List<String> activities = new ArrayList<>();
        
        // Sample activities - replace with actual database queries
        activities.add("You completed 'Java Basics Quiz' with 85% score");
        activities.add("You created a new quiz: 'Programming Fundamentals'");
        activities.add("Alice challenged you to 'World Capitals Quiz'");
        
        return activities;
    }

    private List<String> prepareAnnouncements() {
        List<String> announcements = new ArrayList<>();
        announcements.add("Welcome to Quizzmosis!");
        announcements.add("New feature: Achievements system is now live!");
        announcements.add("Create your first quiz and earn the 'Quiz Creator' badge");
        return announcements;
    }

    private int getTotalQuizzesTaken(QuizDAO quizDAO, int userId) {
        // TODO: Implement actual database query
        return 5; // Sample data
    }

    private int getTotalQuizzesCreated(QuizDAO quizDAO, int userId) {
        // TODO: Implement actual database query
        return 2; // Sample data
    }

    private double getAverageScore(QuizDAO quizDAO, int userId) {
        // TODO: Implement actual database query
        return 78.5; // Sample data
    }
} 