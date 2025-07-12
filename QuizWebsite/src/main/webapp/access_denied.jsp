<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width,initial-scale=1"/>
    <title>Access Denied · Quizzmosis</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet"/>
    <style>
        body { min-height:100vh; background:linear-gradient(135deg,#0d6efd,#024ec1); color:#fff; }
        .glass-card {
            backdrop-filter:blur(8px);
            background:rgba(255,255,255,0.15);
            border:1px solid rgba(255,255,255,0.25);
            border-radius:1rem;
            box-shadow:0 .75rem 1.5rem rgba(0,0,0,0.3);
        }
        footer { font-size:.875rem; color:rgba(255,255,255,.65); }
    </style>
</head>
<body>

<!-- NAVBAR -->
<nav class="navbar navbar-expand-lg navbar-dark glass-card mx-3 mt-3 px-3">
    <a class="navbar-brand fw-bold" href="${ctx}/HomeServlet">Quizzmosis</a>
    <button class="navbar-toggler" data-bs-toggle="collapse" data-bs-target="#navMain">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navMain">
        <ul class="navbar-nav me-auto">
            <li class="nav-item"><a class="nav-link" href="${ctx}/HomeServlet">
                <i class="bi bi-house-door-fill"></i> Home</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/quizzes">
                <i class="bi bi-list-check"></i> Quizzes</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/create">
                <i class="bi bi-plus-circle"></i> Create Quiz</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/leaderboard">
                <i class="bi bi-trophy-fill"></i> Leaderboard</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/friends">
                <i class="bi bi-people-fill"></i> Friends</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/messages">
                <i class="bi bi-envelope-fill"></i> Messages
                <c:if test="${unreadCount>0}"><span class="badge bg-danger ms-1">${unreadCount}</span></c:if>
            </a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/history">
                <i class="bi bi-clock-history"></i> History</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/achievements">
                <i class="bi bi-award-fill"></i> Achievements</a></li>
            <c:if test="${user.admin}">
            <li class="nav-item"><a class="nav-link" href="${ctx}/admin">
                <i class="bi bi-shield-fill"></i> Admin</a></li>
            </c:if>
        </ul>
        <a href="${ctx}/LogoutServlet" class="btn btn-outline-light btn-sm">
            <i class="bi bi-box-arrow-right"></i> Log&nbsp;out
        </a>
    </div>
</nav>

<!-- MAIN CONTENT -->
<main class="container d-flex align-items-center justify-content-center" style="min-height: 80vh;">
    <div class="text-center">
        <div class="glass-card p-5">
            <i class="bi bi-shield-exclamation display-1 text-warning mb-4"></i>
            <h1 class="display-5 fw-bold mb-3">Access Denied</h1>
            <p class="lead mb-4">You don't have permission to access this page. Administrator privileges are required.</p>
            <div class="d-flex justify-content-center gap-3">
                <a href="${ctx}/HomeServlet" class="btn btn-light btn-lg">
                    <i class="bi bi-house-door-fill me-2"></i>Go Home
                </a>
                <a href="${ctx}/LogoutServlet" class="btn btn-outline-light btn-lg">
                    <i class="bi bi-box-arrow-right me-2"></i>Logout
                </a>
            </div>
        </div>
    </div>
</main>

<!-- FOOTER -->
<footer class="text-center py-3">
    &copy; 2025 Quizzmosis &middot; Made with <i class="bi bi-heart-fill"></i> by OverflowQuartet
</footer>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html> 