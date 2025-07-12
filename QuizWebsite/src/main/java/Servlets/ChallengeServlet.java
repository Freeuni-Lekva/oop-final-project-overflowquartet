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
        System.out.println("=== ChallengeServlet doGet called ===");
        
        // Validate user session
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        
        // Get and validate quizId parameter
        String quizIdParam = request.getParameter("quizId");
        if (quizIdParam == null || quizIdParam.trim().isEmpty()) {
            request.setAttribute("error", "Missing quiz ID");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        int quizId;
        try {
            quizId = Integer.parseInt(quizIdParam.trim());
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid quiz ID format");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        // Load quiz and validate it exists
        QuizDAO quizDAO = new QuizDAO();
        Quiz quiz = quizDAO.getQuizById(quizId);
        if (quiz == null) {
            request.setAttribute("error", "Quiz not found");
            request.getRequestDispatcher("/error.jsp").forward(request, response);
            return;
        }

        System.out.println("ChallengeServlet: Quiz found - ID: " + quiz.getQuizId() + ", Title: " + quiz.getTitle());

        // Load user's friends
        FriendsDAO friendsDAO = new FriendsDAO();
        List<Integer> friendIds = friendsDAO.getFriendIds(currentUser.getUserId(), FriendStatus.ACCEPTED);
        
        UserDAO userDAO = new UserDAO();
        List<User> friendList = new ArrayList<>();
        for (Integer friendId : friendIds) {
            User friend = userDAO.getUserById(friendId);
            if (friend != null) {
                friendList.add(friend);
            }
        }

        System.out.println("ChallengeServlet: Found " + friendList.size() + " friends for user " + currentUser.getUserId());

        // Set attributes for JSP
        request.setAttribute("quiz", quiz);
        request.setAttribute("friendList", friendList);
        request.setAttribute("quizId", quizId);
        
        request.getRequestDispatcher(CHALLENGE_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        System.out.println("=== ChallengeServlet doPost called ===");
        
        // Validate user session
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        // Get and validate parameters
        String action = request.getParameter("action");
        String friendIdParam = request.getParameter("friendId");
        String quizIdParam = request.getParameter("quizId");
        
        System.out.println("ChallengeServlet Debug:");
        System.out.println("  User ID: " + currentUser.getUserId());
        System.out.println("  Action: " + action);
        System.out.println("  Friend ID Param: " + friendIdParam);
        System.out.println("  Quiz ID Param: " + quizIdParam);
        
        // Validate action
        if (!"challenge".equals(action)) {
            System.out.println("ERROR: Invalid action: " + action);
            request.setAttribute("error", "Invalid action");
            forwardToChallengePage(request, response, quizIdParam);
            return;
        }
        
        // Validate required parameters
        if (friendIdParam == null || friendIdParam.trim().isEmpty() || 
            quizIdParam == null || quizIdParam.trim().isEmpty()) {
            System.out.println("ERROR: Missing required parameters");
            request.setAttribute("error", "Missing required parameters");
            forwardToChallengePage(request, response, quizIdParam);
            return;
        }

        // Parse and validate IDs
        int friendId, quizId;
        try {
            friendId = Integer.parseInt(friendIdParam.trim());
            quizId = Integer.parseInt(quizIdParam.trim());
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Invalid ID format - " + e.getMessage());
            request.setAttribute("error", "Invalid ID format");
            forwardToChallengePage(request, response, quizIdParam);
            return;
        }
        
        // Validate ID values
        if (friendId <= 0 || quizId <= 0) {
            System.out.println("ERROR: Invalid IDs - friendId: " + friendId + ", quizId: " + quizId);
            request.setAttribute("error", "Invalid IDs");
            forwardToChallengePage(request, response, quizIdParam);
            return;
        }

        // Validate that the quiz exists
        QuizDAO quizDAO = new QuizDAO();
        Quiz quiz = quizDAO.getQuizById(quizId);
        if (quiz == null) {
            System.out.println("ERROR: Quiz not found - ID: " + quizId);
            request.setAttribute("error", "Quiz not found");
            forwardToChallengePage(request, response, quizIdParam);
            return;
        }

        // Validate that the friend exists and is actually a friend
        UserDAO userDAO = new UserDAO();
        User friend = userDAO.getUserById(friendId);
        if (friend == null) {
            System.out.println("ERROR: Friend not found - ID: " + friendId);
            request.setAttribute("error", "Friend not found");
            forwardToChallengePage(request, response, quizIdParam);
            return;
        }

        FriendsDAO friendsDAO = new FriendsDAO();
        List<Integer> userFriends = friendsDAO.getFriendIds(currentUser.getUserId(), FriendStatus.ACCEPTED);
        if (!userFriends.contains(friendId)) {
            System.out.println("ERROR: User " + currentUser.getUserId() + " is not friends with " + friendId);
            request.setAttribute("error", "User is not your friend");
            forwardToChallengePage(request, response, quizIdParam);
            return;
        }
        
        // Test database connection
        try {
            DB.DBConnector.getConnection();
            System.out.println("Database connection test: SUCCESS");
        } catch (Exception e) {
            System.out.println("Database connection test: FAILED - " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Database connection failed");
            forwardToChallengePage(request, response, quizIdParam);
            return;
        }

        // Send the challenge message
        String content = "You've been challenged to take the quiz: " + quiz.getTitle();
        System.out.println("About to send challenge message:");
        System.out.println("  From User: " + currentUser.getUserId() + " (" + currentUser.getUsername() + ")");
        System.out.println("  To User: " + friendId + " (" + friend.getUsername() + ")");
        System.out.println("  Quiz: " + quizId + " (" + quiz.getTitle() + ")");
        System.out.println("  Content: " + content);
        
        boolean success = MessageDAO.sendChallengeMessage(currentUser.getUserId(), friendId, quizId, content);
        
        System.out.println("Challenge message sent: " + success);
        
        if (!success) {
            System.out.println("ERROR: Failed to send challenge message");
            request.setAttribute("error", "Failed to send challenge");
            forwardToChallengePage(request, response, quizIdParam);
            return;
        }

        // Success - redirect to messages page
        response.sendRedirect(request.getContextPath() + "/messages");
    }
    
    private void forwardToChallengePage(HttpServletRequest request, HttpServletResponse response, String quizId) throws ServletException, IOException {
        // Reload the challenge page with error
        if (quizId != null && !quizId.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/challenge?quizId=" + quizId);
        } else {
            response.sendRedirect(request.getContextPath() + "/quizzes");
        }
    }
}
