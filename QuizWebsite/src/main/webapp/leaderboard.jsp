<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Quiz Website · Leaderboard</title>

    <!-- Bootstrap 5 + Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

    <style>
        /* brand backdrop */
        body{
            min-height:100vh;
            background:linear-gradient(135deg,#0d6efd 0%,#024ec1 100%);
            color:#fff;
        }
        /* glass cards reused everywhere */
        .glass-card{
            backdrop-filter:blur(8px);
            background:rgba(255,255,255,.15);
            border:1px solid rgba(255,255,255,.25);
            border-radius:1rem;
            box-shadow:0 .75rem 1.5rem rgba(0,0,0,.3);
        }
        /* table tweaks */
        .table-glass{
            --bs-table-bg:transparent;
            --bs-table-border-color:rgba(255,255,255,.25);
        }
        /* highlight current user row */
        .my-row{
            background:rgba(255,255,255,.25);
        }
        .my-row td{ font-weight:600; }
    </style>
</head>

<body>

<!-- ─── NAVBAR (reuse your existing nav) ──────────────────────────── -->
<nav class="navbar navbar-expand-lg navbar-dark glass-card mx-3 mt-3 px-3">
    <a class="navbar-brand fw-semibold" href="${pageContext.request.contextPath}/">Quizzmosis</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMain">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navMain">
        <ul class="navbar-nav me-auto">
            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/">Home</a></li>
            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/quizzes">Quizzes</a></li>
            <li class="nav-item"><a class="nav-link active fw-semibold" href="#">Leaderboard</a></li>
        </ul>
        <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light btn-sm">Log&nbsp;out</a>
    </div>
</nav>

<!-- ─── PAGE HEADER ──────────────────────────────────────────────── -->
<header class="text-center py-5">
    <h1 class="fw-bold display-5 mb-0">
        <i class="bi bi-trophy-fill me-2"></i>Leaderboard
    </h1>
    <p class="lead">Can you reach the top?</p>
</header>

<!-- ─── LEADERBOARD CARD ─────────────────────────────────────────── -->
<main class="container pb-5">
    <div class="glass-card p-4">

        <!-- Choose quiz (global vs quiz-specific) -->
        <form class="row g-3 mb-4" method="get" action="${pageContext.request.contextPath}/leaderboard">
            <div class="col-12 col-md-6">
                <select class="form-select" name="quizId" onchange="this.form.submit()">
                    <option value="">🌍  Global</option>
                    <c:forEach var="quiz" items="${allQuizzes}">
                        <option value="${quiz.quizId}"
                            ${param.quizId == quiz.quizId ? 'selected' : ''}>
                                ${quiz.title}
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-12 col-md-6">
                <select class="form-select" name="timeframe" onchange="this.form.submit()">
                    <option value="all"  ${param.timeframe=='all'  ? 'selected' : ''}>All-time</option>
                    <option value="month"${param.timeframe=='month'? 'selected' : ''}>This month</option>
                    <option value="week" ${param.timeframe=='week' ? 'selected' : ''}>This week</option>
                </select>
            </div>
        </form>

        <!-- leaderboard table -->
        <div class="table-responsive">
            <table class="table table-sm table-bordered table-glass align-middle">
                <thead>
                <tr>
                    <th style="width:70px">Rank</th>
                    <th>User</th>
                    <th>Score</th>
                    <th>Time&nbsp;(s)</th>
                    <th>Date</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="row" items="${leaderboard}" varStatus="loop">
                    <tr class="${row.userId == sessionScope.user.userId ? 'my-row' : ''}">
                        <td>
                <span class="badge bg-light text-primary fw-semibold">
                        ${loop.index + 1}
                </span>
                        </td>
                        <td>${row.username}</td>
                        <td>${row.score}</td>
                        <td>${row.durationSeconds}</td>
                        <td><fmt:formatDate value="${row.attemptDate}" pattern="yyyy-MM-dd"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</main>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
