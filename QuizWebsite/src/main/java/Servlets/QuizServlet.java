package Servlets;

import Bean.User;
import Bean.Quiz;
import Bean.Question;
import Bean.QuizResult;
import DAO.QuizDAO;
import DAO.QuestionDAO;
import DAO.AnswerDAO;
import DAO.QuizResultDAO;
import DB.DBConnector;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet("/quiz")
public class QuizServlet extends HttpServlet {
    private QuizDAO quizDAO;
    private QuestionDAO questionDAO;
    private AnswerDAO answerDAO;
    private QuizResultDAO quizResultDAO;

    @Override
    public void init() throws ServletException {
        Connection conn = DBConnector.getConnection();
        quizDAO = new QuizDAO(conn);
        questionDAO = new QuestionDAO(conn);
        answerDAO = new AnswerDAO(conn);
        quizResultDAO = new QuizResultDAO(conn);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String quizIdStr = req.getParameter("id");
        if (quizIdStr == null) {
            resp.sendRedirect("index.jsp");
            return;
        }
        try {
            int quizId = Integer.parseInt(quizIdStr);
            Quiz quiz = quizDAO.findById(quizId);
            List<Question> questions = questionDAO.findByQuizId(quizId);
            req.setAttribute("quiz", quiz);
            req.setAttribute("questions", questions);
            req.getRequestDispatcher("quiz.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Failed to load quiz: " + e.getMessage());
            req.getRequestDispatcher("index.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Handle quiz submission and grading
        String quizIdStr = req.getParameter("id");
        User user = (User) req.getSession().getAttribute("user");
        if (quizIdStr == null || user == null) {
            resp.sendRedirect("index.jsp");
            return;
        }
        try {
            int quizId = Integer.parseInt(quizIdStr);
            List<Question> questions = questionDAO.findByQuizId(quizId);
            int score = 0;
            for (Question q : questions) {
                String userAnswer = req.getParameter("q_" + q.getQuestionId());

                if (userAnswer != null && !userAnswer.trim().isEmpty()) {
                    score++;
                }
            }
            QuizResult result = new QuizResult();
            result.setUserId(user.getUserId());
            result.setQuizId(quizId);
            result.setScore(score);
            result.setTotalQuestions(questions.size());
            result.setTimeTaken(0);
            result.setTakenAt(new java.sql.Timestamp(System.currentTimeMillis()));
            quizResultDAO.createQuizResult(result);
            req.setAttribute("result", result);
            req.getRequestDispatcher("result.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Failed to submit quiz: " + e.getMessage());
            req.getRequestDispatcher("index.jsp").forward(req, resp);
        }
    }
} 