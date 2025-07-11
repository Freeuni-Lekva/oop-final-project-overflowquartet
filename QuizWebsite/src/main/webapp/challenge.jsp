<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title>Challenge a Friend · ${quiz.title}</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>
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
    .challenge-icon {
      font-size: 2.5rem;
      color: #ffd700;
      filter: drop-shadow(0 0 8px #fff176);
    }
  </style>
</head>
<body>
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
        <a class="nav-link" href="<%= request.getContextPath() %>/history"><i class="bi bi-clock-history"></i> History</a>
      </li>
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
        <div class="text-center mb-4">
          <i class="bi bi-flag-fill challenge-icon"></i>
          <h2 class="fw-bold mb-1">Challenge a Friend</h2>
          <p class="mb-0">Send a challenge for <span class="fw-semibold">${quiz.title}</span></p>
        </div>
        <div id="challenge-success" class="alert alert-success d-none text-center" role="alert">
          <i class="bi bi-check-circle-fill me-1"></i> Challenge sent!
        </div>
        <form id="challengeForm" method="post" action="${pageContext.request.contextPath}/messages">
          <input type="hidden" name="action"  value="challenge"/>
          <input type="hidden" name="quizId"  value="${quiz.quizId}"/>
          <div class="mb-3">
            <label for="friendId" class="form-label">Select Friend</label>
            <select name="friendId" id="friendId" class="form-select" required>
              <option value="">Choose a friend...</option>
              <c:forEach var="f" items="${friends}">
                <option value="${f.userId}">${f.displayName}</option>
              </c:forEach>
            </select>
          </div>
          <button id="challengeBtn" class="btn btn-warning w-100 fw-semibold" type="submit">
            <i class="bi bi-flag"></i> Send Challenge
          </button>
        </form>
      </div>
    </div>
  </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
// AJAX challenge form
const form = document.getElementById('challengeForm');
const btn = document.getElementById('challengeBtn');
const successMsg = document.getElementById('challenge-success');
form.addEventListener('submit', function(e) {
  e.preventDefault();
  btn.disabled = true;
  btn.innerHTML = '<i class="bi bi-check-circle-fill"></i> Challenged!';
  fetch(form.action, {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: new URLSearchParams(new FormData(form)).toString(),
    credentials: 'same-origin'
  })
  .then(res => {
    if (res.ok) {
      successMsg.classList.remove('d-none');
      setTimeout(() => { btn.disabled = false; btn.innerHTML = '<i class="bi bi-flag"></i> Send Challenge'; }, 2000);
    } else {
      btn.disabled = false;
      btn.innerHTML = '<i class="bi bi-flag"></i> Send Challenge';
      alert('Failed to send challenge.');
    }
  })
  .catch(() => {
    btn.disabled = false;
    btn.innerHTML = '<i class="bi bi-flag"></i> Send Challenge';
    alert('Failed to send challenge.');
  });
});
</script>
</body>
</html>
