package Servlets;

import Bean.Message;
import Bean.Quiz;
import Bean.QuizAttempt;
import Bean.User;
import DB.FriendsDAO;
import DB.MessageDAO;
import DB.QuizDAO;
import DB.QuizAttemptDAO;
import DB.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.*;

@WebServlet("/messages")
public class MessagesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1) Ensure logged in
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }
        User me = (User) session.getAttribute("user");
        int myId = me.getUserId();

        // 2) Load messages
        MessageDAO msgDao = new MessageDAO();
        List<Message> messages = msgDao.getMessagesByReceiver(myId);

        // 3) Enrich with sender, quiz, best‐score for challenges
        UserDAO         userDao    = new UserDAO();
        QuizDAO         quizDao    = new QuizDAO();
        QuizAttemptDAO  atDao      = new QuizAttemptDAO();

        List<Map<String,Object>> inbox = new ArrayList<>();
        for (Message m : messages) {
            Map<String,Object> row = new HashMap<>();
            row.put("message", m);

            // sender info
            User sender = userDao.getUserById(m.getSenderId());
            row.put("sender", sender);

            if ("challenge".equals(m.getMessageType())) {
                // content holds quizId
                int quizId = Integer.parseInt(m.getContent());
                Quiz quiz = quizDao.getQuizById(quizId);
                row.put("quiz", quiz);

                // find sender's best on that quiz
                int best = 0;
                for (QuizAttempt a : atDao.getAttemptsByUser(sender.getUserId())) {
                    if (a.getQuizId() == quizId && a.getScore() > best) {
                        best = a.getScore();
                    }
                }
                row.put("bestScore", best);
            }

            inbox.add(row);
        }

        req.setAttribute("inbox", inbox);
        req.getRequestDispatcher("/messages.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // handle friend request accept/reject
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }
        User me = (User) session.getAttribute("user");

        int messageId = Integer.parseInt(req.getParameter("messageId"));
        int senderId  = Integer.parseInt(req.getParameter("senderId"));
        String action = req.getParameter("action");  // "accept" or "reject"

        MessageDAO msgDao = new MessageDAO();
        // mark read first
        msgDao.markAsRead(messageId);

        // if it was a friend_request, update the friends table
        Message m = msgDao.getMessageById(messageId);
        if ("friend_request".equals(m.getMessageType())) {
            FriendsDAO fdao = new FriendsDAO();
            if ("accept".equals(action)) {
                fdao.acceptFriendRequest(me.getUserId(), senderId);
            } else {
                fdao.rejectFriendRequest(me.getUserId(), senderId);
            }
        }

        resp.sendRedirect(req.getContextPath() + "/messages");
    }
}
