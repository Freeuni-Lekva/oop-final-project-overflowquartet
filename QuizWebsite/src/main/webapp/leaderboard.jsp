<%--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>--%>
<%--<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>--%>


<%--<!DOCTYPE html>--%>
<%--<html lang="en">--%>
<%--<head>--%>
<%--    <meta charset="UTF-8" />--%>
<%--    <meta name="viewport" content="width=device-width, initial-scale=1" />--%>
<%--    <title>Quizzmosis · Leaderboard</title>--%>

<%--    <!-- Bootstrap 5 + Icons -->--%>
<%--    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">--%>
<%--    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">--%>

<%--    <style>--%>
<%--        body{--%>
<%--            min-height:100vh;--%>
<%--            background:linear-gradient(135deg,#0d6efd 0%,#024ec1 100%);--%>
<%--            color:#fff;--%>
<%--        }--%>
<%--        .glass-card{--%>
<%--            backdrop-filter:blur(8px);--%>
<%--            background:rgba(255,255,255,.15);--%>
<%--            border:1px solid rgba(255,255,255,.25);--%>
<%--            border-radius:1rem;--%>
<%--            box-shadow:0 .75rem 1.5rem rgba(0,0,0,.3);--%>
<%--        }--%>
<%--        .table-glass{--%>
<%--            --bs-table-bg:transparent;--%>
<%--            --bs-table-border-color:rgba(255,255,255,.25);--%>
<%--        }--%>
<%--        .my-row{--%>
<%--            background:rgba(255,255,255,.25);--%>
<%--        }--%>
<%--        .my-row td{ font-weight:600; }--%>
<%--        .brand-logo {--%>
<%--            width:40px; height:40px; border-radius:50%; margin-right:12px;--%>
<%--            box-shadow:0 1px 4px rgba(0,0,0,.15);--%>
<%--            background:linear-gradient(120deg,#ffc107,#0d6efd,#6f42c1);--%>
<%--            display:inline-block; vertical-align:middle;--%>
<%--            position:relative;--%>
<%--        }--%>
<%--        .brand-logo .bi {--%>
<%--            font-size:1.8rem; position:absolute; left:50%; top:50%; transform:translate(-50%,-50%);--%>
<%--            color:#fff; text-shadow:0 2px 8px #0003;--%>
<%--        }--%>
<%--    </style>--%>
<%--</head>--%>

<%--<body>--%>

<%--<!-- ─── NAVBAR (Brand + Main Features) ─────────────────────────────── -->--%>
<%--<nav class="navbar navbar-expand-lg navbar-dark glass-card mx-3 mt-3 px-3">--%>
<%--    <a class="navbar-brand fw-bold d-flex align-items-center" href="${pageContext.request.contextPath}/">--%>
<%--        <span class="brand-logo"><i class="bi bi-lightning-charge-fill"></i></span>--%>
<%--        Quizzmosis--%>
<%--    </a>--%>
<%--    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMain">--%>
<%--        <span class="navbar-toggler-icon"></span>--%>
<%--    </button>--%>
<%--    <div class="collapse navbar-collapse" id="navMain">--%>
<%--        <ul class="navbar-nav me-auto">--%>
<%--            <li class="nav-item">--%>
<%--                <a class="nav-link" href="${pageContext.request.contextPath}/">--%>
<%--                    <i class="bi bi-house-door-fill"></i> Home--%>
<%--                </a>--%>
<%--            </li>--%>
<%--            <li class="nav-item">--%>
<%--                <a class="nav-link" href="${pageContext.request.contextPath}/quizzes">--%>
<%--                    <i class="bi bi-list-check"></i> Quizzes--%>
<%--                </a>--%>
<%--            </li>--%>
<%--            <li class="nav-item">--%>
<%--                <a class="nav-link" href="${pageContext.request.contextPath}/friends">--%>
<%--                    <i class="bi bi-people-fill"></i> Friends--%>
<%--                </a>--%>
<%--            </li>--%>
<%--            <li class="nav-item">--%>
<%--                <a class="nav-link" href="${pageContext.request.contextPath}/messages">--%>
<%--                    <i class="bi bi-envelope-fill"></i> Messages--%>
<%--                </a>--%>
<%--            </li>--%>
<%--            <li class="nav-item">--%>
<%--                <a class="nav-link active fw-semibold" href="#">--%>
<%--                    <i class="bi bi-trophy-fill"></i> Leaderboard--%>
<%--                </a>--%>
<%--            </li>--%>
<%--            <li class="nav-item">--%>
<%--                <a class="nav-link" href="${pageContext.request.contextPath}/achievements">--%>
<%--                    <i class="bi bi-star-fill"></i> Achievements--%>
<%--                </a>--%>
<%--            </li>--%>
<%--        </ul>--%>
<%--        <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light btn-sm">--%>
<%--            <i class="bi bi-box-arrow-right"></i> Log&nbsp;out--%>
<%--        </a>--%>
<%--    </div>--%>
<%--</nav>--%>

<%--<!-- ─── PAGE HEADER ──────────────────────────────────────────────── -->--%>
<%--<header class="text-center py-5">--%>
<%--    <h1 class="fw-bold display-5 mb-0">--%>
<%--        <i class="bi bi-trophy-fill me-2"></i>Leaderboard--%>
<%--    </h1>--%>
<%--    <p class="lead">Can you reach the top?</p>--%>
<%--</header>--%>

<%--<!-- ─── LEADERBOARD CARD ─────────────────────────────────────────── -->--%>
<%--<main class="container pb-5">--%>
<%--    <div class="glass-card p-4">--%>

<%--        <!-- Choose quiz (global vs quiz-specific) and timeframe -->--%>
<%--        <form class="row g-3 mb-4" method="get" action="${pageContext.request.contextPath}/leaderboard">--%>
<%--            <div class="col-12 col-md-6">--%>
<%--                <select class="form-select" name="quizId" onchange="this.form.submit()">--%>
<%--                    <option value="">🌍 Global</option>--%>
<%--                    <c:forEach var="quiz" items="${allQuizzes}">--%>
<%--                        <option value="${quiz.quizId}"--%>
<%--                            ${param.quizId == quiz.quizId ? 'selected' : ''}>--%>
<%--                                ${quiz.title}--%>
<%--                        </option>--%>
<%--                    </c:forEach>--%>
<%--                </select>--%>
<%--            </div>--%>
<%--            <div class="col-12 col-md-6">--%>
<%--                <select class="form-select" name="timeframe" onchange="this.form.submit()">--%>
<%--                    <option value="all"   ${param.timeframe=='all'   ? 'selected' : ''}>All-time</option>--%>
<%--                    <option value="today" ${param.timeframe=='today' ? 'selected' : ''}>Today</option>--%>
<%--                    <option value="week"  ${param.timeframe=='week'  ? 'selected' : ''}>This week</option>--%>
<%--                    <option value="month" ${param.timeframe=='month' ? 'selected' : ''}>This month</option>--%>
<%--                </select>--%>
<%--            </div>--%>
<%--        </form>--%>

<%--        <!-- leaderboard table -->--%>
<%--        <div class="table-responsive">--%>
<%--            <table class="table table-sm table-bordered table-glass align-middle">--%>
<%--                <thead>--%>
<%--                <tr>--%>
<%--                    <th style="width:70px">Rank</th>--%>
<%--                    <th>User</th>--%>
<%--                    <th>Score</th>--%>
<%--                    <th>Time&nbsp;(s)</th>--%>
<%--                    <th>Date</th>--%>
<%--                </tr>--%>
<%--                </thead>--%>
<%--                <tbody>--%>
<%--                <c:forEach var="row" items="${leaderboard}" varStatus="loop">--%>
<%--                    <tr class="${row.userId == sessionScope.user.userId ? 'my-row' : ''}">--%>
<%--                        <td>--%>
<%--                            <span class="badge bg-light text-primary fw-semibold">${loop.index + 1}</span>--%>
<%--                        </td>--%>
<%--                        <td>${row.username}</td>--%>
<%--                        <td>${row.score}</td>--%>
<%--                        <td>${row.durationSeconds}</td>--%>
<%--                        <td>--%>
<%--                            <fmt:formatDate value="${row.attemptDate}" pattern="yyyy-MM-dd"/>--%>
<%--                        </td>--%>
<%--                    </tr>--%>
<%--                </c:forEach>--%>
<%--                </tbody>--%>
<%--            </table>--%>
<%--        </div>--%>

<%--        <!-- PERSONAL BEST ROW if not already in top 100 -->--%>
<%--        <c:if test="${!isUserInLeaderboard && not empty personalBest}">--%>
<%--            <div class="mt-3 alert alert-info glass-card border-0 shadow text-dark">--%>
<%--                <strong>Your Best:</strong>--%>
<%--                <span class="badge bg-primary me-2">Personal Best</span>--%>
<%--                Score: <strong>${personalBest.score}</strong>--%>
<%--                &nbsp;|&nbsp; Time: <strong>${personalBest.durationSeconds}s</strong>--%>
<%--                &nbsp;|&nbsp; Date: <fmt:formatDate value="${personalBest.attemptDate}" pattern="yyyy-MM-dd"/>--%>
<%--            </div>--%>
<%--        </c:if>--%>
<%--    </div>--%>
<%--</main>--%>

<%--<!-- Bootstrap JS -->--%>
<%--<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>--%>
<%--</body>--%>
<%--</html>--%>
