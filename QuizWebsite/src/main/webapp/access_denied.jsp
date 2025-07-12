<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Access Denied · Quiz Website</title>
    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" />
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
        .glass-card h5 { color: #fff; }
        .glass-card p  { color: #e8e8e8; }
        footer {
            font-size: .875rem;
            color: rgba(255,255,255,.65);
        }
    </style>
</head>
<body>
<!-- NAVBAR -->
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
                <a class="nav-link" href="<%= request.getContextPath() %>/quizzes"><i class="bi bi-list-check"></i> Quizzes</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= request.getContextPath() %>/create.jsp"><i class="bi bi-plus-circle"></i> Create Quiz</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= request.getContextPath() %>/leaderboard"><i class="bi bi-trophy-fill"></i> Leaderboard</a>
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
            <li class="nav-item"><a class="nav-link" href="<%= request.getContextPath() %>/achievements">
                <i class="bi bi-award-fill"></i> Achievements</a></li>
        </ul>
        <a href="<%= request.getContextPath() %>/LogoutServlet" class="btn btn-outline-light btn-sm">
            <i class="bi bi-box-arrow-right"></i> Log&nbsp;out
        </a>
    </div>
</nav>

<main class="container py-5">
    <div class="glass-card p-4 mx-auto text-center" style="max-width: 600px;">
        <div class="mb-4">
            <i class="bi bi-shield-exclamation" style="font-size: 4rem; color: #ffc107;"></i>
        </div>
        <h2 class="fw-bold mb-3">Access Denied</h2>
        <p class="lead mb-3">${error}</p>
        <c:if test="${not empty quizTitle}">
            <p class="text-light mb-4">Quiz: <strong>${quizTitle}</strong></p>
        </c:if>
        <div class="d-grid gap-2 d-md-flex justify-content-md-center">
            <a href="<%= request.getContextPath() %>/quizzes" class="btn btn-light text-primary fw-semibold">
                <i class="bi bi-arrow-left"></i> Back to Quizzes
            </a>
            <a href="<%= request.getContextPath() %>/create.jsp" class="btn btn-success fw-semibold">
                <i class="bi bi-plus-circle"></i> Create Your Own Quiz
            </a>
        </div>
    </div>
</main>

<footer class="text-center py-3">
    © 2025 QuizMossis · Made with <i class="bi bi-heart-fill"></i> by OverflowQuartet
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 