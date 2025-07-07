package Servlets;

import Bean.PasswordUtil;
import Bean.User;
import DAO.UserDAO;
import DB.DBConnector;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

@WebServlet("/user")
public class UserServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        Connection conn = DBConnector.getConnection();
        userDAO = new UserDAO(conn);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "profile";
        switch (action) {
            case "logout":
                req.getSession().invalidate();
                resp.sendRedirect("login.jsp");
                break;
            case "profile":
            default:

                req.getRequestDispatcher("profile.jsp").forward(req, resp);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "login";
        switch (action) {
            case "register":
                handleRegister(req, resp);
                break;
            case "login":
            default:
                handleLogin(req, resp);
                break;
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String displayName = req.getParameter("displayName");
        String email = req.getParameter("email");
        try {
            if (userDAO.findByUsername(username) != null) {
                req.setAttribute("error", "Username already exists.");
                req.getRequestDispatcher("register.jsp").forward(req, resp);
                return;
            }
            String salt = PasswordUtil.generateSalt();
            String hash = PasswordUtil.hashPassword(password, salt);
            Timestamp now = new Timestamp(System.currentTimeMillis());
            User user = new User(0, username, hash, salt, displayName, email, now, null, false);
            userDAO.createUser(user);
            resp.sendRedirect("login.jsp");
        } catch (Exception e) {
            req.setAttribute("error", "Registration failed: " + e.getMessage());
            req.getRequestDispatcher("register.jsp").forward(req, resp);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException, ServletException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        try {
            User user = userDAO.findByUsername(username);
            if (user == null) {
                req.setAttribute("error", "Invalid username or password.");
                req.getRequestDispatcher("login.jsp").forward(req, resp);
                return;
            }
            String hash = PasswordUtil.hashPassword(password, user.getSalt());
            if (!hash.equals(user.getPasswordHash())) {
                req.setAttribute("error", "Invalid username or password.");
                req.getRequestDispatcher("login.jsp").forward(req, resp);
                return;
            }
            HttpSession session = req.getSession();
            session.setAttribute("user", user);

            user.setLastLogin(new Timestamp(System.currentTimeMillis()));
            userDAO.updateUser(user);
            resp.sendRedirect("index.jsp");
        } catch (Exception e) {
            req.setAttribute("error", "Login failed: " + e.getMessage());
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
    }
} 