package Servlets;

import Bean.Question;
import DB.QuestionDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/test")
public class TestServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().println("<html><body>");
        resp.getWriter().println("<h1>Test Servlet</h1>");
        
        try {
            QuestionDAO questionDao = new QuestionDAO();
            
            // Test with quiz ID 33 (which we know has questions)
            List<Question> questions = questionDao.getQuestionsForQuiz(33, false);
            
            resp.getWriter().println("<p>Found " + questions.size() + " questions for quiz 33</p>");
            
            for (Question q : questions) {
                resp.getWriter().println("<p>Question ID: " + q.getQuestionId() + "</p>");
                resp.getWriter().println("<p>Question Text: " + q.getQuestionText() + "</p>");
                resp.getWriter().println("<p>Question Type: " + q.getQuestionType() + "</p>");
                if (q.getAnswers() != null) {
                    resp.getWriter().println("<p>Answers: " + q.getAnswers() + "</p>");
                }
                resp.getWriter().println("<hr>");
            }
            
        } catch (SQLException e) {
            resp.getWriter().println("<p>Error: " + e.getMessage() + "</p>");
            e.printStackTrace();
        }
        
        resp.getWriter().println("</body></html>");
    }
} 