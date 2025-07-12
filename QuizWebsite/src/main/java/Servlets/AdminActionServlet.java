package Servlets;

import Bean.Announcement;
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

@WebServlet("/admin/action")
public class AdminActionServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
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
        
        String action = req.getParameter("action");
        String message = "";
        boolean success = false;
        
        switch (action) {
            case "create_announcement":
                success = handleCreateAnnouncement(req);
                message = success ? "Announcement created successfully!" : "Failed to create announcement.";
                break;
                
            case "delete_announcement":
                success = handleDeleteAnnouncement(req);
                message = success ? "Announcement deleted successfully!" : "Failed to delete announcement.";
                break;
                
            case "toggle_announcement":
                success = handleToggleAnnouncement(req);
                message = success ? "Announcement status updated!" : "Failed to update announcement status.";
                break;
                
            case "delete_user":
                success = handleDeleteUser(req);
                message = success ? "User deleted successfully!" : "Failed to delete user.";
                break;
                
            case "promote_user":
                success = handlePromoteUser(req);
                message = success ? "User promoted to admin!" : "Failed to promote user.";
                break;
                
            case "demote_user":
                success = handleDemoteUser(req);
                message = success ? "User demoted from admin!" : "Failed to demote user.";
                break;
                
            case "delete_quiz":
                success = handleDeleteQuiz(req);
                message = success ? "Quiz deleted successfully!" : "Failed to delete quiz.";
                break;
                
            case "clear_quiz_history":
                success = handleClearQuizHistory(req);
                message = success ? "Quiz history cleared successfully!" : "Failed to clear quiz history.";
                break;
                
            default:
                message = "Invalid action.";
                break;
        }
        
        // Set message and redirect back to admin dashboard
        session.setAttribute("adminMessage", message);
        session.setAttribute("adminMessageType", success ? "success" : "danger");
        resp.sendRedirect(req.getContextPath() + "/admin");
    }
    
    private boolean handleCreateAnnouncement(HttpServletRequest req) {
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        User currentUser = (User) req.getSession().getAttribute("user");
        
        if (title == null || content == null || title.trim().isEmpty() || content.trim().isEmpty()) {
            return false;
        }
        
        Announcement announcement = new Announcement(title.trim(), content.trim(), currentUser.getUserId());
        AnnouncementDAO announcementDAO = new AnnouncementDAO();
        return announcementDAO.createAnnouncement(announcement);
    }
    
    private boolean handleDeleteAnnouncement(HttpServletRequest req) {
        try {
            int announcementId = Integer.parseInt(req.getParameter("announcement_id"));
            AnnouncementDAO announcementDAO = new AnnouncementDAO();
            return announcementDAO.deleteAnnouncement(announcementId);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean handleToggleAnnouncement(HttpServletRequest req) {
        try {
            int announcementId = Integer.parseInt(req.getParameter("announcement_id"));
            AnnouncementDAO announcementDAO = new AnnouncementDAO();
            return announcementDAO.toggleAnnouncementStatus(announcementId);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean handleDeleteUser(HttpServletRequest req) {
        try {
            int userId = Integer.parseInt(req.getParameter("user_id"));
            UserDAO userDAO = new UserDAO();
            return userDAO.deleteUser(userId);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean handlePromoteUser(HttpServletRequest req) {
        try {
            int userId = Integer.parseInt(req.getParameter("user_id"));
            UserDAO userDAO = new UserDAO();
            return userDAO.promoteToAdmin(userId);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean handleDemoteUser(HttpServletRequest req) {
        try {
            int userId = Integer.parseInt(req.getParameter("user_id"));
            UserDAO userDAO = new UserDAO();
            return userDAO.demoteFromAdmin(userId);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean handleDeleteQuiz(HttpServletRequest req) {
        try {
            int quizId = Integer.parseInt(req.getParameter("quiz_id"));
            QuizDAO quizDAO = new QuizDAO();
            return quizDAO.deleteQuiz(quizId);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean handleClearQuizHistory(HttpServletRequest req) {
        try {
            int quizId = Integer.parseInt(req.getParameter("quiz_id"));
            QuizDAO quizDAO = new QuizDAO();
            return quizDAO.clearQuizHistory(quizId);
        } catch (NumberFormatException e) {
            return false;
        }
    }
} 