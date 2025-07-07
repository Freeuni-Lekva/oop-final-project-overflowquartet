<%@ page import="Bean.User,Bean.Announcement,Bean.Quiz,Bean.QuizResult" %>
<%@ page session="true" %>
<html>
<head>
    <title>Quiz Website Home</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 30px; }
        .section { margin-bottom: 30px; }
        .nav { margin-bottom: 20px; }
        .nav a { margin-right: 15px; }
        .placeholder { color: #888; font-style: italic; }
    </style>
</head>
<body>
<%
    User user = (User) session.getAttribute("user");
    java.util.List<Announcement> announcements = (java.util.List<Announcement>) request.getAttribute("announcements");
    java.util.List<Quiz> quizzes = (java.util.List<Quiz>) request.getAttribute("quizzes");
    java.util.List<QuizResult> userResults = (java.util.List<QuizResult>) request.getAttribute("userResults");
%>
<div class="nav">
    <a href="index.jsp">Home</a>
    <a href="announcement">Announcements</a>
    <a href="quiz-edit">Create Quiz</a>
    <a href="achievement">Achievements</a>
    <a href="message">Messages</a>
    <a href="friend">Friends</a>
    <% if (user == null) { %>
        <a href="login.jsp">Login</a>
        <a href="register.jsp">Register</a>
    <% } else { %>
        Welcome, <b><%= user.getDisplayName() != null ? user.getDisplayName() : user.getUsername() %></b>!
        <a href="user?action=logout">Logout</a>
        <% if (user.isAdmin()) { %>
            <a href="admin">Admin</a>
        <% } %>
    <% } %>
</div>

<div class="section">
    <h2>Announcements</h2>
    <% if (announcements != null && !announcements.isEmpty()) { %>
        <ul>
        <% for (Announcement a : announcements) { %>
            <li><%= a.getContent() %> <span style="color:#888">[<%= a.getPostedAt() %>]</span></li>
        <% } %>
        </ul>
    <% } else { %>
        <div class="placeholder">No announcements.</div>
    <% } %>
</div>

<div class="section">
    <h2>All Quizzes</h2>
    <% if (quizzes != null && !quizzes.isEmpty()) { %>
        <ul>
        <% for (Quiz q : quizzes) { %>
            <li><a href="quiz?id=<%= q.getQuizId() %>"><%= q.getTitle() %></a> - <%= q.getDescription() %></li>
        <% } %>
        </ul>
    <% } else { %>
        <div class="placeholder">No quizzes available.</div>
    <% } %>
</div>

<% if (user != null) { %>
    <div class="section">
        <h2>Your Recent Quiz Activity</h2>
        <% if (userResults != null && !userResults.isEmpty()) { %>
            <ul>
            <% for (QuizResult r : userResults) { %>
                <li>Quiz ID: <%= r.getQuizId() %> | Score: <%= r.getScore() %> / <%= r.getTotalQuestions() %> | <%= r.getTakenAt() %></li>
            <% } %>
            </ul>
        <% } else { %>
            <div class="placeholder">No recent quiz activity.</div>
        <% } %>
    </div>
<% } %>

</body>
</html>
