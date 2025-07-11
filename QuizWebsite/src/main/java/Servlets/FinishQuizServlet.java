package Servlets;

import Bean.AttemptAnswer;
import Bean.QuizAttempt;
import DB.AttemptAnswerDAO;
import DB.QuizAttemptDAO;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/finishQuiz")
public class FinishQuizServlet extends HttpServlet {
    private final AttemptAnswerDAO attemptAnswerDao = new AttemptAnswerDAO();
    private final QuizAttemptDAO    quizAttemptDao  = new QuizAttemptDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        int attemptId = (int) session.getAttribute("attemptId");
        long start    = (long) session.getAttribute("startTime");
        int duration  = (int) ((System.currentTimeMillis() - start) / 1000);

        // Load answers & compute score
        List<AttemptAnswer> answers = attemptAnswerDao.getAnswersForAttemptWithQuestions(attemptId);
        int score = (int) answers.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsCorrect()))
                .count();

        // Update attempt record
        QuizAttempt att = quizAttemptDao.getQuizAttemptById(attemptId);
        att.setScore(score);
        att.setDurationSeconds(duration);
        quizAttemptDao.updateQuizAttempt(att);

        // Check for achievements
        DB.AchievementsService achievementsService = new DB.AchievementsService();
        achievementsService.checkQuizAttemptAchievements(att.getUserId());
        achievementsService.checkHighScoreAchievement(att.getUserId(), att.getQuizId(), score);

        req.setAttribute("attempt", att);
        req.setAttribute("answers", answers);
        req.getRequestDispatcher("/results.jsp")
                .forward(req, resp);
    }
}
