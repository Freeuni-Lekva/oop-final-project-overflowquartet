package Servlets;

import Bean.User;
import Bean.Quiz;
import Bean.Question;
import DAO.QuizDAO;
import DAO.QuestionDAO;
import DB.DBConnector;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;

@WebServlet("/quiz-edit")
public class QuizEditServlet extends HttpServlet {
    private QuizDAO quizDAO;
    private QuestionDAO questionDAO;

    @Override
    public void init() throws ServletException {
        Connection conn = DBConnector.getConnection();
        quizDAO = new QuizDAO(conn);
        questionDAO = new QuestionDAO(conn);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String quizIdStr = req.getParameter("id");
        if (quizIdStr != null) {
            try {
                int quizId = Integer.parseInt(quizIdStr);
                Quiz quiz = quizDAO.findById(quizId);
                List<Question> questions = questionDAO.findByQuizId(quizId);
                req.setAttribute("quiz", quiz);
                req.setAttribute("questions", questions);
            } catch (Exception e) {
                req.setAttribute("error", "Failed to load quiz for editing: " + e.getMessage());
            }
        }
        req.getRequestDispatcher("quiz-edit.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect("login.jsp");
            return;
        }
        String quizIdStr = req.getParameter("id");
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        boolean randomOrder = req.getParameter("randomOrder") != null;
        boolean multiplePages = req.getParameter("multiplePages") != null;
        boolean immediateCorrection = req.getParameter("immediateCorrection") != null;
        boolean practiceMode = req.getParameter("practiceMode") != null;
        try {
            if (quizIdStr == null || quizIdStr.isEmpty()) {

                Quiz quiz = new Quiz();
                quiz.setOwnerId(user.getUserId());
                quiz.setTitle(title);
                quiz.setDescription(description);
                quiz.setRandomOrder(randomOrder);
                quiz.setMultiplePages(multiplePages);
                quiz.setImmediateCorrection(immediateCorrection);
                quiz.setPracticeMode(practiceMode);
                quiz.setCreationDate(new Timestamp(System.currentTimeMillis()));
                quizDAO.createQuiz(quiz);

                resp.sendRedirect("quiz.jsp?id=" + quiz.getQuizId());
            } else {

                int quizId = Integer.parseInt(quizIdStr);
                Quiz quiz = quizDAO.findById(quizId);
                quiz.setTitle(title);
                quiz.setDescription(description);
                quiz.setRandomOrder(randomOrder);
                quiz.setMultiplePages(multiplePages);
                quiz.setImmediateCorrection(immediateCorrection);
                quiz.setPracticeMode(practiceMode);
                quizDAO.updateQuiz(quiz);
                resp.sendRedirect("quiz.jsp?id=" + quizId);
            }
        } catch (Exception e) {
            req.setAttribute("error", "Failed to save quiz: " + e.getMessage());
            req.getRequestDispatcher("quiz-edit.jsp").forward(req, resp);
        }
    }
} 