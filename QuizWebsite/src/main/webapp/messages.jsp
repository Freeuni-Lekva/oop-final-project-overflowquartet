<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title>Quiz Website · Messages</title>
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
    .nav-pills .nav-link {
      background: rgba(255,255,255,0.18);
      color: #fff;
      font-weight: 600;
      transition: background 0.2s, color 0.2s;
    }
    .nav-pills .nav-link:not(.active):hover {
      background: rgba(255,255,255,0.32);
      color: #ffd700;
    }
    .nav-pills .nav-link.active {
      background: #0d6efd !important;
      color: #fff !important;
      box-shadow: 0 2px 8px #0d6efd33;
    }
    .tab-pane > .mb-3 {
      background: rgba(255,255,255,0.10);
      padding: 1rem;
      border-radius: 0.75rem;
    }
  </style>
</head>
<body>

<!-- NAVBAR -->
<nav class="navbar navbar-expand-lg navbar-dark glass-card mx-3 mt-3 px-3">
  <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/HomeServlet">Quizzmosis</a>
  <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMain">
    <span class="navbar-toggler-icon"></span>
  </button>
  <div class="collapse navbar-collapse" id="navMain">
    <ul class="navbar-nav me-auto">
      <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/HomeServlet"><i class="bi bi-house-door-fill"></i> Home</a></li>
      <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/quizzes"><i class="bi bi-list-check"></i> Quizzes</a></li>
      <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/create"><i class="bi bi-plus-circle"></i> Create Quiz</a></li>
      <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/leaderboard"><i class="bi bi-trophy-fill"></i> Leaderboard</a></li>
      <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/friends"><i class="bi bi-people-fill"></i> Friends</a></li>
      <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/messages"><i class="bi bi-envelope-fill"></i> Messages</a></li>
      <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/history"><i class="bi bi-clock-history"></i> History</a></li>
    </ul>
    <a href="${pageContext.request.contextPath}/LogoutServlet" class="btn btn-outline-light btn-sm">
      <i class="bi bi-box-arrow-right"></i> Log out
    </a>
  </div>
</nav>

<!-- HEADER -->
<header class="text-center py-5">
  <h1 class="display-6 fw-bold">Your Messages</h1>
  <p class="lead">Handle friend requests, challenges, and notes.</p>
</header>

<!-- TABS -->
<main class="container" style="padding-top:2.5rem;">
  <ul class="nav nav-pills justify-content-center mb-4" id="messageTabs" role="tablist">
    <li class="nav-item" role="presentation">
      <button class="nav-link active fw-bold" id="tab-friend" data-bs-toggle="pill" data-bs-target="#section-friend" type="button" role="tab">
        <i class="bi bi-person-plus-fill"></i> Friend Requests
      </button>
    </li>
    <li class="nav-item" role="presentation">
      <button class="nav-link fw-bold" id="tab-challenge" data-bs-toggle="pill" data-bs-target="#section-challenge" type="button" role="tab">
        <i class="bi bi-flag-fill"></i> Challenges
      </button>
    </li>
    <li class="nav-item" role="presentation">
      <button class="nav-link fw-bold" id="tab-note" data-bs-toggle="pill" data-bs-target="#section-note" type="button" role="tab">
        <i class="bi bi-chat-left-text-fill"></i> Notes
      </button>
    </li>
  </ul>

  <div class="tab-content glass-card p-4">
    <!-- FRIEND REQUESTS -->
    <div class="tab-pane fade show active" id="section-friend" role="tabpanel">
      <c:if test="${not empty pendingReceived}">
        <h5 class="text-light">Pending Requests (Friends table)</h5>
        <c:forEach var="u" items="${pendingReceived}">
          <div class="mb-3 d-flex justify-content-between align-items-center">
            <div>
              <i class="bi bi-person-circle me-2"></i>
              <a href="${pageContext.request.contextPath}/profile?id=${u.userId}" class="link-light fw-bold">
                  ${u.displayName != null ? u.displayName : u.username}
              </a>
            </div>
            <div class="btn-group">
              <form method="post" action="${pageContext.request.contextPath}/messages">
                <input type="hidden" name="senderId" value="${u.userId}"/>
                <button type="submit" name="action" value="accept" class="btn btn-success btn-sm">
                  <i class="bi bi-check"></i> Accept
                </button>
              </form>
              <form method="post" action="${pageContext.request.contextPath}/messages">
                <input type="hidden" name="senderId" value="${u.userId}"/>
                <button type="submit" name="action" value="reject" class="btn btn-outline-danger btn-sm">
                  <i class="bi bi-x"></i> Reject
                </button>
              </form>
            </div>
          </div>
        </c:forEach>
      </c:if>


      <c:if test="${empty pendingReceived && empty friendRequests}">
        <p class="text-light">No pending friend requests.</p>
      </c:if>
    </div>

    <!-- CHALLENGES -->
    <div class="tab-pane fade" id="section-challenge" role="tabpanel">
      <c:if test="${not empty challenges}">
        <h4 class="text-center mb-4"><i class="bi bi-flag-fill text-warning"></i> Challenges</h4>
        <c:forEach var="row" items="${challenges}">
          <c:set var="sender" value="${row.sender}"/>
          <c:set var="quiz" value="${row.quiz}"/>
          <c:set var="best" value="${row.bestScore}"/>
          <div class="mb-3">
            <i class="bi bi-person-circle me-1"></i>
            <a href="${pageContext.request.contextPath}/profile?id=${sender.userId}" class="link-light fw-semibold">
                ${sender.displayName}
            </a>
            challenged you to take
            <a href="${pageContext.request.contextPath}/quiz?id=${quiz.quizId}" class="text-light fw-bold text-decoration-underline">
                ${quiz.title}
            </a>.
            <br/>
            <small>Their best score: <span class="fw-bold">${best}</span></small>
          </div>
        </c:forEach>
      </c:if>
    </div>

    <!-- NOTES -->
    <div class="tab-pane fade" id="section-note" role="tabpanel">
      <c:if test="${not empty notes}">
        <h4 class="text-center mb-4"><i class="bi bi-chat-left-text-fill"></i> Notes</h4>
        <c:forEach var="row" items="${notes}">
          <c:set var="sender" value="${row.sender}"/>
          <c:set var="m" value="${row.message}"/>
          <div class="mb-3">
            <i class="bi bi-person-circle me-1"></i>
            <a href="${pageContext.request.contextPath}/profile?id=${sender.userId}" class="link-primary fw-semibold">
                ${sender.displayName}
            </a>: ${m.content}
          </div>
        </c:forEach>
      </c:if>
    </div>
  </div>
</main>

<footer class="text-center py-3 text-white-50 fixed-bottom bg-transparent" style="backdrop-filter: blur(8px);">
  © 2025 Quizzmosis · Made with <i class="bi bi-heart-fill"></i> by OverflowQuartet
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
  window.addEventListener('pageshow', evt => {
    if (evt.persisted) window.location.reload();
  });
</script>
</body>
</html>
