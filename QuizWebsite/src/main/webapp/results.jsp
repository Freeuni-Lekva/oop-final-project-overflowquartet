<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Quiz Results · Quiz Website</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
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
    .glass-card h3, .glass-card h5 { color: #fff; }
    .table {
      background: rgba(255, 255, 255, 0.1);
      border-radius: 0.5rem;
      overflow: hidden;
    }
    .table th {
      background: rgba(255, 255, 255, 0.2);
      color: #fff;
      border-color: rgba(255, 255, 255, 0.3);
    }
    .table td {
      color: #333;
      background: rgba(255, 255, 255, 0.9);
      border-color: rgba(255, 255, 255, 0.3);
    }
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
        <a class="nav-link" href="<%= request.getContextPath() %>/quizzes">
          <i class="bi bi-list-check"></i> Quizzes
        </a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="<%= request.getContextPath() %>/create.jsp">
          <i class="bi bi-plus-circle"></i> Create Quiz
        </a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="<%= request.getContextPath() %>/leaderboard">
          <i class="bi bi-trophy-fill"></i> Leaderboard
        </a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="<%= request.getContextPath() %>/friends">
          <i class="bi bi-people-fill"></i> Friends
        </a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="<%= request.getContextPath() %>/messages">
          <i class="bi bi-envelope-fill"></i> Messages
        </a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="<%= request.getContextPath() %>/history">
          <i class="bi bi-clock-history"></i> History
        </a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="<%= request.getContextPath() %>/achievements"><i class="bi bi-award-fill"></i> Achievements</a>
      </li>
      <c:if test="${sessionScope.user.admin}">
      <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/admin"><i class="bi bi-shield-fill"></i> Admin</a>
      </li>
      </c:if>
    </ul>
    <a href="<%= request.getContextPath() %>/LogoutServlet" class="btn btn-outline-light btn-sm">
      <i class="bi bi-box-arrow-right"></i> Log&nbsp;out
    </a>
  </div>
</nav>

<main class="container py-5">
  <div class="glass-card p-4 mx-auto" style="max-width: 800px;">
    
    <div class="text-center mb-4">
      <h2 class="fw-bold mb-3">
        <i class="bi bi-trophy-fill text-warning"></i> Quiz Results
      </h2>
      
      <div class="row text-center">
        <div class="col-md-6">
          <div class="p-3" style="background: rgba(255, 255, 255, 0.1); border-radius: 0.5rem;">
            <h4 class="mb-1">${attempt.score}</h4>
            <p class="mb-0 text-light">Score</p>
          </div>
        </div>
        <div class="col-md-6">
          <div class="p-3" style="background: rgba(255, 255, 255, 0.1); border-radius: 0.5rem;">
            <h4 class="mb-1">${attempt.durationSeconds}s</h4>
            <p class="mb-0 text-light">Time Taken</p>
          </div>
        </div>
      </div>
    </div>

    <div class="table-responsive">
      <table class="table table-hover">
        <thead>
          <tr>
            <th><i class="bi bi-question-circle me-2"></i>Question</th>
            <th><i class="bi bi-chat-text me-2"></i>Your Answer</th>
            <th class="text-center"><i class="bi bi-check-circle me-2"></i>Result</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="a" items="${answers}">
            <tr>
              <td>${a.questionText}</td>
              <td>${a.userAnswerText}</td>
              <td class="text-center">
                <c:choose>
                  <c:when test="${a.isCorrect}">
                    <span class="badge bg-success">
                      <i class="bi bi-check-circle"></i> Correct
                    </span>
                  </c:when>
                  <c:otherwise>
                    <span class="badge bg-danger">
                      <i class="bi bi-x-circle"></i> Incorrect
                    </span>
                  </c:otherwise>
                </c:choose>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>

    <div class="text-center mt-4">
      <a href="<%= request.getContextPath() %>/quizzes" 
         class="btn btn-light text-primary fw-semibold me-3">
        <i class="bi bi-list-check"></i> Back to Quizzes
      </a>
      <a href="<%= request.getContextPath() %>/HomeServlet" 
         class="btn btn-outline-light">
        <i class="bi bi-house-door"></i> Go Home
      </a>
    </div>
  </div>
</main>

<footer class="text-center py-3">
  © 2025 Quizzmosis · Made with <i class="bi bi-heart-fill"></i> by OverflowQuartet
</footer>

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
