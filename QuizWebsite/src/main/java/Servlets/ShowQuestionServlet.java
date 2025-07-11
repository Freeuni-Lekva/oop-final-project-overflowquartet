package Servlets;

import Bean.Question;
import DB.QuestionDAO;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/showQuestion")
public class ShowQuestionServlet extends HttpServlet {
    private final QuestionDAO questionDao = new QuestionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        System.out.println("DEBUG: ShowQuestionServlet called");
        HttpSession session = req.getSession();
        int quizId = (int) session.getAttribute("quizId");
        System.out.println("DEBUG: quizId from session: " + quizId);
        boolean random    = (boolean) session.getAttribute("randomOrder");
        System.out.println("DEBUG: random from session: " + random);
        boolean multiPage = (boolean) session.getAttribute("multiplePages");
        System.out.println("DEBUG: multiPage from session: " + multiPage);

        // Load all questions once
        @SuppressWarnings("unchecked")
        List<Question> all = null;
        try {
            all = questionDao.getQuestionsForQuiz(quizId, random);
            System.out.println("DEBUG: Loaded " + (all != null ? all.size() : 0) + " questions for quiz " + quizId);
            if (all != null && !all.isEmpty()) {
                System.out.println("DEBUG: First question: " + all.get(0).getQuestionText());
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to load questions for quiz " + quizId + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        if (all == null || all.isEmpty()) {
            System.out.println("DEBUG: No questions found for quiz " + quizId);
            req.setAttribute("error", "This quiz has no questions yet. Please add some questions before starting the quiz.");
            req.getRequestDispatcher("/question.jsp").forward(req, resp);
            return;
        }

        if (multiPage) {
            // pick out the single question for this page
            int page = Integer.parseInt(req.getParameter("page"));
            Question q = all.get(page - 1);
            req.setAttribute("question", q);
            req.setAttribute("page", page);
            req.setAttribute("total", all.size());
            System.out.println("DEBUG: Multi-page mode - page " + page + " of " + all.size());
            System.out.println("DEBUG: Question text: " + q.getQuestionText());
        } else {
            // one big form
            req.setAttribute("questions", all);
            System.out.println("DEBUG: Single-page mode - " + all.size() + " questions");
        }

        System.out.println("DEBUG: Forwarding to question.jsp");
        System.out.println("DEBUG: Request attributes:");
        System.out.println("  - question: " + req.getAttribute("question"));
        System.out.println("  - questions: " + req.getAttribute("questions"));
        System.out.println("  - error: " + req.getAttribute("error"));
        System.out.println("  - page: " + req.getAttribute("page"));
        System.out.println("  - total: " + req.getAttribute("total"));
        
        req.getRequestDispatcher("/question.jsp")
                .forward(req, resp);
    }
}
