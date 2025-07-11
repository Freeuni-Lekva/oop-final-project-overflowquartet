//package Servlets;
//
//import Bean.QuizAttempt;
//import Bean.User;
//import DB.QuizAttemptDAO;
//import DB.QuizDAO;
//import Bean.Quiz;
//



//@WebServlet("/history")
//public class HistoryServlet extends HttpServlet {
//    private QuizAttemptDAO attemptDAO = new QuizAttemptDAO();
//    private QuizDAO quizDAO = new QuizDAO();
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        HttpSession session = request.getSession();
//        User currentUser = (User) session.getAttribute("user");
//        if (currentUser == null) {
//            response.sendRedirect("index.jsp");
//            return;
//        }
//        int userId = currentUser.getUserId();
//        List<QuizAttempt> attempts = attemptDAO.getAttemptsByUser(userId);
//        // Attach quiz titles to attempts
//        List<AttemptWithQuiz> attemptsWithQuiz = new ArrayList<>();
//        for (QuizAttempt a : attempts) {
//            Quiz quiz = quizDAO.getQuizById(a.getQuizId());
//            attemptsWithQuiz.add(new AttemptWithQuiz(a, quiz));
//        }
//        request.setAttribute("attemptsWithQuiz", attemptsWithQuiz);
//        request.getRequestDispatcher("/history.jsp").forward(request, response);
//    }
//
//    public static class AttemptWithQuiz {
//        public QuizAttempt attempt;
//        public Quiz quiz;
//        public AttemptWithQuiz(QuizAttempt attempt, Quiz quiz) {
//            this.attempt = attempt;
//            this.quiz = quiz;
//        }
//    }
//}
package Servlets;

import Bean.Quiz;
import Bean.QuizAttempt;
import Bean.User;
import DB.QuizAttemptDAO;
import DB.QuizDAO;
import jakarta.servlet.http.HttpServlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shows the full history of quiz attempts for the current user.
 */
@WebServlet("/history")
public class HistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1) check login
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            // not logged in → bounce to login page
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        User user = (User) session.getAttribute("user");

        // 2) fetch all attempts
        QuizAttemptDAO attemptDao = new QuizAttemptDAO();
        List<QuizAttempt> attempts = attemptDao.getAttemptsByUser(user.getUserId());

        // 3) for each attempt, look up the Quiz bean
        QuizDAO quizDao = new QuizDAO();
        List<Map<String,Object>> historyList = new ArrayList<>();
        for (QuizAttempt at : attempts) {
            Map<String,Object> row = new HashMap<>();
            row.put("attempt", at);
            Quiz q = quizDao.getQuizById(at.getQuizId());
            row.put("quiz", q);
            historyList.add(row);
        }

        // 4) forward into JSP
        request.setAttribute("historyList", historyList);
        request.getRequestDispatcher("/history.jsp").forward(request, response);
    }
}
