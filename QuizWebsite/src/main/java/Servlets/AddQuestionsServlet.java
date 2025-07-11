package Servlets;

import Bean.Question;
import Bean.Choice;
import DB.QuestionDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import Bean.User;

@WebServlet("/add-questions")
public class AddQuestionsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String quizIdStr = req.getParameter("quizId");
        if (quizIdStr == null) {
            resp.sendRedirect("quizzes");
            return;
        }
        int quizId = Integer.parseInt(quizIdStr);
        User user = (User) req.getSession().getAttribute("user");
        DB.QuizDAO quizDAO = new DB.QuizDAO();
        Bean.Quiz quiz = quizDAO.getQuizById(quizId);
        if (quiz == null || user == null || quiz.getOwnerId() != user.getUserId()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not allowed to edit this quiz.");
            return;
        }
        QuestionDAO questionDAO = new QuestionDAO();
        List<Question> questions;
        try {
            questions = questionDAO.getQuestionsForQuiz(quizId, false);
        } catch (Exception e) {
            req.setAttribute("error", "Failed to load questions: " + e.getMessage());
            questions = java.util.Collections.emptyList();
        }
        req.setAttribute("quizId", quizId);
        req.setAttribute("questions", questions);
        req.getRequestDispatcher("/add_questions.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int quizId = Integer.parseInt(req.getParameter("quizId"));
        User user = (User) req.getSession().getAttribute("user");
        DB.QuizDAO quizDAO = new DB.QuizDAO();
        Bean.Quiz quiz = quizDAO.getQuizById(quizId);
        if (quiz == null || user == null || quiz.getOwnerId() != user.getUserId()) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not allowed to edit this quiz.");
            return;
        }
        String questionText = req.getParameter("questionText");
        String questionType = req.getParameter("questionType");

        Question question = new Question();
        question.setQuizId(quizId);
        question.setQuestionText(questionText);
        question.setQuestionType(questionType);
        question.setQuestionOrder(0);

        boolean valid = true;
        String error = null;

        if ("multiple_choice".equals(questionType)) {
            String[] choiceTexts = req.getParameterValues("choiceText");
            String[] corrects = req.getParameterValues("isCorrect");
            List<Choice> choices = new java.util.ArrayList<>();
            for (int i = 0; i < choiceTexts.length; i++) {
                boolean isCorrect = false;
                if (corrects != null) {
                    for (String c : corrects) {
                        if (Integer.parseInt(c) == i) isCorrect = true;
                    }
                }
                choices.add(new Choice(choiceTexts[i], isCorrect));
            }
            if (choices.isEmpty()) {
                valid = false;
                error = "Please provide at least one choice.";
            }
            question.setChoices(choices);
        } else if ("question_response".equals(questionType) || "fill_blank".equals(questionType) || "picture_response".equals(questionType)) {
            String answerText = req.getParameter("answerText");
            List<String> answers = new java.util.ArrayList<>();
            if (answerText != null) {
                for (String line : answerText.split("\\r?\\n")) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty()) answers.add(trimmed);
                }
            }
            if (answers.isEmpty()) {
                valid = false;
                error = "Please provide at least one answer.";
            }
            question.setAnswers(answers);
        }

        if (!valid) {
            req.setAttribute("error", error);
            // Reload questions for display
            QuestionDAO questionDAO = new QuestionDAO();
            List<Question> questions;
            try {
                questions = questionDAO.getQuestionsForQuiz(quizId, false);
            } catch (Exception e) {
                questions = java.util.Collections.emptyList();
            }
            req.setAttribute("quizId", quizId);
            req.setAttribute("questions", questions);
            req.getRequestDispatcher("/add_questions.jsp").forward(req, resp);
            return;
        }

        // Actually add the question
        QuestionDAO questionDAO = new QuestionDAO();
        try {
            questionDAO.addQuestion(question);
        } catch (Exception e) {
            req.setAttribute("error", "Failed to add question: " + e.getMessage());
            List<Question> questions;
            try {
                questions = questionDAO.getQuestionsForQuiz(quizId, false);
            } catch (Exception ex) {
                questions = java.util.Collections.emptyList();
            }
            req.setAttribute("quizId", quizId);
            req.setAttribute("questions", questions);
            req.getRequestDispatcher("/add_questions.jsp").forward(req, resp);
            return;
        }
        // Redirect to avoid form resubmission and show updated list
        resp.sendRedirect(req.getContextPath() + "/add-questions?quizId=" + quizId);
    }
}