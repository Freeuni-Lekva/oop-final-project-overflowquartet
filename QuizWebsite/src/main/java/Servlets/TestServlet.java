package Servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/test")
public class TestServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        
        try {
            // Test database connection
            Connection conn = DB.DBConnector.getConnection();
            if (conn != null && !conn.isClosed()) {
                response.getWriter().write("Database connection: OK\n");
                conn.close();
            } else {
                response.getWriter().write("Database connection: FAILED\n");
            }
            
            // Test if we can reach this servlet
            response.getWriter().write("TestServlet: OK\n");
            response.getWriter().write("Request URI: " + request.getRequestURI() + "\n");
            response.getWriter().write("Context Path: " + request.getContextPath() + "\n");
            
            // Test quiz retrieval
            String quizIdParam = request.getParameter("quizId");
            if (quizIdParam != null) {
                try {
                    int quizId = Integer.parseInt(quizIdParam);
                    DB.QuizDAO quizDAO = new DB.QuizDAO();
                    Bean.Quiz quiz = quizDAO.getQuizById(quizId);
                    if (quiz != null) {
                        response.getWriter().write("Quiz found: ID=" + quiz.getQuizId() + ", Title=" + quiz.getTitle() + "\n");
                    } else {
                        response.getWriter().write("Quiz not found for ID: " + quizId + "\n");
                    }
                } catch (NumberFormatException e) {
                    response.getWriter().write("Invalid quiz ID: " + quizIdParam + "\n");
                }
            }
            
        } catch (Exception e) {
            response.getWriter().write("Error: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        
        response.getWriter().write("TestServlet POST: OK\n");
        response.getWriter().write("Request method: " + request.getMethod() + "\n");
        response.getWriter().write("Content-Type: " + request.getContentType() + "\n");
        
        // Echo back the parameters
        String action = request.getParameter("action");
        String quizId = request.getParameter("quizId");
        String friendId = request.getParameter("friendId");
        
        response.getWriter().write("action: " + action + "\n");
        response.getWriter().write("quizId: " + quizId + "\n");
        response.getWriter().write("friendId: " + friendId + "\n");
    }
} 