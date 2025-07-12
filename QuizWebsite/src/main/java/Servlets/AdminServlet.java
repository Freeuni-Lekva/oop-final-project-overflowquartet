package Servlets;

import Bean.Announcement;
import Bean.Quiz;
import Bean.User;
import DB.AnnouncementDAO;
import DB.QuizDAO;
import DB.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        if (!currentUser.isAdmin()) {
            resp.sendRedirect(req.getContextPath() + "/access_denied.jsp");
            return;
        }
        
        // Get data for admin dashboard
        UserDAO userDAO = new UserDAO();
        QuizDAO quizDAO = new QuizDAO();
        AnnouncementDAO announcementDAO = new AnnouncementDAO();
        
        // Get all users
        List<User> allUsers = userDAO.getAllUsers();
        req.setAttribute("allUsers", allUsers);
        
        // Get all quizzes
        List<Quiz> allQuizzes = quizDAO.getAllQuizzes();
        req.setAttribute("allQuizzes", allQuizzes);
        
        // Get all announcements
        List<Announcement> allAnnouncements = announcementDAO.getAllAnnouncements();
        req.setAttribute("allAnnouncements", allAnnouncements);
        
        // Forward to admin dashboard
        req.getRequestDispatcher("/admin_dashboard.jsp").forward(req, resp);
    }
} 