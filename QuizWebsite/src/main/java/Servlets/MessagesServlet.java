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
        
        // Mark all unread messages as read when visiting the messages page
        for (Message message : messages) {
            if (!message.isRead()) {
                msgDao.markAsRead(message.getMessageId());
                message.setRead(true); // Update the local object as well
            }
        }

        // 2) split them up by type
        List<Map<String,Object>> friendRequests = new ArrayList<>();
        List<Map<String,Object>> challenges     = new ArrayList<>();
        List<Map<String,Object>> notes          = new ArrayList<>();

        QuizDAO        quizDao    = new QuizDAO();
        QuizAttemptDAO attemptDao = new QuizAttemptDAO();

        for (Message m : messages) {
            // Skip friend request messages that are already read (accepted/rejected)
            if ("friend_request".equals(m.getMessageType()) && m.isRead()) continue;

            Map<String,Object> row = new HashMap<>();
            row.put("message", m);
            User sender = userDAO.getUserById(m.getSenderId());
            row.put("sender", sender);

            switch (m.getMessageType()) {
                case "friend_request" -> friendRequests.add(row);
                case "challenge" -> {
                    // Use quiz_id from database if available, otherwise fallback to content parsing
                    Integer quizId = m.getQuizId();
                    if (quizId == null) {
                        try {
                            // Try to parse quiz_id from content (old schema format)
                            String content = m.getContent();
                            if (content.contains("[Quiz ID: ")) {
                                // Extract quiz ID from content format: "message [Quiz ID: 123]"
                                int start = content.lastIndexOf("[Quiz ID: ") + 10;
                                int end = content.lastIndexOf("]");
                                if (start > 9 && end > start) {
                                    quizId = Integer.parseInt(content.substring(start, end));
                                }
                            } else {
                                // Try parsing entire content as quiz ID (very old format)
                                quizId = Integer.parseInt(content);
                            }
                        } catch (NumberFormatException ignored) {
                            quizId = null;
                        }
                    }
                    
                    if (quizId != null) {
                        row.put("quiz", quizDao.getQuizById(quizId));
                        // find sender's best score
                        int best = 0;
                        for (QuizAttempt a : attemptDao.getAttemptsByUser(sender.getUserId())) {
                            if (a.getQuizId() == quizId && a.getScore() > best) {
                                best = a.getScore();
                            }
                        }
                        row.put("bestScore", best);
                    } else {
                        row.put("bestScore", 0);
                    }
                    challenges.add(row);
                }
                case "note" -> notes.add(row);
            }
        }

        // 3) fallback: any pending friend-requests in the FriendsDAO?
        // But filter out duplicates that are already in the messages-based friend requests
        List<Integer> pendingIds = friendsDAO.getPendingRequestsForUser(myId);
        List<User>    pendingReceived = new ArrayList<>();
        
        // Create a set of user IDs that are already in the messages-based friend requests
        Set<Integer> messageBasedRequestIds = new HashSet<>();
        for (Map<String, Object> request : friendRequests) {
            User sender = (User) request.get("sender");
            if (sender != null) {
                messageBasedRequestIds.add(sender.getUserId());
            }
        }
        
        for (int fromId : pendingIds) {
            // Skip if this user is already in the messages-based friend requests
            if (messageBasedRequestIds.contains(fromId)) {
                continue;
            }
            
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

        // Add friends list for note form
        List<User> friends = friendsDAO.getFriendsAsUsers(myId);
        req.setAttribute("friends", friends);

        // Handle tab parameter for staying on specific tab
        String tab = req.getParameter("tab");
        if (tab != null) {
            req.setAttribute("activeTab", tab);
        }

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

        // Handle challenge action
        if ("challenge".equals(action)) {
            String friendIdStr = req.getParameter("friendId");
            String quizIdStr = req.getParameter("quizId");
            
            if (friendIdStr != null && quizIdStr != null) {
                try {
                    int friendId = Integer.parseInt(friendIdStr);
                    int quizId = Integer.parseInt(quizIdStr);
                    String content = "You've been challenged to take a quiz!";
                    MessageDAO.sendChallengeMessage(userId, friendId, quizId, content);
                } catch (NumberFormatException e) {
                    // Invalid parameters, continue to redirect
                }
            }
            resp.sendRedirect("messages");
            return;
        }

        // Handle send note action
        if ("send_note".equals(action)) {
            String receiverIdStr = req.getParameter("receiverId");
            String noteContent = req.getParameter("noteContent");
            
            if (receiverIdStr != null && noteContent != null && !noteContent.trim().isEmpty()) {
                try {
                    int receiverId = Integer.parseInt(receiverIdStr);
                    msgDao.sendNote(userId, receiverId, noteContent.trim());
                    // Set success message and stay on notes tab
                    req.setAttribute("noteSuccess", "Note sent successfully!");
                } catch (NumberFormatException e) {
                    // Invalid parameters, set error message
                    req.setAttribute("noteError", "Invalid parameters");
                }
            } else {
                req.setAttribute("noteError", "Please fill in all fields");
            }
            
            // Reload the page with notes tab active
            resp.sendRedirect("messages?tab=note");
            return;
        }

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