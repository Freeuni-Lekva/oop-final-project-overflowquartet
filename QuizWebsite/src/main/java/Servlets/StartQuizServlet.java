package Servlets;

import Bean.Quiz;
import Bean.QuizAttempt;
import Bean.User;
import DB.QuizAttemptDAO;
import DB.QuizDAO;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;


@WebServlet("/startQuiz")
public class StartQuizServlet extends HttpServlet {
    private final QuizDAO quizDao = new QuizDAO();
    private final QuizAttemptDAO attemptDao = new QuizAttemptDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        System.out.println("DEBUG: StartQuizServlet called");
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            System.out.println("DEBUG: No user found, redirecting to login");
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        String quizIdStr = req.getParameter("quizId");
        System.out.println("DEBUG: quizId parameter: " + quizIdStr);
        
        if (quizIdStr == null || quizIdStr.trim().isEmpty()) {
            System.out.println("DEBUG: No quizId provided");
            resp.sendRedirect(req.getContextPath() + "/quizzes");
            return;
        }
        
        int quizId;
        try {
            quizId = Integer.parseInt(quizIdStr);
        } catch (NumberFormatException e) {
            System.out.println("DEBUG: Invalid quizId format: " + quizIdStr);
            resp.sendRedirect(req.getContextPath() + "/quizzes");
            return;
        }
        
        Quiz quiz = quizDao.getQuizById(quizId);
        System.out.println("DEBUG: Quiz found: " + (quiz != null ? quiz.getTitle() : "null"));
        
        if (quiz == null) {
            System.out.println("DEBUG: Quiz not found with ID: " + quizId);
            resp.sendRedirect(req.getContextPath() + "/quizzes");
            return;
        }

        // Check if quiz has questions
        try {
            DB.QuestionDAO questionDao = new DB.QuestionDAO();
            java.util.List<Bean.Question> questions = questionDao.getQuestionsForQuiz(quizId, false);
            if (questions == null || questions.isEmpty()) {
                System.out.println("DEBUG: Quiz has no questions, redirecting to add questions");
                resp.sendRedirect(req.getContextPath() + "/add-questions?quizId=" + quizId);
                return;
            }
        } catch (Exception e) {
            System.err.println("ERROR: Failed to check questions for quiz " + quizId + ": " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/quizzes");
            return;
        }

        // 1) Create attempt record (score and duration initialized to 0)
        QuizAttempt attempt = new QuizAttempt(currentUser.getUserId(),
                quizId, 0, 0);
        attemptDao.createQuizAttempt(attempt);

        // 2) Seed session with quiz state
        session.setAttribute("attemptId", attempt.getAttemptId());
        session.setAttribute("quizId", quizId);
        session.setAttribute("randomOrder", quiz.isRandomOrder());
        session.setAttribute("multiplePages", quiz.isMultiplePages());
        session.setAttribute("immediateCorrection", quiz.isImmediateCorrection());
        session.setAttribute("startTime", System.currentTimeMillis());

        // 3) Kick off at page 1
        String redirectUrl = req.getContextPath() + "/showQuestion?page=1";
        System.out.println("DEBUG: Redirecting to: " + redirectUrl);
        resp.sendRedirect(redirectUrl);
    }
}
