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
        .scrollable-content {
            max-height: 200px;
            overflow-y: auto;
            scrollbar-width: thin;
            scrollbar-color: rgba(255,255,255,0.3) transparent;
        }
        .scrollable-content::-webkit-scrollbar {
            width: 6px;
        }
        .scrollable-content::-webkit-scrollbar-track {
            background: transparent;
        }
        .scrollable-content::-webkit-scrollbar-thumb {
            background: rgba(255,255,255,0.3);
            border-radius: 3px;
        }
        .scrollable-content::-webkit-scrollbar-thumb:hover {
            background: rgba(255,255,255,0.5);
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
            <li class="nav-item"><a class="nav-link active" href="${ctx}/HomeServlet">
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
        <!-- Achievements Summary -->
        <div class="d-flex flex-wrap justify-content-center gap-3 my-3">
            <c:forEach var="ach" items="${userAchievements}">
            <span class="position-relative" data-bs-toggle="tooltip" data-bs-title="${ach.description}">
              <i class="bi ${ach.iconUrl} fs-2" style="color:#ffd700;"></i>
            </span>

          </c:forEach>
          <c:if test="${empty userAchievements}">
            <div class="rounded-pill px-3 py-2 d-flex align-items-center justify-content-center" style="background:rgba(255,255,255,0.10); min-width:220px;">
              <i class="bi bi-patch-check-fill me-2 fs-4" style="color:#ffe066;"></i>
              <span class="small text-light">No achievements yet</span>
              <a href="${ctx}/achievements" class="btn btn-sm btn-light ms-3 px-3 py-1 fw-semibold text-primary d-flex align-items-center" style="font-size:1em; line-height:1.1; min-height:unset;">
                <i class="bi bi-arrow-right-circle me-2"></i>See all
              </a>
            </div>
          </c:if>

        </div>
        <script>
            var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
            tooltipTriggerList.map(function (tooltipTriggerEl) {
                return new bootstrap.Tooltip(tooltipTriggerEl);
            });
        </script>
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

                        <div class="scrollable-content">
                            <ul class="list-unstyled mb-0">
                                <c:forEach var="q" items="${myCreatedQuizzes}">
                                    <li class="mb-2">
                                        <i class="bi bi-lightbulb-fill me-1"></i>
                                        <a href="${ctx}/quiz-summary?quizId=${q.quizId}" class="link-light fw-semibold">
                                                ${q.title}
                                        </a>
                                        <span class="small ms-2">by <a href="${ctx}/profile?id=${q.ownerId}" class="link-light">${q.ownerUsername}</a></span>
                                    </li>   
                                </c:forEach>
                            </ul>
                        </div>

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
                            <a href="${ctx}/friends" class="link-light">Find some</a>.
                        </p>
                    </c:when>
                    <c:otherwise>
                        <div class="scrollable-content">
                            <ul class="list-inline mb-0">
                                <c:forEach var="f" items="${myFriends}">
                                    <li class="list-inline-item me-3 mb-2">
                                        <a href="${ctx}/profile?id=${f.userId}" class="link-light">
                                            <i class="bi bi-person-circle"></i> ${f.displayName}
                                        </a>
                                    </li>
                                </c:forEach>
                            </ul>
                        </div>
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
                        <div class="scrollable-content">
                            <ul class="mb-0">
                                <c:forEach var="row" items="${friendsActivity}">
                                    <li class="mb-2">
                                        <strong>${row.user.displayName}</strong> scored
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
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </section>

    <!-- POPULAR QUIZZES -->
    <section class="mb-5">
        <h2 class="h4 fw-semibold mb-3">
            <i class="bi bi-fire"></i> Popular Quizzes
        </h2>
        <div class="row g-4">
            <c:forEach var="quiz" items="${popularQuizzes}">
                <div class="col-12 col-sm-6 col-lg-4">
                    <div class="glass-card p-4 h-100 d-flex flex-column position-relative">
                        <!-- Popular badge -->
                        <div class="position-absolute top-0 end-0 mt-2 me-2">
                            <span class="badge bg-warning text-dark">
                                <i class="bi bi-fire"></i> Popular
                            </span>
                        </div>
                        <h5 class="fw-semibold mb-1">
                            <i class="bi bi-lightbulb-fill me-1"></i>
                            <a href="${ctx}/quiz-summary?quizId=${quiz.quizId}" class="link-light text-decoration-none">${quiz.title}</a>
                            <span class="small ms-2">by <a href="${ctx}/profile?id=${quiz.ownerId}" class="link-light">${quiz.ownerUsername}</a></span>
                        </h5>
                        <p class="small flex-grow-1">${quiz.description}</p>
                        <div class="d-flex justify-content-between align-items-center mt-2">
                            <span class="small">
                                <i class="bi bi-question-circle"></i> ${quiz.questionCount} Qs
                                <span class="ms-2"><i class="bi bi-people"></i> ${quiz.attemptCount} attempts</span>
                            </span>
                            <a href="${ctx}/startQuiz?quizId=${quiz.quizId}"
                               class="btn btn-sm btn-warning text-dark fw-semibold">
                                <i class="bi bi-play-fill"></i> Play
                            </a>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </section>

    <!-- FEATURED QUIZZES -->
    <section class="mb-5">
        <h2 class="h4 fw-semibold mb-3">3 Recently Created Quizzes</h2>
        <div class="row g-4">
            <c:forEach var="quiz" items="${featuredQuizzes}" varStatus="status">
                <c:if test="${status.index < 3}">
                <div class="col-12 col-sm-6 col-lg-4">
                    <div class="glass-card p-4 h-100 d-flex flex-column">
                        <h5 class="fw-semibold mb-1">
                            <i class="bi bi-lightbulb-fill me-1"></i>
                            <a href="${ctx}/quiz-summary?quizId=${quiz.quizId}" class="link-light text-decoration-none">${quiz.title}</a>
                            <span class="small ms-2">by <a href="${ctx}/profile?id=${quiz.ownerId}" class="link-light">${quiz.ownerUsername}</a></span>
                        </h5>
                        <p class="small flex-grow-1">${quiz.description}</p>
                        <div class="d-flex justify-content-between align-items-center mt-2">
                            <span class="small"><i class="bi bi-question-circle"></i> ${quiz.questionCount} Qs</span>
                            <a href="${ctx}/startQuiz?quizId=${quiz.quizId}"
                               class="btn btn-sm btn-light text-primary fw-semibold">Play</a>
                        </div>
                    </div>
                </div>
                </c:if>
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
                <div class="glass-card p-4 text-center">
                    <i class="bi bi-clock-history fs-1 text-light mb-3"></i>
                    <h5 class="fw-semibold mb-2">No Recent Activity</h5>
                    <p class="text-light mb-3">You haven't taken any quizzes yet.</p>
                    <a href="${ctx}/quizzes" class="btn btn-light text-primary fw-semibold">
                        <i class="bi bi-play-fill me-1"></i>Start Playing
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="glass-card p-4">
                    <div class="row g-3">
                        <c:forEach var="row" items="${recentAttemptsWithQuiz}">
                            <div class="col-12">
                                <div class="d-flex align-items-center justify-content-between p-3 rounded" 
                                     style="background: rgba(255, 255, 255, 0.1); border: 1px solid rgba(255, 255, 255, 0.2);">
                                    <div class="d-flex align-items-center">
                                        <div class="me-3">
                                            <i class="bi bi-lightbulb-fill fs-4" style="color: #ffd700;"></i>
                                        </div>
                                        <div>
                                            <h6 class="mb-1 fw-semibold">
                                                <a href="${ctx}/startQuiz?quizId=${row.quiz.quizId}" class="link-light text-decoration-none">
                                                    ${row.quiz.title}
                                                </a>
                                            </h6>
                                            <small class="text-light">
                                                <i class="bi bi-calendar3 me-1"></i>
                                                <fmt:formatDate value="${row.attempt.attemptDate}" pattern="MMM dd, yyyy 'at' HH:mm"/>
                                            </small>
                                        </div>
                                    </div>
                                    <div class="text-end">
                                        <div class="badge bg-success fs-6 px-3 py-2">
                                            <i class="bi bi-trophy-fill me-1"></i>
                                            Score: ${row.attempt.score}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                    <div class="text-center mt-3">
                        <a href="${ctx}/history" class="btn btn-outline-light btn-sm">
                            <i class="bi bi-clock-history me-1"></i>View Full History
                        </a>
                    </div>
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