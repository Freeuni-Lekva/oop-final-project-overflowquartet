<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Quiz Summary · ${quiz.title}</title>
    <!-- Bootstrap 5 + Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <style>
        body {
            min-height: 100vh;
            background: linear-gradient(135deg, #0d6efd 0%, #024ec1 100%);
            color: #fff;
        }
        .glass-card {
            backdrop-filter: blur(8px);
            background: rgba(255, 255, 255, 0.15);
            border: 1px solid rgba(255, 255, 255, 0.25);
            border-radius: 1rem;
            box-shadow: 0 0.75rem 1.5rem rgba(0, 0, 0, 0.3);
        }
        .glass-card h5, .glass-card h4 { color: #fff; }
        .glass-card p { color: #e8e8e8; }
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
        .stats-card {
            background: rgba(255, 255, 255, 0.1);
            border-radius: 0.75rem;
            padding: 1.5rem;
            text-align: center;
        }
        .stats-number {
            font-size: 2rem;
            font-weight: bold;
            color: #fff;
        }
        .stats-label {
            font-size: 0.875rem;
            color: rgba(255, 255, 255, 0.8);
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        .rank-badge {
            background: linear-gradient(45deg, #ffd700, #ffed4e);
            color: #000;
            font-weight: bold;
            padding: 0.25rem 0.5rem;
            border-radius: 0.5rem;
            font-size: 0.75rem;
        }
        .performance-good { color: #28a745; }
        .performance-average { color: #ffc107; }
        .performance-poor { color: #dc3545; }
    </style>
</head>
<body>

<!-- Navbar -->
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
                <a class="nav-link" href="<%= request.getContextPath() %>/HomeServlet">
                    <i class="bi bi-house-door-fill"></i> Home
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= request.getContextPath() %>/quizzes">
                    <i class="bi bi-list-check"></i> Quizzes
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= request.getContextPath() %>/create.jsp">
                    <i class="bi bi-plus-circle"></i> Create Quiz
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= request.getContextPath() %>/leaderboard">
                    <i class="bi bi-trophy-fill"></i> Leaderboard
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= request.getContextPath() %>/friends">
                    <i class="bi bi-people-fill"></i> Friends
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= request.getContextPath() %>/messages">
                    <i class="bi bi-envelope-fill"></i> Messages
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= request.getContextPath() %>/history">
                    <i class="bi bi-clock-history"></i> History
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= request.getContextPath() %>/achievements">
                    <i class="bi bi-award-fill"></i> Achievements
                </a>
            </li>
        </ul>
        <a href="<%= request.getContextPath() %>/LogoutServlet" class="btn btn-outline-light btn-sm">
            <i class="bi bi-box-arrow-right"></i> Log&nbsp;out
        </a>
    </div>
</nav>

<main class="container py-5">
    <!-- Quiz Header -->
    <div class="glass-card p-4 mb-4">
        <div class="row align-items-center">
            <div class="col-md-8">
                <h1 class="display-6 fw-bold mb-2">
                    <i class="bi bi-lightbulb-fill me-2"></i>${quiz.title}
                </h1>
                <p class="lead mb-2">${quiz.description}</p>
                <p class="mb-0">
                    <i class="bi bi-person-circle me-1"></i>
                    Created by <a href="<%= request.getContextPath() %>/profile?id=${quiz.ownerId}" class="link-light fw-semibold">${quiz.ownerUsername}</a>
                    <span class="ms-3">
                        <i class="bi bi-calendar me-1"></i>
                        <fmt:formatDate value="${quiz.creationDate}" pattern="MMM dd, yyyy"/>
                    </span>
                </p>
            </div>
            <div class="col-md-4 text-md-end">
                <c:choose>
                    <c:when test="${stats.totalQuestions == 0}">
                        <a href="<%= request.getContextPath() %>/add-questions?quizId=${quiz.quizId}" 
                           class="btn btn-warning btn-lg">
                            <i class="bi bi-plus-circle me-2"></i>Add Questions
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="<%= request.getContextPath() %>/startQuiz?quizId=${quiz.quizId}" 
                           class="btn btn-success btn-lg me-2">
                            <i class="bi bi-play-fill me-2"></i>Start Quiz
                        </a>
                        <c:if test="${isOwner}">
                            <a href="<%= request.getContextPath() %>/add-questions?quizId=${quiz.quizId}" 
                               class="btn btn-outline-light btn-lg">
                                <i class="bi bi-pencil me-2"></i>Edit
                            </a>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Summary Statistics -->
    <div class="row g-4 mb-4">
        <div class="col-md-3">
            <div class="stats-card">
                <div class="stats-number">${stats.totalAttempts}</div>
                <div class="stats-label">Total Attempts</div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="stats-card">
                <div class="stats-number">
                    <c:choose>
                        <c:when test="${stats.averageScore > 0}">
                            <fmt:formatNumber value="${stats.averageScore}" maxFractionDigits="1"/>
                        </c:when>
                        <c:otherwise>0</c:otherwise>
                    </c:choose>
                </div>
                <div class="stats-label">Average Score</div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="stats-card">
                <div class="stats-number">
                    <c:choose>
                        <c:when test="${stats.averageTimeMinutes > 0}">
                            <fmt:formatNumber value="${stats.averageTimeMinutes}" maxFractionDigits="1"/>m
                        </c:when>
                        <c:otherwise>0m</c:otherwise>
                    </c:choose>
                </div>
                <div class="stats-label">Avg Time</div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="stats-card">
                <div class="stats-number">${stats.totalQuestions}</div>
                <div class="stats-label">Questions</div>
            </div>
        </div>
    </div>

    <div class="row g-4">
        <!-- Your Performance -->
        <div class="col-lg-6">
            <div class="glass-card p-4 h-100">
                <h4 class="mb-3">
                    <i class="bi bi-person-check me-2"></i>Your Performance
                </h4>
                
                <c:choose>
                    <c:when test="${empty userPerformance}">
                        <p class="text-center text-light">You haven't taken this quiz yet.</p>
                    </c:when>
                    <c:otherwise>
                        <div class="mb-3">
                            <label class="form-label">Sort by:</label>
                            <div class="btn-group" role="group">
                                <a href="?quizId=${quiz.quizId}&sortBy=date" 
                                   class="btn btn-sm ${currentSort == 'date' ? 'btn-light' : 'btn-outline-light'}">Date</a>
                                <a href="?quizId=${quiz.quizId}&sortBy=score" 
                                   class="btn btn-sm ${currentSort == 'score' ? 'btn-light' : 'btn-outline-light'}">Score</a>
                                <a href="?quizId=${quiz.quizId}&sortBy=time" 
                                   class="btn btn-sm ${currentSort == 'time' ? 'btn-light' : 'btn-outline-light'}">Time</a>
                            </div>
                        </div>
                        
                        <div class="table-responsive">
                            <table class="table table-glass table-sm">
                                <thead>
                                    <tr>
                                        <th>Date</th>
                                        <th>Score</th>
                                        <th>Time</th>
                                        <th>%</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="perf" items="${userPerformance}">
                                        <tr>
                                            <td><fmt:formatDate value="${perf.attemptDate}" pattern="MMM dd, HH:mm"/></td>
                                            <td>${perf.score}/${perf.totalQuestions}</td>
                                            <td>${perf.formattedDuration}</td>
                                            <td>
                                                <span class="fw-bold 
                                                    <c:choose>
                                                        <c:when test="${perf.percentage >= 80}">performance-good</c:when>
                                                        <c:when test="${perf.percentage >= 60}">performance-average</c:when>
                                                        <c:otherwise>performance-poor</c:otherwise>
                                                    </c:choose>">
                                                    ${perf.formattedPercentage}
                                                </span>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Top Performers All Time -->
        <div class="col-lg-6">
            <div class="glass-card p-4 h-100">
                <h4 class="mb-3">
                    <i class="bi bi-trophy-fill me-2"></i>Top Performers (All Time)
                </h4>
                
                <c:choose>
                    <c:when test="${empty topPerformers}">
                        <p class="text-center text-light">No attempts yet.</p>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-glass table-sm">
                                <thead>
                                    <tr>
                                        <th>Rank</th>
                                        <th>User</th>
                                        <th>Score</th>
                                        <th>Time</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="perf" items="${topPerformers}">
                                        <tr>
                                            <td>
                                                <span class="rank-badge">#${perf.rank}</span>
                                            </td>
                                            <td>
                                                <a href="<%= request.getContextPath() %>/profile?id=${perf.userId}" 
                                                   class="link-light text-decoration-none">
                                                    ${perf.displayName != null ? perf.displayName : perf.username}
                                                </a>
                                            </td>
                                            <td>${perf.score}/${perf.totalQuestions}</td>
                                            <td>${perf.formattedDuration}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Top Performers Last Day -->
        <div class="col-lg-6">
            <div class="glass-card p-4 h-100">
                <h4 class="mb-3">
                    <i class="bi bi-calendar-day me-2"></i>Top Performers (Last 24h)
                </h4>
                
                <c:choose>
                    <c:when test="${empty topPerformersLastDay}">
                        <p class="text-center text-light">No attempts in the last 24 hours.</p>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-glass table-sm">
                                <thead>
                                    <tr>
                                        <th>Rank</th>
                                        <th>User</th>
                                        <th>Score</th>
                                        <th>Time</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="perf" items="${topPerformersLastDay}">
                                        <tr>
                                            <td>
                                                <span class="rank-badge">#${perf.rank}</span>
                                            </td>
                                            <td>
                                                <a href="<%= request.getContextPath() %>/profile?id=${perf.userId}" 
                                                   class="link-light text-decoration-none">
                                                    ${perf.displayName != null ? perf.displayName : perf.username}
                                                </a>
                                            </td>
                                            <td>${perf.score}/${perf.totalQuestions}</td>
                                            <td>${perf.formattedDuration}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Recent Test Takers -->
        <div class="col-lg-6">
            <div class="glass-card p-4 h-100">
                <h4 class="mb-3">
                    <i class="bi bi-clock-history me-2"></i>Recent Test Takers
                </h4>
                
                <c:choose>
                    <c:when test="${empty recentTestTakers}">
                        <p class="text-center text-light">No recent attempts.</p>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-glass table-sm">
                                <thead>
                                    <tr>
                                        <th>User</th>
                                        <th>Score</th>
                                        <th>Time</th>
                                        <th>When</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="perf" items="${recentTestTakers}">
                                        <tr>
                                            <td>
                                                <a href="<%= request.getContextPath() %>/profile?id=${perf.userId}" 
                                                   class="link-light text-decoration-none">
                                                    ${perf.displayName != null ? perf.displayName : perf.username}
                                                </a>
                                            </td>
                                            <td>
                                                <span class="fw-bold 
                                                    <c:choose>
                                                        <c:when test="${perf.percentage >= 80}">performance-good</c:when>
                                                        <c:when test="${perf.percentage >= 60}">performance-average</c:when>
                                                        <c:otherwise>performance-poor</c:otherwise>
                                                    </c:choose>">
                                                    ${perf.score}/${perf.totalQuestions}
                                                </span>
                                            </td>
                                            <td>${perf.formattedDuration}</td>
                                            <td><fmt:formatDate value="${perf.attemptDate}" pattern="MMM dd, HH:mm"/></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</main>

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