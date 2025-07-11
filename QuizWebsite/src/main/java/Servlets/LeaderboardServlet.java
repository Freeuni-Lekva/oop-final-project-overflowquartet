package Servlets;

import Bean.Quiz;
import Bean.User;
import DB.QuizDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/leaderboard")
public class LeaderboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login");
            return;
        }

        String quizIdParam = request.getParameter("quizId");
        String timeframe = request.getParameter("timeframe");

        if (timeframe == null || timeframe.isEmpty()) {
            timeframe = "all";
        }

        try {
            // Use QuizDAO to get all quizzes
            QuizDAO quizDAO = new QuizDAO();
            List<Quiz> allQuizzes = quizDAO.getAllQuizzes();
            request.setAttribute("allQuizzes", allQuizzes);

            // Debug output
            System.out.println("=== LeaderboardServlet Debug ===");
            System.out.println("Quiz ID param: '" + quizIdParam + "'");
            System.out.println("Timeframe param: '" + timeframe + "'");
            System.out.println("All quizzes count: " + allQuizzes.size());
            if (allQuizzes.isEmpty()) {
                System.out.println("WARNING: No quizzes found in database!");
            } else {
                for (Quiz quiz : allQuizzes) {
                    System.out.println("  Quiz: ID=" + quiz.getQuizId() + ", Title='" + quiz.getTitle() + "', Owner='" + quiz.getOwnerUsername() + "'");
                }
            }

            // Get leaderboard
            List<LeaderboardEntry> leaderboard = getLeaderboardData(quizIdParam, timeframe);
            System.out.println("DEBUG: Leaderboard entries found: " + leaderboard.size());

            // Get user's personal best (for this quiz or globally)
            LeaderboardEntry personalBest = getUserPersonalBest(user.getUserId(), quizIdParam, timeframe);
            System.out.println("DEBUG: Personal best found: " + (personalBest != null ? "Yes" : "No"));
            request.setAttribute("leaderboard", leaderboard);
            request.setAttribute("personalBest", personalBest);

            // For use in the JSP to highlight if current user is not in top 100
            boolean isUserInLeaderboard = leaderboard.stream()
                    .anyMatch(entry -> entry.getUserId() == user.getUserId());
            request.setAttribute("isUserInLeaderboard", isUserInLeaderboard);

            request.getRequestDispatcher("/leaderboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading leaderboard");
        }
    }

    /**
     * Gets leaderboard entries filtered by quiz and timeframe.
     * Supports: all, month, week, today (last 24 hours)
     */
    private List<LeaderboardEntry> getLeaderboardData(String quizIdParam, String timeframe) {
        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT u.username, qa.score, qa.duration_seconds, qa.attempt_date, u.user_id, q.title, qa.quiz_id ");
        sql.append("FROM quiz_attempts qa ");
        sql.append("JOIN users u ON qa.user_id = u.user_id ");
        sql.append("JOIN quizzes q ON qa.quiz_id = q.quiz_id ");
        sql.append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        // Quiz filter
        if (quizIdParam != null && !quizIdParam.isEmpty()) {
            try {
                int quizId = Integer.parseInt(quizIdParam);
                sql.append("AND qa.quiz_id = ? ");
                params.add(quizId);
            } catch (NumberFormatException e) {
                // Invalid quizId, skip filtering by quiz
            }
        }

        // Timeframe filter
        if (!"all".equals(timeframe)) {
            sql.append("AND qa.attempt_date >= ? ");
            Calendar cal = Calendar.getInstance();
            switch (timeframe) {
                case "week":  
                    cal.add(Calendar.WEEK_OF_YEAR, -1); 
                    break;
                case "month": 
                    cal.add(Calendar.MONTH, -1); 
                    break;
                case "today":
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    break;
            }
            params.add(new Timestamp(cal.getTimeInMillis()));
        }

        sql.append("ORDER BY qa.score DESC, qa.duration_seconds ASC, qa.attempt_date DESC ");
        sql.append("LIMIT 100");

        System.out.println("DEBUG: Executing SQL: " + sql.toString());
        System.out.println("DEBUG: Parameters: " + params);

        try (Connection conn = DB.DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LeaderboardEntry entry = new LeaderboardEntry();
                    entry.setUsername(rs.getString("username"));
                    entry.setScore(rs.getInt("score"));
                    entry.setDurationSeconds(rs.getInt("duration_seconds"));
                    entry.setAttemptDate(rs.getTimestamp("attempt_date"));
                    entry.setUserId(rs.getInt("user_id"));
                    entry.setQuizTitle(rs.getString("title"));
                    entry.setQuizId(rs.getInt("quiz_id"));
                    leaderboard.add(entry);
                }
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("DEBUG: Found " + leaderboard.size() + " leaderboard entries");
        return leaderboard;
    }

    /**
     * Gets the user's personal best entry (for quiz or globally, with same timeframe filter).
     * If quizIdParam is null, gets global best.
     */
    private LeaderboardEntry getUserPersonalBest(int userId, String quizIdParam, String timeframe) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT u.username, qa.score, qa.duration_seconds, qa.attempt_date, u.user_id, q.title, qa.quiz_id ");
        sql.append("FROM quiz_attempts qa ");
        sql.append("JOIN users u ON qa.user_id = u.user_id ");
        sql.append("JOIN quizzes q ON qa.quiz_id = q.quiz_id ");
        sql.append("WHERE qa.user_id = ? ");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (quizIdParam != null && !quizIdParam.isEmpty()) {
            try {
                int quizId = Integer.parseInt(quizIdParam);
                sql.append("AND qa.quiz_id = ? ");
                params.add(quizId);
            } catch (NumberFormatException e) {
                // Invalid quizId, skip filtering by quiz
            }
        }

        if (!"all".equals(timeframe)) {
            sql.append("AND qa.attempt_date >= ? ");
            Calendar cal = Calendar.getInstance();
            switch (timeframe) {
                case "week":  
                    cal.add(Calendar.WEEK_OF_YEAR, -1); 
                    break;
                case "month": 
                    cal.add(Calendar.MONTH, -1); 
                    break;
                case "today":
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    break;
            }
            params.add(new Timestamp(cal.getTimeInMillis()));
        }

        sql.append("ORDER BY qa.score DESC, qa.duration_seconds ASC, qa.attempt_date DESC ");
        sql.append("LIMIT 1");

        try (Connection conn = DB.DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    LeaderboardEntry entry = new LeaderboardEntry();
                    entry.setUsername(rs.getString("username"));
                    entry.setScore(rs.getInt("score"));
                    entry.setDurationSeconds(rs.getInt("duration_seconds"));
                    entry.setAttemptDate(rs.getTimestamp("attempt_date"));
                    entry.setUserId(rs.getInt("user_id"));
                    entry.setQuizTitle(rs.getString("title"));
                    entry.setQuizId(rs.getInt("quiz_id"));
                    return entry;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Inner class to represent leaderboard entries
    public static class LeaderboardEntry {
        private String username;
        private int score;
        private int durationSeconds;
        private Timestamp attemptDate;
        private int userId;
        private String quizTitle;
        private int quizId;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }
        public int getDurationSeconds() { return durationSeconds; }
        public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }
        public Timestamp getAttemptDate() { return attemptDate; }
        public void setAttemptDate(Timestamp attemptDate) { this.attemptDate = attemptDate; }
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        public String getQuizTitle() { return quizTitle; }
        public void setQuizTitle(String quizTitle) { this.quizTitle = quizTitle; }
        public int getQuizId() { return quizId; }
        public void setQuizId(int quizId) { this.quizId = quizId; }
    }
}
