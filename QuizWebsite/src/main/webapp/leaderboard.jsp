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
                <a class="nav-link active fw-semibold" href="#"><i class="bi bi-trophy-fill"></i> Leaderboard</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= request.getContextPath() %>/friends.jsp"><i class="bi bi-people-fill"></i> Friends</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= request.getContextPath() %>/messages"><i class="bi bi-envelope-fill"></i> Messages</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= request.getContextPath() %>/history"><i class="bi bi-clock-history"></i> History</a>
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
        <form class="row g-3 mb-4" method="get" action="<%= request.getContextPath() %>/LeaderboardServlet">
            <div class="col-12 col-md-6">
                <select class="form-select" name="quizId" onchange="this.form.submit()">
                    <option value="">All Quizzes</option>
                    <c:forEach var="quiz" items="${allQuizzes}">
                        <option value="${quiz.quizId}" ${param.quizId == quiz.quizId ? 'selected' : ''}>
                            ${quiz.title}
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-12 col-md-6">
                <select class="form-select" name="timeframe" onchange="this.form.submit()">
                    <option value="all" ${param.timeframe=='all' ? 'selected' : ''}>All-time</option>
                    <option value="today" ${param.timeframe=='today' ? 'selected' : ''}>Today</option>
                    <option value="week" ${param.timeframe=='week' ? 'selected' : ''}>This week</option>
                    <option value="month" ${param.timeframe=='month' ? 'selected' : ''}>This month</option>
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
                <c:choose>
                    <c:when test="${not empty leaderboard}">
                        <c:forEach var="row" items="${leaderboard}" varStatus="loop">
                            <tr class="${row.userId == sessionScope.user.userId ? 'my-row' : ''}">
                                <td>
                                    <span class="badge bg-light text-primary fw-semibold">${loop.index + 1}</span>
                                </td>
                                <td>${row.username}</td>
                                <td>${row.quizTitle}</td>
                                <td><strong>${row.score}</strong></td>
                                <td>${row.durationSeconds}</td>
                                <td>
                                    <fmt:formatDate value="${row.attemptDate}" pattern="MMM dd"/>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="6" class="text-center py-4">
                                <i class="bi bi-inbox display-4 text-muted"></i>
                                <p class="mt-2 mb-0">No leaderboard data available yet.</p>
                                <small class="text-muted">Complete some quizzes to see rankings!</small>
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>

        <!-- Personal best section -->
        <c:if test="${not empty personalBest && !isUserInLeaderboard}">
            <div class="mt-4 alert alert-info glass-card border-0">
                <div class="d-flex align-items-center">
                    <i class="bi bi-star-fill me-2"></i>
                    <div>
                        <strong>Your Personal Best:</strong>
                        <span class="badge bg-primary ms-2">${personalBest.score} points</span>
                        <small class="d-block text-muted">
                            ${personalBest.quizTitle} • ${personalBest.durationSeconds}s • 
                            <fmt:formatDate value="${personalBest.attemptDate}" pattern="MMM dd, yyyy"/>
                        </small>
                    </div>
                </div>
            </div>
        </c:if>

        <!-- Empty state message -->
        <c:if test="${empty leaderboard && empty personalBest}">
            <div class="text-center py-5">
                <i class="bi bi-trophy display-1 text-muted"></i>
                <h4 class="mt-3">No leaderboard data yet</h4>
                <p class="text-muted">Start taking quizzes to see rankings!</p>
                <a href="<%= request.getContextPath() %>/quizzes" class="btn btn-light text-primary">
                    <i class="bi bi-play-fill me-1"></i>Take a Quiz
                </a>
            </div>
        </c:if>
    </div>
</main>

<!-- ─── FOOTER ──────────────────────────────────────────────────────── -->
<footer class="text-center py-3">
     2025 QuizMossis · Made with <i class="bi bi-heart-fill"></i> by OverflowQuartet
</footer>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
