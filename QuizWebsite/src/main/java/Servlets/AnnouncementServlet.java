package Servlets;

import Bean.Announcement;
import DAO.AnnouncementDAO;
import DB.DBConnector;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;

@WebServlet("/announcement")
public class AnnouncementServlet extends HttpServlet {
    private AnnouncementDAO announcementDAO;

    @Override
    public void init() throws ServletException {
        Connection conn = DBConnector.getConnection();
        announcementDAO = new AnnouncementDAO(conn);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Announcement> announcements = announcementDAO.findAll();
            req.setAttribute("announcements", announcements);
        } catch (Exception e) {
            req.setAttribute("error", "Failed to load announcements: " + e.getMessage());
        }
        req.getRequestDispatcher("announcement.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        try {
            if ("create".equals(action)) {
                String content = req.getParameter("content");
                Announcement announcement = new Announcement();
                announcement.setContent(content);
                announcement.setPostedAt(new Timestamp(System.currentTimeMillis()));
                announcementDAO.createAnnouncement(announcement);
            } else if ("delete".equals(action)) {
                int announcementId = Integer.parseInt(req.getParameter("announcementId"));
                announcementDAO.deleteAnnouncement(announcementId);
            }
            resp.sendRedirect("announcement");
        } catch (Exception e) {
            req.setAttribute("error", "Failed to process announcement: " + e.getMessage());
            req.getRequestDispatcher("announcement.jsp").forward(req, resp);
        }
    }
} 