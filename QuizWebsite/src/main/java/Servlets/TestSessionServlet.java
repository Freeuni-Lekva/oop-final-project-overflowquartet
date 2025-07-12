package Servlets;

import Bean.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/test-session")
public class TestSessionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("=== TestSessionServlet doGet called ===");
        
        HttpSession session = request.getSession(false);
        System.out.println("Session exists: " + (session != null));
        
        if (session != null) {
            User user = (User) session.getAttribute("user");
            System.out.println("User in session: " + (user != null));
            if (user != null) {
                System.out.println("User ID: " + user.getUserId());
                System.out.println("Username: " + user.getUsername());
            }
        }
        
        response.setContentType("text/plain");
        response.getWriter().write("Session test completed. Check server logs.");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("=== TestSessionServlet doPost called ===");
        
        HttpSession session = request.getSession(false);
        System.out.println("Session exists: " + (session != null));
        
        if (session != null) {
            User user = (User) session.getAttribute("user");
            System.out.println("User in session: " + (user != null));
            if (user != null) {
                System.out.println("User ID: " + user.getUserId());
                System.out.println("Username: " + user.getUsername());
            }
        }
        
        response.setContentType("application/json");
        response.getWriter().write("{\"success\": true, \"message\": \"Session test completed\"}");
    }
} 