package Servlets;

import Bean.Question;
import DAO.QuestionDAO;
import DB.DBConnector;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/question")
public class QuestionServlet extends HttpServlet {
    private QuestionDAO questionDAO;

    @Override
    public void init() throws ServletException {
        Connection conn = DBConnector.getConnection();
        questionDAO = new QuestionDAO(conn);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String questionIdStr = req.getParameter("id");
        if (questionIdStr != null) {
            try {
                int questionId = Integer.parseInt(questionIdStr);
                Question question = questionDAO.findById(questionId);
                req.setAttribute("question", question);
            } catch (Exception e) {
                req.setAttribute("error", "Failed to load question: " + e.getMessage());
            }
        }
        req.getRequestDispatcher("question-edit.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String questionIdStr = req.getParameter("id");
        String quizIdStr = req.getParameter("quizId");
        String type = req.getParameter("type");
        String prompt = req.getParameter("prompt");
        String imageUrl = req.getParameter("imageUrl");
        int order = Integer.parseInt(req.getParameter("order"));
        try {
            if (questionIdStr == null || questionIdStr.isEmpty()) {
                // Create new question
                Question question = new Question();
                question.setQuizId(Integer.parseInt(quizIdStr));
                question.setType(type);
                question.setPrompt(prompt);
                question.setImageUrl(imageUrl);
                question.setOrder(order);
                questionDAO.createQuestion(question);
            } else {

                int questionId = Integer.parseInt(questionIdStr);
                Question question = questionDAO.findById(questionId);
                question.setType(type);
                question.setPrompt(prompt);
                question.setImageUrl(imageUrl);
                question.setOrder(order);
                questionDAO.updateQuestion(question);
            }
            resp.sendRedirect("quiz-edit.jsp?id=" + quizIdStr);
        } catch (Exception e) {
            req.setAttribute("error", "Failed to save question: " + e.getMessage());
            req.getRequestDispatcher("question-edit.jsp").forward(req, resp);
        }
    }
} 