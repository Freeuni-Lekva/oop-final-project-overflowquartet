<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Profile · Quizzmosis</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
    <style>
        body { min-height: 100vh; background: linear-gradient(135deg, #0d6efd 0%, #024ec1 100%); color: #fff; }
        .glass-card { backdrop-filter: blur(8px); background: rgba(255,255,255,.15); border: 1px solid rgba(255,255,255,.25); border-radius: 1rem; box-shadow: 0 .75rem 1.5rem rgba(0,0,0,.3); }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark glass-card mx-3 mt-3 px-3">
    <a class="navbar-brand fw-bold d-flex align-items-center" href="<%= request.getContextPath() %>/HomeServlet">Quizzmosis</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMain">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navMain">
        <ul class="navbar-nav me-auto">
            <li class="nav-item"><a class="nav-link" href="<%= request.getContextPath() %>/HomeServlet"><i class="bi bi-house-door-fill"></i> Home</a></li>
            <li class="nav-item"><a class="nav-link" href="<%= request.getContextPath() %>/quizzes"><i class="bi bi-list-check"></i> Quizzes</a></li>
            <li class="nav-item"><a class="nav-link" href="<%= request.getContextPath() %>/create"><i class="bi bi-plus-circle"></i> Create Quiz</a></li>
            <li class="nav-item"><a class="nav-link" href="<%= request.getContextPath() %>/leaderboard"><i class="bi bi-trophy-fill"></i> Leaderboard</a></li>
            <li class="nav-item"><a class="nav-link" href="<%= request.getContextPath() %>/friends"><i class="bi bi-people-fill"></i> Friends</a></li>
            <li class="nav-item"><a class="nav-link" href="<%= request.getContextPath() %>/messages"><i class="bi bi-envelope-fill"></i> Messages</a></li>
            <li class="nav-item"><a class="nav-link" href="<%= request.getContextPath() %>/history"><i class="bi bi-clock-history"></i> History</a></li>
        </ul>
        <a href="<%= request.getContextPath() %>/LogoutServlet" class="btn btn-outline-light btn-sm"><i class="bi bi-box-arrow-right"></i> Log&nbsp;out</a>
    </div>
</nav>
<main class="container py-5">
    <div class="glass-card p-5 mx-auto" style="max-width: 500px;">
        <div class="text-center mb-4">
            <i class="bi bi-person-circle" style="font-size: 4rem;"></i>
            <h2 class="fw-bold mt-2 mb-0">${profileUser.username}</h2>
        </div>
        <c:choose>
            <c:when test="${isSelf}">
                <div class="alert alert-info">This is your profile.</div>
            </c:when>
            <c:when test="${isFriend}">
                <form method="post" action="${pageContext.request.contextPath}/friends" class="mb-3">
                    <input type="hidden" name="action" value="remove" />
                    <input type="hidden" name="targetId" value="${profileUser.userId}" />
                    <button type="submit" class="btn btn-outline-danger w-100"><i class="bi bi-x"></i> Unfriend</button>
                </form>
                <div class="alert alert-success">You are friends.</div>
            </c:when>
            <c:when test="${isPending}">
                <button class="btn btn-secondary w-100" disabled><i class="bi bi-hourglass-split"></i> Friend Request Sent</button>
            </c:when>
            <c:when test="${canAddFriend}">
                <form method="post" action="${pageContext.request.contextPath}/friends" class="mb-3">
                    <input type="hidden" name="action" value="send" />
                    <input type="hidden" name="targetId" value="${profileUser.userId}" />
                    <button type="submit" class="btn btn-success w-100"><i class="bi bi-person-plus"></i> Add Friend</button>
                </form>
            </c:when>
        </c:choose>
    </div>
</main>
<footer class="text-center py-3 text-white-50">
    © 2025 Quizzmosis · Made with <i class="bi bi-heart-fill"></i> by OverflowQuartet
</footer>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 