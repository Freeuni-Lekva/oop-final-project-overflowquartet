package Servlets;

import Bean.Quiz;
import Bean.User;
import DB.FriendsDAO;
import DB.FriendsDAO.FriendStatus;
import DB.MessageDAO;
import DB.QuizDAO;
import DB.UserDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/challenge")
public class ChallengeServlet extends HttpServlet {

    private static final String CHALLENGE_JSP = "/challenge.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String quizParam = request.getParameter("quizId");
        if (quizParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing quizId");
            return;
        }

        int quizId;
        try {
            quizId = Integer.parseInt(quizParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid quizId");
            return;
        }

        QuizDAO quizDAO = new QuizDAO();
        Quiz quiz = quizDAO.getQuizById(quizId);
        if (quiz == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Quiz not found");
            return;
        }

        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not logged in");
            return;
        }
        int currentUserId = currentUser.getUserId();

        FriendsDAO friendDAO = new FriendsDAO();
        List<Integer> friendIds = friendDAO.getFriendIds(currentUserId, FriendStatus.ACCEPTED);

        UserDAO userDAO = new UserDAO();
        List<User> friendList = new ArrayList<>();
        for (Integer fid : friendIds) {
            User f = userDAO.getUserById(fid);
            if (f != null) friendList.add(f);
        }

        request.setAttribute("quiz", quiz);
        request.setAttribute("friendList", friendList);
        request.getRequestDispatcher(CHALLENGE_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not logged in");
            return;
        }
        int fromUserId = currentUser.getUserId();

        String friendParam = request.getParameter("friendId");
        String quizParam = request.getParameter("quizId");
        if (friendParam == null || quizParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
            return;
        }

        int toUserId, quizId;
        try {
            toUserId = Integer.parseInt(friendParam);
            quizId = Integer.parseInt(quizParam);
        } catch (NumberFormatException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters");
            return;
        }

        String content = "You've been challenged to take a quiz!";
        MessageDAO.sendChallengeMessage(fromUserId, toUserId, quizId, content);

        String xhr = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(xhr)) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            response.sendRedirect(request.getContextPath() + "/messages");
        }
    }
}
