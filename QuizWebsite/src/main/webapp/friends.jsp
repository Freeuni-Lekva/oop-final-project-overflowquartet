<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title>Quizzmosis · Friends</title>

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

  <style>
    body {
      min-height: 100vh;
      background: linear-gradient(135deg, #0d6efd 0%, #024ec1 100%);
      color: #fff;
    }
    .glass-card {
      backdrop-filter: blur(8px);
      background: rgba(255,255,255,.15);
      border: 1px solid rgba(255,255,255,.25);
      border-radius: 1rem;
      box-shadow: 0 .75rem 1.5rem rgba(0,0,0,.3);
    }
    .brand-logo {
      width: 40px; height: 40px; border-radius: 50%; margin-right: 12px;
      box-shadow: 0 1px 4px rgba(0,0,0,.15);
      background: linear-gradient(120deg,#ffc107,#0d6efd,#6f42c1);
      display: inline-block; vertical-align: middle;
      position: relative;
    }
    .brand-logo .bi {
      font-size: 1.8rem; position: absolute; left: 50%; top: 50%; transform: translate(-50%,-50%);
      color: #fff; text-shadow: 0 2px 8px #0003;
    }
  </style>
</head>
<body>
<!-- NAVBAR (same style as leaderboard) -->
<nav class="navbar navbar-expand-lg navbar-dark glass-card mx-3 mt-3 px-3">
  <a class="navbar-brand fw-bold d-flex align-items-center" href="${pageContext.request.contextPath}/">
    <span class="brand-logo"><i class="bi bi-lightning-charge-fill"></i></span>
    Quizzmosis
  </a>
  <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMain">
    <span class="navbar-toggler-icon"></span>
  </button>
  <div class="collapse navbar-collapse" id="navMain">
    <ul class="navbar-nav me-auto">
      <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/"><i class="bi bi-house-door-fill"></i> Home</a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/quizzes"><i class="bi bi-list-check"></i> Quizzes</a>
      </li>
      <li class="nav-item">
        <a class="nav-link active fw-semibold" href="#"><i class="bi bi-people-fill"></i> Friends</a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/messages"><i class="bi bi-envelope-fill"></i> Messages</a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/leaderboard"><i class="bi bi-trophy-fill"></i> Leaderboard</a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="${pageContext.request.contextPath}/achievements"><i class="bi bi-star-fill"></i> Achievements</a>
      </li>
    </ul>
    <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light btn-sm">
      <i class="bi bi-box-arrow-right"></i> Log&nbsp;out
    </a>
  </div>
</nav>

<!-- PAGE HEADER -->
<header class="text-center py-5">
  <h1 class="fw-bold display-5 mb-0"><i class="bi bi-people-fill me-2"></i>Friends</h1>
  <p class="lead">Manage your connections!</p>
</header>

<main class="container pb-5">
  <div class="glass-card p-4 mb-4">
    <!-- FRIENDS LIST -->
    <h3><i class="bi bi-people-fill"></i> Your Friends</h3>
    <c:choose>
      <c:when test="${empty friends}">
        <p class="text-light">You have no friends yet.</p>
      </c:when>
      <c:otherwise>
        <ul class="list-group mb-3">
          <c:forEach var="friend" items="${friends}">
            <li class="list-group-item d-flex align-items-center justify-content-between bg-transparent text-light">
                            <span>
                                <i class="bi bi-person-circle me-2"></i>
                                <b>${friend.displayName}</b> <small class="text-secondary">(@${friend.username})</small>
                            </span>
              <form method="post" action="${pageContext.request.contextPath}/friends" class="mb-0">
                <input type="hidden" name="action" value="remove"/>
                <input type="hidden" name="targetId" value="${friend.userId}"/>
                <button type="submit" class="btn btn-outline-danger btn-sm"><i class="bi bi-x"></i> Unfriend</button>
              </form>
            </li>
          </c:forEach>
        </ul>
      </c:otherwise>
    </c:choose>
  </div>

  <div class="glass-card p-4 mb-4">
    <!-- PENDING RECEIVED REQUESTS -->
    <h3><i class="bi bi-person-plus-fill"></i> Friend Requests Received</h3>
    <c:choose>
      <c:when test="${empty pendingReceived}">
        <p class="text-light">No pending friend requests.</p>
      </c:when>
      <c:otherwise>
        <ul class="list-group mb-3">
          <c:forEach var="req" items="${pendingReceived}">
            <li class="list-group-item d-flex align-items-center justify-content-between bg-transparent text-light">
                            <span>
                                <i class="bi bi-person-circle me-2"></i>
                                <b>${req.displayName}</b> <small class="text-secondary">(@${req.username})</small>
                            </span>
              <form method="post" action="${pageContext.request.contextPath}/friends" class="mb-0 d-flex gap-2">
                <input type="hidden" name="targetId" value="${req.userId}"/>
                <button name="action" value="accept" type="submit" class="btn btn-success btn-sm">
                  <i class="bi bi-check"></i> Accept
                </button>
                <button name="action" value="reject" type="submit" class="btn btn-outline-danger btn-sm">
                  <i class="bi bi-x"></i> Reject
                </button>
              </form>
            </li>
          </c:forEach>
        </ul>
      </c:otherwise>
    </c:choose>
  </div>

  <div class="glass-card p-4 mb-4">
    <!-- PENDING SENT REQUESTS -->
    <h3><i class="bi bi-person-plus"></i> Friend Requests Sent</h3>
    <c:choose>
      <c:when test="${empty pendingSent}">
        <p class="text-light">No outgoing friend requests.</p>
      </c:when>
      <c:otherwise>
        <ul class="list-group mb-3">
          <c:forEach var="sent" items="${pendingSent}">
            <li class="list-group-item d-flex align-items-center justify-content-between bg-transparent text-light">
                            <span>
                                <i class="bi bi-person-circle me-2"></i>
                                <b>${sent.displayName}</b> <small class="text-secondary">(@${sent.username})</small>
                            </span>
              <form method="post" action="${pageContext.request.contextPath}/friends" class="mb-0">
                <input type="hidden" name="action" value="remove"/>
                <input type="hidden" name="targetId" value="${sent.userId}"/>
                <button type="submit" class="btn btn-outline-danger btn-sm"><i class="bi bi-x"></i> Cancel Request</button>
              </form>
            </li>
          </c:forEach>
        </ul>
      </c:otherwise>
    </c:choose>
  </div>

  <div class="glass-card p-4">
    <!-- FRIEND SEARCH -->
    <h3><i class="bi bi-search"></i> Find Friends</h3>
    <form class="row g-3 mb-3" method="get" action="${pageContext.request.contextPath}/friends">
      <div class="col-10">
        <input type="text" name="search" class="form-control" placeholder="Search by username or display name" value="${param.search}"/>
      </div>
      <div class="col-2">
        <button class="btn btn-primary w-100" type="submit"><i class="bi bi-search"></i> Search</button>
      </div>
    </form>
    <c:if test="${not empty searchResults}">
      <ul class="list-group mb-3">
        <c:forEach var="user" items="${searchResults}">
          <li class="list-group-item d-flex align-items-center justify-content-between bg-transparent text-light">
                        <span>
                            <i class="bi bi-person-circle me-2"></i>
                            <b>${user.displayName}</b> <small class="text-secondary">(@${user.username})</small>
                        </span>
            <form method="post" action="${pageContext.request.contextPath}/friends" class="mb-0">
              <input type="hidden" name="action" value="send"/>
              <input type="hidden" name="targetId" value="${user.userId}"/>
              <button type="submit" class="btn btn-success btn-sm"><i class="bi bi-person-plus"></i> Add Friend</button>
            </form>
          </li>
        </c:forEach>
      </ul>
    </c:if>
  </div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
