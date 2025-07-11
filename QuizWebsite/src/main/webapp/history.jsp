<%@ page
        language="java"
        contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"
        isELIgnored="false"
%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"   %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Quiz Website · History</title>

  <!-- Bootstrap 5 CSS -->
  <link
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet" />
  <!-- Bootstrap Icons -->
  <link
          rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" />

  <style>
    body {
      min-height: 100vh;
      background: linear-gradient(135deg, #0d6efd 0%, #024ec1 100%);
      color: #fff;
    }
    .glass-card {
      backdrop-filter: blur(8px);
      background: rgba(255,255,255,0.15);
      border: 1px solid rgba(255,255,255,0.25);
      border-radius: 1rem;
      box-shadow: 0 .75rem 1.5rem rgba(0,0,0,0.3);
    }
    table.table-glass {
      background: rgba(255,255,255,0.10);
    }
    table.table-glass th,
    table.table-glass td {
      color: #222;
      background: rgba(255,255,255,0.85);
    }
  </style>
</head>
<body>

<!-- ─── NAV BAR ─────────────────────────────────────────────────── -->
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
        <a class="nav-link" href="<%= request.getContextPath() %>/leaderboard"><i class="bi bi-trophy-fill"></i> Leaderboard</a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="<%= request.getContextPath() %>/friends"><i class="bi bi-people-fill"></i> Friends</a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="<%= request.getContextPath() %>/messages"><i class="bi bi-envelope-fill"></i> Messages</a>
      </li>
      <li class="nav-item">
        <a class="nav-link active" href="${pageContext.request.contextPath}/history">
          <i class="bi bi-clock-history"></i> History
        </a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="<%= request.getContextPath() %>/achievements"><i class="bi bi-award-fill"></i> Achievements</a>
      </li>
    </ul>
    <a href="<%= request.getContextPath() %>/LogoutServlet" class="btn btn-outline-light btn-sm">
      <i class="bi bi-box-arrow-right"></i> Log&nbsp;out
    </a>
  </div>
</nav>

<!-- ─── PAGE HEADER ─────────────────────────────────────────────── -->
<header class="text-center py-5">
  <h1 class="display-6 fw-bold">Your Quiz History</h1>
  <p class="lead">All of your past quiz attempts, newest first.</p>
</header>

<!-- ─── HISTORY TABLE ──────────────────────────────────────────── -->
<section class="container pb-5">
  <c:choose>
    <c:when test="${empty historyList}">
      <div class="glass-card p-5 text-center">
        <i class="bi bi-clock-history fs-1 text-light mb-3"></i>
        <h5 class="fw-semibold mb-2">No Quiz History Yet</h5>
        <p class="text-light mb-3">You haven't taken any quizzes yet. Start your quiz journey today!</p>
        <a href="${pageContext.request.contextPath}/quizzes" class="btn btn-light text-primary fw-semibold">
          <i class="bi bi-play-fill me-1"></i>Browse Quizzes
        </a>
      </div>
    </c:when>
    <c:otherwise>
      <div class="glass-card p-4">
        <div class="row g-3">
          <c:forEach var="row" items="${historyList}">
            <c:if test="${not empty row.quiz and not empty row.attempt}">
            <div class="col-12">
              <div class="d-flex align-items-center justify-content-between p-4 rounded" 
                   style="background: rgba(255, 255, 255, 0.1); border: 1px solid rgba(255, 255, 255, 0.2);">
                <div class="d-flex align-items-center">
                  <div class="me-4">
                    <i class="bi bi-lightbulb-fill fs-3" style="color: #ffd700;"></i>
                  </div>
                  <div>
                    <h6 class="mb-1 fw-semibold">
                      <a href="${pageContext.request.contextPath}/startQuiz?quizId=${row.quiz.quizId}" 
                         class="link-light text-decoration-none">
                        ${row.quiz.title}
                      </a>
                    </h6>
                    <small class="text-light">
                      <i class="bi bi-calendar3 me-1"></i>
                      <fmt:formatDate value="${row.attempt.attemptDate}" pattern="MMM dd, yyyy 'at' HH:mm"/>
                    </small>
                  </div>
                </div>
                <div class="d-flex align-items-center gap-3">
                  <div class="text-center">
                    <div class="badge bg-success fs-6 px-3 py-2">
                      <i class="bi bi-trophy-fill me-1"></i>
                      Score: ${row.attempt.score}
                    </div>
                  </div>
                  <div class="text-center">
                    <div class="badge bg-info fs-6 px-3 py-2">
                      <i class="bi bi-clock-fill me-1"></i>
                      <c:set var="mins" value="${row.attempt.durationSeconds div 60}" />
                      <fmt:formatNumber value="${mins}" pattern="#.##"/> mins
                    </div>
                  </div>
                </div>
              </div>
            </div>
            </c:if>
          </c:forEach>
        </div>
      </div>
    </c:otherwise>
  </c:choose>
</section>

<!-- ─── FOOTER ─────────────────────────────────────────────────── -->
<footer class="text-center py-3 text-white-50">
  © 2025 Quizzmosis · Made with <i class="bi bi-heart-fill"></i> by OverflowQuartet
</footer>

<!-- Bootstrap JS Bundle -->
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

