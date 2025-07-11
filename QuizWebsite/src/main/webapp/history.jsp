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
      color: #fff;
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
        <a class="nav-link" href="<%= request.getContextPath() %>/leaderboard.jsp"><i class="bi bi-trophy-fill"></i> Leaderboard</a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="<%= request.getContextPath() %>/friends.jsp"><i class="bi bi-people-fill"></i> Friends</a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="<%= request.getContextPath() %>/messages"><i class="bi bi-envelope-fill"></i> Messages</a>
      </li>
      <li class="nav-item">
        <a class="nav-link active" href="${pageContext.request.contextPath}/history">
          <i class="bi bi-clock-history"></i> History
        </a>
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
      <div class="alert alert-light text-center">
        You haven’t taken any quizzes yet.
        <a href="${pageContext.request.contextPath}/quizzes">Start one now!</a>
      </div>
    </c:when>
    <c:otherwise>
      <div class="table-responsive glass-card p-3">
        <table class="table table-sm table-bordered table-glass align-middle mb-0">
          <thead>
          <tr>
            <th>Quiz</th>
            <th>Score</th>
            <th>Duration</th>
            <th>Date</th>
          </tr>
          </thead>
          <tbody>
          <c:forEach var="row" items="${historyList}">
            <tr>
              <td>
                <a href="${pageContext.request.contextPath}/quiz?id=${row.quiz.quizId}"
                   class="text-decoration-none text-white">
                    ${row.quiz.title}
                </a>
              </td>
              <td>${row.attempt.score}</td>
              <td>
                  <%-- perform the division in JSTL core so it’s evaluated --%>
                <c:set var="mins" value="${row.attempt.durationSeconds div 60}" />
                <fmt:formatNumber value="${mins}" pattern="#.##"/> mins
              </td>
              <td>
                <fmt:formatDate value="${row.attempt.attemptDate}"
                                pattern="MMM dd, yyyy HH:mm"/>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
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
</body>
</html>

