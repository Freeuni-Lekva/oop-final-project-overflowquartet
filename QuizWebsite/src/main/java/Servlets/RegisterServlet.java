package Servlets;

import DB.UserDAO;
import Bean.User;
import Bean.PasswordUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class RegisterServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("register.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String displayName = req.getParameter("displayName");
        UserDAO userDAO = new UserDAO();
        if (userDAO.getUserByUsername(username) != null) {
            req.setAttribute("error", "Username already exists");
            RequestDispatcher dispatcher = req.getRequestDispatcher("register.jsp");
            dispatcher.forward(req, resp);
            return;
        }
        String passwordHash = PasswordUtil.hashPassword(password);
        userDAO.registerUser(username, passwordHash, displayName);
        User user = userDAO.getUserByUsername(username);
        req.getSession().setAttribute("user", user);
        resp.sendRedirect(req.getContextPath() + "/home");
    }
} 