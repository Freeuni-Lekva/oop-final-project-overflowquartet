package Servlets;

import Bean.User;
import Bean.Message;
import DAO.MessageDAO;
import DB.DBConnector;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;

@WebServlet("/message")
public class MessageServlet extends HttpServlet {
    private MessageDAO messageDAO;

    @Override
    public void init() throws ServletException {
        Connection conn = DBConnector.getConnection();
        messageDAO = new MessageDAO(conn);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect("login.jsp");
            return;
        }
        try {
            List<Message> messages = messageDAO.findByUserId(user.getUserId());
            req.setAttribute("messages", messages);
        } catch (Exception e) {
            req.setAttribute("error", "Failed to load messages: " + e.getMessage());
        }
        req.getRequestDispatcher("message.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect("login.jsp");
            return;
        }
        String toUserIdStr = req.getParameter("toUserId");
        String type = req.getParameter("type");
        String content = req.getParameter("content");
        try {
            Message message = new Message();
            message.setFromUserId(user.getUserId());
            message.setToUserId(Integer.parseInt(toUserIdStr));
            message.setType(type);
            message.setContent(content);
            message.setSentAt(new Timestamp(System.currentTimeMillis()));
            message.setRead(false);
            messageDAO.createMessage(message);
            resp.sendRedirect("message");
        } catch (Exception e) {
            req.setAttribute("error", "Failed to send message: " + e.getMessage());
            req.getRequestDispatcher("message.jsp").forward(req, resp);
        }
    }
} 