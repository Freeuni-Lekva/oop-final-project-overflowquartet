package Servlets;

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
import java.sql.Timestamp;
import java.util.*;

@WebServlet("/HomeServlet")
public class HomeServlet extends HttpServlet {

    // reuse this for both your own attempts and friends' attempts
    public static class AttemptWithQuiz {
        private final QuizAttempt attempt;
        private final Quiz quiz;
        private final User user;

        public AttemptWithQuiz(QuizAttempt a, Quiz q, User u) {
            this.attempt = a;
            this.quiz    = q;
            this.user    = u;
        }

        public QuizAttempt getAttempt() {
            return attempt;
        }

        public Quiz getQuiz() {
            return quiz;
        }

        public User getUser() {
            return user;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }
        User currentUser = (User) session.getAttribute("user");
        int userId = currentUser.getUserId();

        // DAOs
        QuizDAO         quizDAO      = new QuizDAO();
        QuizAttemptDAO  attemptDAO   = new QuizAttemptDAO();
        MessageDAO      messageDAO   = new MessageDAO();
        FriendsDAO      friendsDAO   = new FriendsDAO();
        UserDAO         userDAO      = new UserDAO();

        // 1) Popular quizzes (top 3 by attempts)
        List<Quiz> popularQuizzes = quizDAO.getPopularQuizzes(3);
        req.setAttribute("popularQuizzes", popularQuizzes);
        
        // 2) Featured quizzes (global)
        List<Quiz> featuredQuizzes = quizDAO.getAllQuizzesWithQuestionCount();
        req.setAttribute("featuredQuizzes", featuredQuizzes);

        // 2) Your name for greeting
        req.setAttribute("userDisplayName", currentUser.getDisplayName());

        // 3) Unread messages count
        int unreadCount = messageDAO.countUnread(userId);
        req.setAttribute("unreadCount", unreadCount);

        // 4) Quizzes you created
        List<Quiz> myCreated = quizDAO.getQuizzesByOwner(userId);
        req.setAttribute("myCreatedQuizzes", myCreated);

        // 5) Your friends list
        List<Integer> friendIds = friendsDAO.getFriendIds(userId, FriendsDAO.FriendStatus.ACCEPTED);
        List<User> myFriends = new ArrayList<>();
        for (int fid : friendIds) {
            User f = userDAO.getUserById(fid);
            if (f != null) myFriends.add(f);
        }
        req.setAttribute("myFriends", myFriends);

        // 6) Recent activity by friends (up to 3 each)
        List<AttemptWithQuiz> friendsActivity = new ArrayList<>();
        for (User f : myFriends) {
            int count = 0;
            for (QuizAttempt a : attemptDAO.getAttemptsByUser(f.getUserId())) {
                if (count++ >= 3) break;
                Quiz q = quizDAO.getQuizById(a.getQuizId());
                friendsActivity.add(new AttemptWithQuiz(a, q, f));
            }
        }
        req.setAttribute("friendsActivity", friendsActivity);

        // 7) Your recent quiz attempts (up to 3)
        List<QuizAttempt> allYour = attemptDAO.getAttemptsByUser(userId);
        List<AttemptWithQuiz> recentYour = new ArrayList<>();
        for (int i = 0; i < Math.min(3, allYour.size()); i++) {
            QuizAttempt a = allYour.get(i);
            Quiz q = quizDAO.getQuizById(a.getQuizId());
            recentYour.add(new AttemptWithQuiz(a, q, currentUser));
        }
        req.setAttribute("recentAttemptsWithQuiz", recentYour);

        // 8) User achievements
        DB.AchievementsDAO achievementsDAO = new DB.AchievementsDAO();
        List<Bean.Achievement> userAchievements = achievementsDAO.getAchievementsByUserId(userId);
        req.setAttribute("userAchievements", userAchievements);

        // Forward to JSP
        req.getRequestDispatcher("/home_page.jsp")
                .forward(req, resp);
    }
}