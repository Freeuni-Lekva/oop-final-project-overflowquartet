<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" /><meta name="viewport" content="width=device-width,initial-scale=1"/>
    <title>Quiz Website · Home</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet"/>
    <style>
        body { min-height:100vh; background:linear-gradient(135deg,#0d6efd,#024ec1); color:#fff; }
        .hero { padding:6rem 0 5rem; }
        .glass-card {
            backdrop-filter:blur(8px);
            background:rgba(255,255,255,0.15);
            border:1px solid rgba(255,255,255,0.25);
            border-radius:1rem;
            box-shadow:0 .75rem 1.5rem rgba(0,0,0,0.3);
        }
        .glass-card h5, .glass-card h6, .glass-card p { color:#fff; }
        .dashboard .glass-card { padding:1.5rem; }
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
            <li class="nav-item"><a class="nav-link active" href="${ctx}/HomeServlet">
                <i class="bi bi-house-door-fill"></i> Home</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/quizzes">
                <i class="bi bi-list-check"></i> Quizzes</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/create">
                <i class="bi bi-plus-circle"></i> Create Quiz</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/leaderboard">
                <i class="bi bi-trophy-fill"></i> Leaderboard</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/friends.jsp">
                <i class="bi bi-people-fill"></i> Friends</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/messages">
                <i class="bi bi-envelope-fill"></i> Messages
                <c:if test="${unreadCount>0}"><span class="badge bg-danger ms-1">${unreadCount}</span></c:if>
            </a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/history">
                <i class="bi bi-clock-history"></i> History</a></li>
        </ul>
        <a href="${ctx}/LogoutServlet" class="btn btn-outline-light btn-sm">
            <i class="bi bi-box-arrow-right"></i> Log&nbsp;out
        </a>
    </div>
</nav>

<!-- HERO -->
<header class="hero text-center">
    <div class="container">
        <h1 class="display-5 fw-bold">Welcome back, ${userDisplayName}!</h1>
        <p class="lead mb-4">What would you like to do today?</p>
        <a href="${ctx}/quizzes" class="btn btn-light btn-lg text-primary fw-semibold">Browse Quizzes</a>
    </div>
</header>

<main class="container">

    <!-- DASHBOARD PANELS -->
    <section class="dashboard row g-4 mb-5">
        <!-- Your Quizzes -->
        <div class="col-md-6">
            <div class="glass-card h-100">
                <h5>Your Quizzes</h5>
                <c:choose>
                    <c:when test="${empty myCreatedQuizzes}">
                        <p>You haven’t created any quizzes yet.
                            <a href="${ctx}/create" class="link-light">Create one</a>!
                        </p>
                    </c:when>
                    <c:otherwise>
                        <ul class="list-unstyled mb-0">
                            <c:forEach var="q" items="${myCreatedQuizzes}">
                                <li class="mb-2">
                                    <i class="bi bi-lightbulb-fill me-1"></i>
                                    <a href="${ctx}/startQuiz?quizId=${q.quizId}" class="link-light fw-semibold">
                                            ${q.title}
                                    </a>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Your Friends -->
        <div class="col-md-6">
            <div class="glass-card h-100">
                <h5>Your Friends</h5>
                <c:choose>
                    <c:when test="${empty myFriends}">
                        <p>You have no friends yet.
                            <a href="${ctx}/friends.jsp" class="link-light">Find some</a>.
                        </p>
                    </c:when>
                    <c:otherwise>
                        <ul class="list-inline mb-0">
                            <c:forEach var="f" items="${myFriends}">
                                <li class="list-inline-item me-3">
                                    <a href="${ctx}/profile?id=${f.userId}" class="link-light">
                                        <i class="bi bi-person-circle"></i> ${f.displayName}
                                    </a>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Friends’ Recent Activity -->
        <div class="col-12">
            <div class="glass-card">
                <h5>Friends’ Recent Activity</h5>
                <c:choose>
                    <c:when test="${empty friendsActivity}">
                        <p>No recent activity from your friends.</p>
                    </c:when>
                    <c:otherwise>
                        <ul class="mb-0">
                            <c:forEach var="row" items="${friendsActivity}">
                                <li>
                                    <strong>${row.sender.displayName}</strong> scored
                                    <span class="fw-semibold">${row.attempt.score}</span> on
                                    <a href="${ctx}/startQuiz?quizId=${row.quiz.quizId}" class="link-light">
                                            ${row.quiz.title}
                                    </a>
                                    <small class="text-muted">
                                        (<fmt:formatDate value="${row.attempt.attemptDate}" pattern="MMM dd"/>)
                                    </small>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </section>

    <!-- FEATURED QUIZZES -->
    <section class="mb-5">
        <h2 class="h4 fw-semibold mb-3">Featured Quizzes</h2>
        <div class="row g-4">
            <c:forEach var="quiz" items="${featuredQuizzes}">
                <div class="col-12 col-sm-6 col-lg-4">
                    <div class="glass-card p-4 h-100 d-flex flex-column">
                        <h5 class="fw-semibold mb-1">
                            <i class="bi bi-lightbulb-fill me-1"></i> ${quiz.title}
                        </h5>
                        <p class="small flex-grow-1">${quiz.description}</p>
                        <div class="d-flex justify-content-between align-items-center mt-2">
                            <span class="small"><i class="bi bi-question-circle"></i> ${quiz.questionCount} Qs</span>
                            <a href="${ctx}/startQuiz?quizId=${quiz.quizId}"
                               class="btn btn-sm btn-light text-primary fw-semibold">Play</a>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </section>

    <!-- RECENT QUIZ HISTORY -->
    <section class="mb-5">
        <h2 class="h4 fw-semibold mb-3">
            <i class="bi bi-clock-history"></i> Recent Quiz History
        </h2>
        <c:choose>
            <c:when test="${empty recentAttemptsWithQuiz}">
                <p>No recent quiz attempts yet. Start playing quizzes!</p>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-sm table-bordered table-glass align-middle">
                        <thead>
                        <tr><th>Quiz</th><th>Score</th><th>Date</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach var="row" items="${recentAttemptsWithQuiz}">
                            <tr>
                                <td>${row.quiz.title}</td>
                                <td>${row.attempt.score}</td>
                                <td><fmt:formatDate value="${row.attempt.attemptDate}" pattern="MMM dd, yyyy HH:mm"/></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </section>

</main>

<!-- FOOTER -->
<footer class="text-center py-3">
    &copy; 2025 Quizzmosis &middot; Made with <i class="bi bi-heart-fill"></i> by OverflowQuartet
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

