<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Quiz Website · Create Quiz</title>
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
<!-- ─── NAVBAR (copied from home_page.jsp) ───────────────────────────── -->
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
                <a class="nav-link active fw-semibold" href="#"><i class="bi bi-plus-circle"></i> Create Quiz</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="<%= request.getContextPath() %>/leaderboard.jsp"><i class="bi bi-trophy-fill"></i> Leaderboard</a>
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
<main class="container py-5">
    <div class="glass-card p-4 mx-auto" style="max-width: 600px;">
        <h2 class="fw-bold mb-4"><i class="bi bi-plus-circle"></i> Create a New Quiz</h2>
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        <form action="<%= request.getContextPath() %>/create" method="post" autocomplete="off">
            <div class="mb-3">
                <label for="title" class="form-label">Quiz Title</label>
                <input type="text" class="form-control" id="title" name="title" required maxlength="100">
            </div>
            <div class="mb-3">
                <label for="description" class="form-label">Description <small>(optional)</small></label>
                <textarea class="form-control" id="description" name="description" rows="2" maxlength="300"></textarea>
            </div>
            <div class="mb-3 row">
                <div class="col">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="randomOrder" name="randomOrder">
                        <label class="form-check-label" for="randomOrder">Randomize question order</label>
                    </div>
                </div>
                <div class="col">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="multiplePages" name="multiplePages">
                        <label class="form-check-label" for="multiplePages">Multiple pages</label>
                    </div>
                </div>
                <div class="col">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="immediateCorrection" name="immediateCorrection">
                        <label class="form-check-label" for="immediateCorrection">Immediate correction</label>
                    </div>
                </div>
            </div>
            <hr>
            <!-- Placeholder for dynamic question addition (future enhancement) -->
            <div class="mb-3">
                <div class="alert alert-info mb-0">
                    <i class="bi bi-info-circle"></i> You can add questions after creating the quiz.
                </div>
            </div>
            <button type="submit" class="btn btn-light text-primary fw-semibold w-100">
                <i class="bi bi-plus-circle"></i> Create Quiz
            </button>
        </form>
    </div>
</main>
<footer class="text-center py-3">
    © 2025 QuizMossis · Made with <i class="bi bi-heart-fill"></i> by OverflowQuartet
</footer>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 