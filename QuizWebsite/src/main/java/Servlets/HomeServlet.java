//package Servlets;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.*;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import jakarta.servlet.annotation.WebServlet;
//import Bean.User;
//import Bean.Quiz;
//import DB.QuizDAO;
//import DB.UserDAO;
//import DB.QuizAttemptDAO;
//import Bean.QuizAttempt;
//
//@WebServlet("/HomeServlet")
//public class HomeServlet extends HttpServlet {
//
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        // Check if user is logged in
//        Object userObj = req.getSession().getAttribute("user");
//        if (userObj == null) {
//            resp.sendRedirect("index.jsp");
//            return;
//        }
//
//        User currentUser = (User) userObj;
//        int userId = currentUser.getUserId();
//
//        // Initialize DAOs
//        QuizDAO quizDAO = new QuizDAO();
//        QuizAttemptDAO attemptDAO = new QuizAttemptDAO();
//
//        try {
//            // Prepare featured quizzes data
//            List<Quiz> featuredQuizzes = prepareFeaturedQuizzes(quizDAO);
//            req.setAttribute("featuredQuizzes", featuredQuizzes);
//
//            // Prepare user-specific data
//            req.setAttribute("currentUser", currentUser);
//            req.setAttribute("userDisplayName", currentUser.getDisplayName());
//
//            // Prepare recent activities
//            List<String> recentActivities = prepareRecentActivities(quizDAO, userId);
//            req.setAttribute("recentActivities", recentActivities);
//
//            // Prepare user statistics
//            req.setAttribute("totalQuizzesTaken", getTotalQuizzesTaken(quizDAO, userId));
//            req.setAttribute("totalQuizzesCreated", getTotalQuizzesCreated(quizDAO, userId));
//            req.setAttribute("averageScore", getAverageScore(quizDAO, userId));
//
//            // Prepare announcements
//            List<String> announcements = prepareAnnouncements();
//            req.setAttribute("announcements", announcements);
//
//            // Prepare recent quiz attempts (real data)
//            List<QuizAttempt> allAttempts = attemptDAO.getAttemptsByUser(userId);
//            List<AttemptWithQuiz> recentAttemptsWithQuiz = new ArrayList<>();
//            int count = 0;
//            for (QuizAttempt a : allAttempts) {
//                if (count++ >= 3) break;
//                Quiz quiz = quizDAO.getQuizById(a.getQuizId());
//                recentAttemptsWithQuiz.add(new AttemptWithQuiz(a, quiz));
//            }
//            req.setAttribute("recentAttemptsWithQuiz", recentAttemptsWithQuiz);
//
//            // Forward to home page
//            RequestDispatcher dispatcher = req.getRequestDispatcher("/home_page.jsp");
//            dispatcher.forward(req, resp);
//
//        } catch (Exception e) {
//            // Handle any errors gracefully
//            req.setAttribute("error", "Unable to load home page data. Please try again.");
//            RequestDispatcher dispatcher = req.getRequestDispatcher("/home_page.jsp");
//            dispatcher.forward(req, resp);
//        }
//    }
//
//    private List<Quiz> prepareFeaturedQuizzes(QuizDAO quizDAO) {
//        // Use actual database query to get all quizzes with question counts
//        return quizDAO.getAllQuizzesWithQuestionCount();
//    }
//
//    private List<String> prepareRecentActivities(QuizDAO quizDAO, int userId) {
//        List<String> activities = new ArrayList<>();
//
//        // Sample activities - replace with actual database queries
//        activities.add("You completed 'Java Basics Quiz' with 85% score");
//        activities.add("You created a new quiz: 'Programming Fundamentals'");
//        activities.add("Alice challenged you to 'World Capitals Quiz'");
//
//        return activities;
//    }
//
//    private List<String> prepareAnnouncements() {
//        List<String> announcements = new ArrayList<>();
//        announcements.add("Welcome to Quizzmosis!");
//        announcements.add("New feature: Achievements system is now live!");
//        announcements.add("Create your first quiz and earn the 'Quiz Creator' badge");
//        return announcements;
//    }
//
//    private int getTotalQuizzesTaken(QuizDAO quizDAO, int userId) {
//        // TODO: Implement actual database query
//        return 5; // Sample data
//    }
//
//    private int getTotalQuizzesCreated(QuizDAO quizDAO, int userId) {
//        // TODO: Implement actual database query
//        return 2; // Sample data
//    }
//
//    private double getAverageScore(QuizDAO quizDAO, int userId) {
//        // TODO: Implement actual database query
//        return 78.5; // Sample data
//    }
//
//    public static class AttemptWithQuiz {
//        public QuizAttempt attempt;
//        public Quiz quiz;
//        public AttemptWithQuiz(QuizAttempt attempt, Quiz quiz) {
//            this.attempt = attempt;
//            this.quiz = quiz;
//        }
//    }
       // }
package Servlets;

import Bean.Quiz;
import Bean.QuizAttempt;
import Bean.User;
import DB.FriendsDAO;
import DB.MessageDAO;
import DB.QuizDAO;
import DB.QuizAttemptDAO;
import DB.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@WebServlet("/HomeServlet")
public class HomeServlet extends HttpServlet {

    // reuse this for both your own attempts and friends' attempts
    public static class AttemptWithQuiz {
        public final QuizAttempt attempt;
        public final Quiz      quiz;
        public AttemptWithQuiz(QuizAttempt a, Quiz q) {
            this.attempt = a;
            this.quiz    = q;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }
        User currentUser = (User) session.getAttribute("user");
        int userId = currentUser.getUserId();

        // DAOs
        QuizDAO         quizDAO      = new QuizDAO();
        QuizAttemptDAO  attemptDAO   = new QuizAttemptDAO();
        MessageDAO      messageDAO   = new MessageDAO();
        FriendsDAO      friendsDAO   = new FriendsDAO();
        UserDAO         userDAO      = new UserDAO();

        // 1) Featured quizzes (global)
        List<Quiz> featuredQuizzes = quizDAO.getAllQuizzesWithQuestionCount();
        req.setAttribute("featuredQuizzes", featuredQuizzes);

        // 2) Your name for greeting
        req.setAttribute("userDisplayName", currentUser.getDisplayName());

        // 3) Quizzes you created
        List<Quiz> myCreated = quizDAO.getQuizzesByOwner(userId);
        req.setAttribute("myCreatedQuizzes", myCreated);

        // 4) Unread messages count
        long unread = messageDAO.getMessagesByReceiver(userId)
                .stream()
                .filter(m -> !m.isRead())
                .count();
        req.setAttribute("unreadCount", (int) unread);

        // 5) Your friends list
        List<Integer> friendIds = friendsDAO.getFriendIds(userId, FriendsDAO.FriendStatus.ACCEPTED);
        List<User> myFriends = new ArrayList<>();
        for (int fid : friendIds) {
            User f = userDAO.getUserById(fid);
            if (f != null) myFriends.add(f);
        }
        req.setAttribute("myFriends", myFriends);

        // 6) Recent activity by friends (up to 3 each)
        List<AttemptWithQuiz> friendsActivity = new ArrayList<>();
        for (User f : myFriends) {
            int count = 0;
            for (QuizAttempt a : attemptDAO.getAttemptsByUser(f.getUserId())) {
                if (count++ >= 3) break;
                Quiz q = quizDAO.getQuizById(a.getQuizId());
                friendsActivity.add(new AttemptWithQuiz(a, q));
            }
        }
        req.setAttribute("friendsActivity", friendsActivity);

        // 7) Your recent quiz attempts (up to 3)
        List<QuizAttempt> allYour = attemptDAO.getAttemptsByUser(userId);
        List<AttemptWithQuiz> recentYour = new ArrayList<>();
        for (int i = 0; i < Math.min(3, allYour.size()); i++) {
            QuizAttempt a = allYour.get(i);
            Quiz q = quizDAO.getQuizById(a.getQuizId());
            recentYour.add(new AttemptWithQuiz(a, q));
        }
        req.setAttribute("recentAttemptsWithQuiz", recentYour);

        // Forward to JSP
        req.getRequestDispatcher("/home_page.jsp")
                .forward(req, resp);
    }
}
