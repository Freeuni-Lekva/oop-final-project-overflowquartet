package Servlets;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Object user = req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect("login");
            return;
        }

        // Announcements
        List<String> announcements = new ArrayList<>();
        announcements.add("Welcome to Quizzmosis!");
        announcements.add("New feature: Achievements!");
        req.setAttribute("announcements", announcements);

        //Friends' activities
        List<String> friendsActivities = new ArrayList<>();
        friendsActivities.add("Alice took 'Java Basics Quiz' and scored 90%.");
        friendsActivities.add("Bob created a new quiz: 'World Capitals'.");
        req.setAttribute("friendsActivities", friendsActivities);

        // Recent quizzes
        List<String> recentQuizzes = new ArrayList<>();
        recentQuizzes.add("Java Basics Quiz");
        recentQuizzes.add("World Capitals");
        recentQuizzes.add("Famous Paintings");
        req.setAttribute("recentQuizzes", recentQuizzes);

        //Achievements
        List<String> achievements = new ArrayList<>();
        achievements.add("Amateur Author");
        achievements.add("Quiz Machine");
        req.setAttribute("achievements", achievements);

        RequestDispatcher dispatcher = req.getRequestDispatcher("/home_page.jsp");
        dispatcher.forward(req, resp);
    }
} 