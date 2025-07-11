package Servlets;

import Bean.AttemptAnswer;
import Bean.Question;
import DB.AttemptAnswerDAO;
import DB.QuestionAnswerDAO;
import DB.QuestionChoiceDAO;

import DB.QuestionDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Enumeration;

@WebServlet("/submitAnswer")
public class SubmitAnswerServlet extends HttpServlet {
    private final AttemptAnswerDAO attemptAnswerDao = new AttemptAnswerDAO();
    private final QuestionChoiceDAO choiceDao        = new QuestionChoiceDAO();
    private final QuestionAnswerDAO answerDao        = new QuestionAnswerDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        int attemptId = (int) session.getAttribute("attemptId");
        boolean multiPage = (boolean) session.getAttribute("multiplePages");
        boolean immediate = (boolean) session.getAttribute("immediateCorrection");

        System.out.println("DEBUG: SubmitAnswerServlet called");
        System.out.println("DEBUG: multiPage = " + multiPage);
        System.out.println("DEBUG: immediate = " + immediate);

        if (multiPage) {
            // Multi-page mode
            handleMultiPageSubmission(req, resp, session, attemptId, immediate);
        } else {
            // Single-page mode
            handleSinglePageSubmission(req, resp, session, attemptId);
        }
    }

    private void handleMultiPageSubmission(HttpServletRequest req, HttpServletResponse resp, 
                                         HttpSession session, int attemptId, boolean immediate) 
            throws ServletException, IOException {
        
        String questionIdStr = req.getParameter("questionId");
        if (questionIdStr == null || questionIdStr.trim().isEmpty()) {
            System.err.println("ERROR: questionId parameter is null or empty");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing questionId parameter");
            return;
        }

        int questionId;
        try {
            questionId = Integer.parseInt(questionIdStr);
        } catch (NumberFormatException e) {
            System.err.println("ERROR: Invalid questionId format: " + questionIdStr);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid questionId format");
            return;
        }

        System.out.println("DEBUG: Processing questionId = " + questionId);

        String choiceParam = req.getParameter("answer");
        String textParam   = req.getParameter("answerText");

        // Determine correctness
        Boolean isCorrect = null;
        String userText;
        if (choiceParam != null && !choiceParam.trim().isEmpty()) {
            // multiple‐choice
            try {
                int choiceId = Integer.parseInt(choiceParam);
                isCorrect = choiceDao.getChoiceById(choiceId).isCorrect();
                userText  = choiceDao.getChoiceById(choiceId).getChoiceText();
                System.out.println("DEBUG: Multiple choice answer - choiceId = " + choiceId + ", isCorrect = " + isCorrect);
            } catch (NumberFormatException e) {
                System.err.println("ERROR: Invalid choiceId format: " + choiceParam);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid choice format");
                return;
            }
        } else if (textParam != null && !textParam.trim().isEmpty()) {
            userText = textParam.trim();
            List<String> legal = answerDao.getAnswersForQuestion(questionId);
            isCorrect = legal.stream()
                    .anyMatch(a -> a.equalsIgnoreCase(userText));
            System.out.println("DEBUG: Text answer - userText = '" + userText + "', isCorrect = " + isCorrect);
        } else {
            System.err.println("ERROR: No answer provided");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No answer provided");
            return;
        }

        // Check if this answer was already submitted (to prevent duplicates in immediate mode)
        List<AttemptAnswer> existingAnswers = attemptAnswerDao.getAnswersForAttempt(attemptId);
        boolean alreadyAnswered = existingAnswers.stream()
                .anyMatch(a -> a.getQuestionId() == questionId);
        
        if (alreadyAnswered) {
            System.out.println("DEBUG: Answer already submitted for question " + questionId + ", checking if it's the last question");
            // If already answered, check if this is the last question and finish
            String pageStr = req.getParameter("page");
            String totalStr = req.getParameter("total");
            
            if (pageStr != null && totalStr != null) {
                try {
                    int page = Integer.parseInt(pageStr);
                    int total = Integer.parseInt(totalStr);
                    
                    if (page >= total) {
                        // This is the last question, finish the quiz
                        resp.sendRedirect(req.getContextPath() + "/finishQuiz");
                        return;
                    } else {
                        // Not the last question, go to next
                        resp.sendRedirect(req.getContextPath() + "/showQuestion?page=" + (page + 1));
                        return;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("ERROR: Invalid page or total format");
                }
            }
        }
        
        // Persist it
        AttemptAnswer aa = new AttemptAnswer(attemptId, questionId, userText, isCorrect);
        boolean saved = attemptAnswerDao.addAttemptAnswer(aa);
        System.out.println("DEBUG: Answer saved = " + saved);

        if (immediate) {
            // show feedback on same page
            req.setAttribute("feedbackCorrect", isCorrect);
            req.setAttribute("userAnswer", userText);
            req.setAttribute("showNext", true); // <-- add this flag
            // reload the same question with page info
            try {
                req.setAttribute("question",
                        new QuestionDAO().getQuestion(questionId));
                // Set page and total attributes for the JSP
                String currentPageStr = req.getParameter("page");
                String currentTotalStr = req.getParameter("total");
                if (currentPageStr != null && currentTotalStr != null) {
                    req.setAttribute("page", Integer.parseInt(currentPageStr));
                    req.setAttribute("total", Integer.parseInt(currentTotalStr));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            req.getRequestDispatcher("/question.jsp")
                    .forward(req, resp);
            return; // prevent further processing
        } else {
            // go to next page or finish
            String pageStr = req.getParameter("page");
            String totalStr = req.getParameter("total");
            
            if (pageStr == null || totalStr == null) {
                System.err.println("ERROR: Missing page or total parameters");
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing page information");
                return;
            }
            
            try {
                int page = Integer.parseInt(pageStr);
                int total = Integer.parseInt(totalStr);
                
                if (page < total) {
                    resp.sendRedirect(req.getContextPath()
                            + "/showQuestion?page=" + (page + 1));
                } else {
                    resp.sendRedirect(req.getContextPath() + "/finishQuiz");
                }
            } catch (NumberFormatException e) {
                System.err.println("ERROR: Invalid page or total format");
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid page information");
                return;
            }
        }
    }

    private void handleSinglePageSubmission(HttpServletRequest req, HttpServletResponse resp, 
                                          HttpSession session, int attemptId) 
            throws ServletException, IOException {
        
        System.out.println("DEBUG: Processing single-page submission");
        
        // Get all parameters to find question IDs
        Enumeration<String> paramNames = req.getParameterNames();
        boolean hasAnswers = false;
        
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            System.out.println("DEBUG: Parameter: " + paramName + " = " + req.getParameter(paramName));
            
            if (paramName.startsWith("answerText_")) {
                // Text answer
                String questionIdStr = paramName.substring("answerText_".length());
                String answerText = req.getParameter(paramName);
                
                if (answerText != null && !answerText.trim().isEmpty()) {
                    try {
                        int questionId = Integer.parseInt(questionIdStr);
                        processTextAnswer(attemptId, questionId, answerText.trim());
                        hasAnswers = true;
                    } catch (NumberFormatException e) {
                        System.err.println("ERROR: Invalid questionId in parameter: " + paramName);
                    }
                }
            } else if (paramName.startsWith("answer_")) {
                // Multiple choice answer
                String questionIdStr = paramName.substring("answer_".length());
                String choiceIdStr = req.getParameter(paramName);
                
                if (choiceIdStr != null && !choiceIdStr.trim().isEmpty()) {
                    try {
                        int questionId = Integer.parseInt(questionIdStr);
                        int choiceId = Integer.parseInt(choiceIdStr);
                        processChoiceAnswer(attemptId, questionId, choiceId);
                        hasAnswers = true;
                    } catch (NumberFormatException e) {
                        System.err.println("ERROR: Invalid questionId or choiceId in parameter: " + paramName);
                    }
                }
            }
        }
        
        if (!hasAnswers) {
            System.err.println("ERROR: No answers provided in single-page submission");
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No answers provided");
            return;
        }
        
        // Redirect to finish quiz
        resp.sendRedirect(req.getContextPath() + "/finishQuiz");
    }

    private void processTextAnswer(int attemptId, int questionId, String answerText) {
        System.out.println("DEBUG: Processing text answer for question " + questionId + ": '" + answerText + "'");
        
        List<String> legal = answerDao.getAnswersForQuestion(questionId);
        Boolean isCorrect = legal.stream()
                .anyMatch(a -> a.equalsIgnoreCase(answerText));
        
        AttemptAnswer aa = new AttemptAnswer(attemptId, questionId, answerText, isCorrect);
        boolean saved = attemptAnswerDao.addAttemptAnswer(aa);
        System.out.println("DEBUG: Text answer saved = " + saved + ", isCorrect = " + isCorrect);
    }

    private void processChoiceAnswer(int attemptId, int questionId, int choiceId) {
        System.out.println("DEBUG: Processing choice answer for question " + questionId + ": choiceId = " + choiceId);
        
        Boolean isCorrect = choiceDao.getChoiceById(choiceId).isCorrect();
        String userText = choiceDao.getChoiceById(choiceId).getChoiceText();
        
        AttemptAnswer aa = new AttemptAnswer(attemptId, questionId, userText, isCorrect);
        boolean saved = attemptAnswerDao.addAttemptAnswer(aa);
        System.out.println("DEBUG: Choice answer saved = " + saved + ", isCorrect = " + isCorrect);
    }
}
