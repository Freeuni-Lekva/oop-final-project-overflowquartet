<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Quiz Website · Leaderboard</title>

    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

    <style>
        /* brand backdrop */
        body {
            min-height: 100vh;
            background: linear-gradient(135deg, #0d6efd 0%, #024ec1 100%);
            color: #fff;
        }

        /* glassy card look reused from other pages */
        .glass-card {
            backdrop-filter: blur(8px);
            background: rgba(255, 255, 255, 0.15);
            border: 1px solid rgba(255, 255, 255, 0.25);
            border-radius: 1rem;
            box-shadow: 0 0.75rem 1.5rem rgba(0, 0, 0, 0.3);
        }
        .glass-card h5 { color: #fff; }
        .glass-card p { color: #e8e8e8; }

        /* table styling */
        .table-glass {
            --bs-table-bg: transparent;
            --bs-table-border-color: rgba(255, 255, 255, 0.25);
            color: #fff;
        }
        .table-glass th {
            border-color: rgba(255, 255, 255, 0.25);
            font-weight: 600;
        }
        .table-glass td {
            border-color: rgba(255, 255, 255, 0.15);
        }
        .my-row {
            background: rgba(255, 255, 255, 0.25);
        }
        .my-row td { 
            font-weight: 600; 
        }
        
        /* Rank badge styling */
        .rank-badge {
            min-width: 35px;
            text-align: center;
        }
        .rank-1 { background: linear-gradient(45deg, #ffd700, #ffed4e) !important; color: #000 !important; }
        .rank-2 { background: linear-gradient(45deg, #c0c0c0, #e5e5e5) !important; color: #000 !important; }
        .rank-3 { background: linear-gradient(45deg, #cd7f32, #daa520) !important; color: #fff !important; }

        /* footer styling */
        footer {
            font-size: .875rem;
            color: rgba(255,255,255,.65);
        }
    </style>
</head>

<body>

<!-- ─── TOP NAV ─────────────────────────────────────────────────────── -->
<nav class="navbar navbar-expand-lg navbar-dark glass-card mx-3 mt-3 px-3">
    <a class="navbar-brand fw-bold d-flex align-items-center"
       href="<%= request.getContextPath() %>/HomeServlet">
        Quizzmosis
    </a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMain">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navMain">
        <ul class="navbar-nav me-auto">
            <li class="nav-item">
                <a class="nav-link"
                   href="<%= request.getContextPath() %>/HomeServlet">
                    <i class="bi bi-house-door-fill"></i> Home
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= request.getContextPath() %>/quizzes"><i class="bi bi-list-check"></i> Quizzes</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= request.getContextPath() %>/create"><i class="bi bi-plus-circle"></i> Create Quiz</a>
            </li>
            <li class="nav-item">
                <a class="nav-link active fw-semibold" href="<%= request.getContextPath() %>/leaderboard"><i class="bi bi-trophy-fill"></i> Leaderboard</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= request.getContextPath() %>/friends"><i class="bi bi-people-fill"></i> Friends</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= request.getContextPath() %>/messages"><i class="bi bi-envelope-fill"></i> Messages</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= request.getContextPath() %>/history"><i class="bi bi-clock-history"></i> History</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= request.getContextPath() %>/achievements"><i class="bi bi-award-fill"></i> Achievements</a>
            </li>
        </ul>
        <a href="<%= request.getContextPath() %>/LogoutServlet" class="btn btn-outline-light btn-sm">
            <i class="bi bi-box-arrow-right"></i> Log&nbsp;out
        </a>
    </div>
</nav>

<!-- ─── PAGE HEADER ──────────────────────────────────────────────── -->
<header class="text-center py-5">
    <h1 class="display-5 fw-bold mb-0">
        <i class="bi bi-trophy-fill me-2"></i>Leaderboard
    </h1>
    <p class="lead">Test your knowledge and compete with others!</p>
</header>

<!-- ─── LEADERBOARD CARD ─────────────────────────────────────────── -->
<main class="container pb-5">
    <div class="glass-card p-4">

        <!-- Simple filter form -->
        <form class="row g-3 mb-4" method="get" action="<%= request.getContextPath() %>/leaderboard">
            <div class="col-12 col-md-6">
                <label for="quizSelect" class="form-label text-light">Filter by Quiz</label>
                <select class="form-select" id="quizSelect" name="quizId" onchange="this.form.submit()">
                    <option value="">All Quizzes</option>
                    <%
                        java.util.List<Bean.Quiz> allQuizzes = (java.util.List<Bean.Quiz>) request.getAttribute("allQuizzes");
                        String selectedQuizId = request.getParameter("quizId");
                        if (allQuizzes != null) {
                            for (Bean.Quiz quiz : allQuizzes) {
                                String selected = (selectedQuizId != null && selectedQuizId.equals(String.valueOf(quiz.getQuizId()))) ? "selected" : "";
                    %>
                        <option value="<%= quiz.getQuizId() %>" <%= selected %>><%= quiz.getTitle() %></option>
                    <%
                            }
                        }
                    %>
                </select>
            </div>
            <div class="col-12 col-md-6">
                <label for="timeframeSelect" class="form-label text-light">Time Period</label>
                <select class="form-select" id="timeframeSelect" name="timeframe" onchange="this.form.submit()">
                    <%
                        String selectedTimeframe = request.getParameter("timeframe");
                        if (selectedTimeframe == null) selectedTimeframe = "all";
                    %>
                    <option value="all" <%= "all".equals(selectedTimeframe) ? "selected" : "" %>>All-time</option>
                    <option value="today" <%= "today".equals(selectedTimeframe) ? "selected" : "" %>>Today</option>
                    <option value="week" <%= "week".equals(selectedTimeframe) ? "selected" : "" %>>This week</option>
                    <option value="month" <%= "month".equals(selectedTimeframe) ? "selected" : "" %>>This month</option>
                </select>
            </div>
        </form>

        <!-- Leaderboard table -->
        <div class="table-responsive">
            <table class="table table-sm table-bordered table-glass align-middle">
                <thead>
                <tr>
                    <th style="width:70px">Rank</th>
                    <th>User</th>
                    <th>Quiz</th>
                    <th>Score</th>
                    <th>Time (s)</th>
                    <th>Date</th>
                </tr>
                </thead>
                <tbody>
                <%
                    java.util.List<Servlets.LeaderboardServlet.LeaderboardEntry> leaderboard = 
                        (java.util.List<Servlets.LeaderboardServlet.LeaderboardEntry>) request.getAttribute("leaderboard");
                    Bean.User currentUser = (Bean.User) session.getAttribute("user");
                    
                    if (leaderboard != null && !leaderboard.isEmpty()) {
                        int rank = 1;
                        for (Servlets.LeaderboardServlet.LeaderboardEntry row : leaderboard) {
                            String rowClass = (currentUser != null && row.getUserId() == currentUser.getUserId()) ? "my-row" : "";
                            String rankClass = "";
                            if (rank == 1) rankClass = "rank-1";
                            else if (rank == 2) rankClass = "rank-2";
                            else if (rank == 3) rankClass = "rank-3";
                %>
                            <tr class="<%= rowClass %>">
                                <td>
                                    <span class="badge bg-light text-primary fw-semibold rank-badge <%= rankClass %>"><%= rank %></span>
                                </td>
                                <td><%= row.getUsername() %></td>
                                <td><%= row.getQuizTitle() %></td>
                                <td><strong><%= row.getScore() %></strong></td>
                                <td><%= row.getDurationSeconds() %></td>
                                <td>
                                    <%= new java.text.SimpleDateFormat("MMM dd").format(row.getAttemptDate()) %>
                                </td>
                            </tr>
                <%
                            rank++;
                        }
                    } else {
                %>
                        <tr>
                            <td colspan="6" class="text-center py-4">
                                <i class="bi bi-inbox display-4 text-muted"></i>
                                <p class="mt-2 mb-0">No leaderboard data available yet.</p>
                                <small class="text-muted">Complete some quizzes to see rankings!</small>
                            </td>
                        </tr>
                <%
                    }
                %>
                </tbody>
            </table>
        </div>

        <!-- Personal best section -->
        <%
            Servlets.LeaderboardServlet.LeaderboardEntry personalBest = 
                (Servlets.LeaderboardServlet.LeaderboardEntry) request.getAttribute("personalBest");
            Boolean isUserInLeaderboard = (Boolean) request.getAttribute("isUserInLeaderboard");
            
            if (personalBest != null && (isUserInLeaderboard == null || !isUserInLeaderboard)) {
        %>
            <div class="mt-4 alert alert-info glass-card border-0">
                <div class="d-flex align-items-center">
                    <i class="bi bi-star-fill me-2 text-warning"></i>
                    <div>
                        <strong>Your Personal Best:</strong>
                        <span class="badge bg-warning text-dark ms-2 fw-semibold"><%= personalBest.getScore() %> points</span>
                        <small class="d-block text-light mt-1">
                            <%= personalBest.getQuizTitle() %> • <%= personalBest.getDurationSeconds() %>s • 
                            <%= new java.text.SimpleDateFormat("MMM dd, yyyy").format(personalBest.getAttemptDate()) %>
                        </small>
                    </div>
                </div>
            </div>
        <%
            }
        %>

        <!-- Empty state message -->
        <%
            if ((leaderboard == null || leaderboard.isEmpty()) && personalBest == null) {
        %>
            <div class="text-center py-3">
                <a href="<%= request.getContextPath() %>/quizzes" class="btn btn-light text-primary">
                    <i class="bi bi-play-fill me-1"></i>Take a Quiz
                </a>
            </div>
        <%
            }
        %>
    </div>
</main>
<!-- FOOTER -->
<footer class="text-center py-3 text-white-50">
    © 2025 Quizzmosis · Made with <i class="bi bi-heart-fill"></i> by OverflowQuartet
</footer>



<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
// Force reload on back navigation to prevent showing cached content after logout
window.addEventListener('pageshow', function(event) {
    if (event.persisted) {
        window.location.reload();
    }
});
</script>
</body>
</html>
