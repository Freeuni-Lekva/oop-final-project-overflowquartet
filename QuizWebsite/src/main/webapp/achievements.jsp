<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Quiz Website · Achievements</title>
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
      background: rgba(255, 255, 255, 0.15);
      border: 1px solid rgba(255, 255, 255, 0.25);
      border-radius: 1rem;
      box-shadow: 0 0.75rem 1.5rem rgba(0, 0, 0, 0.3);
    }
    .oscar-trophy {
      font-size: 3.7rem;
      color: #ffe066;
      filter: drop-shadow(0 0 16px #fff176);
      margin-bottom: 0.5rem;
      animation: shine 2s infinite alternate;
    }
    @keyframes shine {
      0% { filter: drop-shadow(0 0 8px #fff176); }
      100% { filter: drop-shadow(0 0 32px #fff176); }
    }
    .firework {
      position: absolute;
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background: #fff176;
      opacity: 0.8;
      pointer-events: none;
      animation: explode 1.2s ease-out forwards;
    }
    @keyframes explode {
      0% { transform: scale(0.5) translateY(0); opacity: 1; }
      80% { opacity: 1; }
      100% { transform: scale(1.5) translateY(-120px); opacity: 0; }
    }
    .achievement-icon {
      font-size: 2.5rem;
      margin: 0.5rem;
      color: #ffd700;
      transition: transform 0.2s;
    }
    .achievement-icon:hover {
      transform: scale(1.15);
      color: #fff176;
    }
    .achievements-row {
      display: flex;
      flex-wrap: wrap;
      justify-content: center;
      gap: 2rem;
      margin-top: 2rem;
    }
    .achievement-card {
      background: rgba(255,255,255,0.10);
      border-radius: 1rem;
      padding: 1.5rem 1.2rem;
      min-width: 180px;
      max-width: 220px;
      text-align: center;
      box-shadow: 0 0.5rem 1rem rgba(0,0,0,0.15);
      transition: transform 0.2s, box-shadow 0.2s;
      position: relative;
    }
    .achievement-card:hover {
      transform: translateY(-6px) scale(1.04);
      box-shadow: 0 1.5rem 2rem rgba(0,0,0,0.25);
    }
    .achievement-title {
      color: #fff;
      font-weight: 600;
      margin-top: 0.5rem;
      margin-bottom: 0.25rem;
    }
    .achievement-desc {
      color: #e8e8e8;
      font-size: 0.95em;
    }
  </style>
</head>
<body>
<!-- Fireworks container -->
<div id="fireworks" style="position:fixed;top:0;left:0;width:100vw;height:100vh;pointer-events:none;z-index:10;"></div>
<!-- Navbar -->
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
        <a class="nav-link" href="#"><i class="bi bi-trophy-fill"></i> Leaderboard</a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="<%= request.getContextPath() %>/friends"><i class="bi bi-people-fill"></i> Friends</a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/messages">
          <i class="bi bi-envelope-fill"></i> Messages
        </a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="<%= request.getContextPath() %>/history"><i class="bi bi-clock-history"></i> History</a>
      </li>
      <li class="nav-item">
        <a class="nav-link active fw-semibold" href="#"><i class="bi bi-award-fill"></i> Achievements</a>
      </li>
    </ul>
    <a href="<%= request.getContextPath() %>/LogoutServlet" class="btn btn-outline-light btn-sm">
      <i class="bi bi-box-arrow-right"></i> Log&nbsp;out
    </a>
  </div>
</nav>
<main class="container pb-5 position-relative">
  <div class="text-center mt-5 mb-2">
    <i class="bi bi-patch-check-fill oscar-trophy"></i>
    <h1 class="fw-bold display-5 mb-1">Your Achievements</h1>
    <p class="lead mb-0" style="color:#fff176;">Celebrate your quiz journey!</p>
  </div>
  <div class="achievements-row">
    <c:forEach var="ach" items="${userAchievements}">
      <div class="achievement-card">
        <span class="position-relative" data-bs-toggle="tooltip" data-bs-title="${ach.description}">
          <i class="achievement-icon bi ${ach.iconUrl}"></i>
        </span>
        <div class="achievement-title">${ach.name}</div>
        <div class="achievement-desc">${ach.description}</div>
      </div>
    </c:forEach>
    <c:if test="${empty userAchievements}">
      <div class="achievement-card text-center">
        <i class="bi bi-emoji-frown fs-1" style="color:#fff176;"></i>
        <div class="achievement-title">No achievements yet</div>
        <div class="achievement-desc">Start playing and creating quizzes to earn your first badge!</div>
      </div>
    </c:if>
  </div>
</main>
<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
  // Bootstrap tooltips
  var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
  tooltipTriggerList.map(function (tooltipTriggerEl) {
    return new bootstrap.Tooltip(tooltipTriggerEl);
  });

  // Fireworks effect
  function randomColor() {
    const colors = ['#fff176', '#ffd700', '#ff4081', '#40c4ff', '#7c4dff', '#00e676'];
    return colors[Math.floor(Math.random() * colors.length)];
  }
  function launchFirework() {
    const fw = document.getElementById('fireworks');
    const x = Math.random() * window.innerWidth * 0.8 + window.innerWidth * 0.1;
    const y = Math.random() * window.innerHeight * 0.3 + window.innerHeight * 0.1;
    for (let i = 0; i < 18; i++) {
      const dot = document.createElement('div');
      dot.className = 'firework';
      dot.style.left = x + 'px';
      dot.style.top = y + 'px';
      dot.style.background = randomColor();
      dot.style.transform = `rotate(${i * 20}deg) translateY(0)`;
      fw.appendChild(dot);
      setTimeout(() => fw.removeChild(dot), 1200);
    }
  }
  // Launch a few fireworks on page load
  for (let i = 0; i < 5; i++) {
    setTimeout(launchFirework, 400 + i * 500);
  }
</script>
</body>
</html> 
