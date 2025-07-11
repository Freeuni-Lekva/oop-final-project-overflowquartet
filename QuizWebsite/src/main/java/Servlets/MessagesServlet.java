package Servlets;

import Bean.Message;
import Bean.User;
import Bean.Quiz;
import Bean.QuizAttempt;
import DB.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.*;

@WebServlet("/messages")
public class MessagesServlet extends HttpServlet {
    private final FriendsDAO friendsDAO = new FriendsDAO();
    private final UserDAO userDAO       = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        User me   = (User) session.getAttribute("user");
        int  myId = me.getUserId();

        // 1) load all messages the user has received
        MessageDAO      msgDao    = new MessageDAO();
        List<Message>   messages  = msgDao.getMessagesByReceiver(myId);

        // 2) split them up by type
        List<Map<String,Object>> friendRequests = new ArrayList<>();
        List<Map<String,Object>> challenges     = new ArrayList<>();
        List<Map<String,Object>> notes          = new ArrayList<>();

        QuizDAO        quizDao    = new QuizDAO();
        QuizAttemptDAO attemptDao = new QuizAttemptDAO();

        for (Message m : messages) {
            Map<String,Object> row = new HashMap<>();
            row.put("message", m);
            User sender = userDAO.getUserById(m.getSenderId());
            row.put("sender", sender);

            switch (m.getMessageType()) {
                case "friend_request" -> friendRequests.add(row);
                case "challenge" -> {
                    try {
                        int quizId = Integer.parseInt(m.getContent());
                        row.put("quiz", quizDao.getQuizById(quizId));
                        // find sender’s best score
                        int best = 0;
                        for (QuizAttempt a : attemptDao.getAttemptsByUser(sender.getUserId())) {
                            if (a.getQuizId() == quizId && a.getScore() > best) {
                                best = a.getScore();
                            }
                        }
                        row.put("bestScore", best);
                    } catch (NumberFormatException ignored) {
                        row.put("bestScore", 0);
                    }
                    challenges.add(row);
                }
                case "note" -> notes.add(row);
            }
        }

        // 3) fallback: any pending friend-requests in the FriendsDAO?
        List<Integer> pendingIds = friendsDAO.getPendingRequestsForUser(myId);
        List<User>    pendingReceived = new ArrayList<>();
        for (int fromId : pendingIds) {
            User u = userDAO.getUserById(fromId);
            if (u == null) u = new User();  // fallback
            u.setUserId(fromId);
            u.setUsername(u.getUsername() != null ? u.getUsername() : "User#" + fromId);
            pendingReceived.add(u);
        }

        // 4) push everything into request
        req.setAttribute("friendRequests",  friendRequests);
        req.setAttribute("challenges",      challenges);
        req.setAttribute("notes",           notes);
        req.setAttribute("pendingReceived", pendingReceived);

        req.getRequestDispatcher("/messages.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        int         userId  = ((User) session.getAttribute("user")).getUserId();
        String      action  = req.getParameter("action");
        String      midStr  = req.getParameter("messageId");
        String      sidStr  = req.getParameter("senderId");

        MessageDAO  msgDao  = new MessageDAO();

        int senderId;
        try {
            senderId = Integer.parseInt(sidStr);
        } catch (NumberFormatException e) {
            resp.sendRedirect("messages");
            return;
        }

        // Accept / reject
        if ("accept".equals(action)) {
            friendsDAO.acceptFriendRequest(userId, senderId);
        } else if ("reject".equals(action)) {
            friendsDAO.rejectFriendRequest(userId, senderId);
        }

        // If there *was* a messageId, mark it read
        if (midStr != null) {
            try {
                int messageId = Integer.parseInt(midStr);
                msgDao.markAsRead(messageId);
            } catch (NumberFormatException ignored) {}
        }

        resp.sendRedirect("messages");
    }
}