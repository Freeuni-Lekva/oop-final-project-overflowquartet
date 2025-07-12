<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Quiz Website · Challenge Friend</title>
  <!-- Bootstrap 5 + Icons -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
  <style>
    body {
      min-height: 100vh;
      background: linear-gradient(135deg, #0d6efd 0%, #024ec1 100%);
      color: #fff;
      overflow-x: hidden;
    }
    .glass-card {
      backdrop-filter: blur(8px);
      background: rgba(255,255,255,0.15);
      border: 1px solid rgba(255,255,255,0.25);
      border-radius: 1rem;
      box-shadow: 0 .75rem 1.5rem rgba(0,0,0,0.3);
    }
    .challenge-icon {
      font-size: 2.5rem;
      color: #ffd700;
      filter: drop-shadow(0 0 8px #fff176);
    }
  </style>
</head>
<body>

<!-- NAVBAR -->
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
      <li class="nav-item"><a class="nav-link" href="<%= request.getContextPath() %>/achievements"><i class="bi bi-award-fill"></i> Achievements</a></li>
      <c:if test="${sessionScope.user.admin}">
      <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin"><i class="bi bi-shield-fill"></i> Admin</a></li>
      </c:if>
    </ul>
    <a href="<%= request.getContextPath() %>/LogoutServlet" class="btn btn-outline-light btn-sm">
      <i class="bi bi-box-arrow-right"></i> Log&nbsp;out
    </a>
  </div>
</nav>

<main class="container py-5">
  <div class="row justify-content-center">
    <div class="col-12 col-md-8 col-lg-6">
      <div class="glass-card p-4">
        
        <!-- Error Display -->
        <c:if test="${not empty error}">
          <div class="alert alert-danger text-center" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>${error}
          </div>
        </c:if>
        
        <!-- Success Display -->
        <c:if test="${not empty success}">
          <div class="alert alert-success text-center" role="alert">
            <i class="bi bi-check-circle-fill me-2"></i>${success}
          </div>
        </c:if>
        
        <!-- Quiz Info -->
        <div class="text-center mb-4">
          <i class="bi bi-flag-fill challenge-icon"></i>
          <h2 class="fw-bold mb-1">Challenge a Friend</h2>
          
          <c:choose>
            <c:when test="${not empty quiz}">
              <p class="mb-0">Send a challenge for <span class="fw-semibold">${quiz.title}</span></p>
              <small class="text-light">Quiz ID: ${quiz.quizId}</small>
            </c:when>
            <c:otherwise>
              <p class="mb-0">Send a challenge for <span class="fw-semibold">Quiz #${quizId}</span></p>
              <small class="text-light">Quiz ID: ${quizId}</small>
            </c:otherwise>
          </c:choose>
        </div>
        
        <!-- Challenge Form -->
        <form method="post" action="<%= request.getContextPath() %>/challenge">
          <input type="hidden" name="action" value="challenge"/>
          <input type="hidden" name="quizId" value="${quiz.quizId != null ? quiz.quizId : quizId}"/>
          
          <div class="mb-3">
            <label for="friendId" class="form-label">Select Friend</label>
            <select name="friendId" id="friendId" class="form-select" required>
              <option value="">Choose a friend...</option>
              <c:forEach var="friend" items="${friendList}">
                <option value="${friend.userId}">
                  ${friend.displayName != null ? friend.displayName : friend.username}
                </option>
              </c:forEach>
            </select>
          </div>
          
          <button class="btn btn-warning w-100 fw-semibold" type="submit">
            <i class="bi bi-flag"></i> Send Challenge
          </button>
        </form>
        
        <!-- No Friends Message -->
        <c:if test="${empty friendList}">
          <div class="text-center mt-3">
            <p class="text-light mb-2">You don't have any friends to challenge yet.</p>
            <a href="<%= request.getContextPath() %>/friends" class="btn btn-outline-light btn-sm">
              <i class="bi bi-people-fill me-1"></i> Find Friends
            </a>
          </div>
        </c:if>
        
      </div>
    </div>
  </div>
</main>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
