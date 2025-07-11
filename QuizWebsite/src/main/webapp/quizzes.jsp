<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Quiz Website · All Quizzes</title>
  <!-- Bootstrap 5 + Icons -->
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
      background: rgba(255, 255, 255, 0.15);
      border: 1px solid rgba(255, 255, 255, 0.25);
      border-radius: 1rem;
      box-shadow: 0 0.75rem 1.5rem rgba(0, 0, 0, 0.3);
    }
    .glass-card h5 { color: #fff; }
    .glass-card p  { color: #e8e8e8; }
  </style>
</head>
<body>
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
        <a class="nav-link"
           href="<%= request.getContextPath() %>/HomeServlet">
          <i class="bi bi-house-door-fill"></i> Home
        </a>
      </li>
      <li class="nav-item">
        <a class="nav-link active fw-semibold" href="<%= request.getContextPath() %>/quizzes"><i class="bi bi-list-check"></i> Quizzes</a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="<%= request.getContextPath() %>/create.jsp"><i class="bi bi-plus-circle"></i> Create Quiz</a>
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
        <a class="nav-link" href="<%= request.getContextPath() %>/history"><i class="bi bi-clock-history"></i> History</a>
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
<main class="container pb-5">
  <h1 class="fw-bold display-5 text-center my-4">All Quizzes</h1>
  
  <!-- POPULAR QUIZZES SECTION -->
  <c:if test="${not empty popularQuizzes}">
    <div class="mb-5">
      <h2 class="h4 fw-semibold mb-3">
        <i class="bi bi-fire"></i> Popular Quizzes
      </h2>
      <div class="row g-4">
        <c:forEach var="quiz" items="${popularQuizzes}">
          <div class="col-12 col-sm-6 col-lg-4">
            <div class="glass-card p-4 h-100 d-flex flex-column position-relative">
              <!-- Popular badge -->
              <div class="position-absolute top-0 end-0 mt-2 me-2">
                <span class="badge bg-warning text-dark">
                  <i class="bi bi-fire"></i> Popular
                </span>
              </div>
              <h5 class="fw-semibold mb-1">
                <i class="bi bi-lightbulb-fill me-1"></i> 
                <a href="<%= request.getContextPath() %>/quiz-summary?quizId=${quiz.quizId}" class="link-light text-decoration-none">
                  ${quiz.title}
                </a>
              </h5>
              <p class="small flex-grow-1">${quiz.description}</p>
              <p class="small text-light mb-2">
                <i class="bi bi-person-circle me-1"></i> by ${quiz.ownerUsername}
              </p>
              <div class="d-flex justify-content-between align-items-center mt-2">
                <span class="small">
                  <i class="bi bi-question-circle"></i> ${quiz.questionCount} Qs
                  <span class="ms-2"><i class="bi bi-people"></i> ${quiz.attemptCount} attempts</span>
                </span>
                <c:choose>
                  <c:when test="${quiz.questionCount == 0}">
                    <a href="${pageContext.request.contextPath}/add-questions?quizId=${quiz.quizId}"
                       class="btn btn-sm btn-warning fw-semibold">
                      <i class="bi bi-plus-circle me-1"></i> Add Questions
                    </a>
                  </c:when>
                  <c:otherwise>
                    <a href="${pageContext.request.contextPath}/startQuiz?quizId=${quiz.quizId}"
                       class="btn btn-sm btn-warning text-dark fw-semibold">
                      <i class="bi bi-play-fill me-1"></i> Start Quiz
                    </a>
                  </c:otherwise>
                </c:choose>
              </div>
            </div>
          </div>
        </c:forEach>
      </div>
    </div>
  </c:if>
  
  <!-- MY QUIZZES SECTION -->
  <c:if test="${not empty myQuizzes}">
    <div class="mb-5">
      <h2 class="h4 fw-semibold mb-3">
        <i class="bi bi-person-circle"></i> My Quizzes
      </h2>
      <div class="row g-4">
        <c:forEach var="quiz" items="${myQuizzes}">
          <div class="col-12 col-sm-6 col-lg-4">
            <div class="glass-card p-4 h-100 d-flex flex-column">
              <h5 class="fw-semibold mb-1">
                <i class="bi bi-lightbulb-fill me-1"></i> 
                <a href="<%= request.getContextPath() %>/quiz-summary?quizId=${quiz.quizId}" class="link-light text-decoration-none">
                  ${quiz.title}
                </a>
              </h5>
              <p class="small flex-grow-1">${quiz.description}</p>
              <p class="small text-light mb-2">
                <i class="bi bi-person-circle me-1"></i> by ${quiz.ownerUsername}
              </p>
              <div class="d-flex justify-content-between align-items-center mt-2">
                <span class="small">
                  <i class="bi bi-question-circle"></i> ${quiz.questionCount} Qs
                </span>
                <c:choose>
                  <c:when test="${quiz.questionCount == 0}">
                    <a href="${pageContext.request.contextPath}/add-questions?quizId=${quiz.quizId}"
                       class="btn btn-sm btn-warning fw-semibold">
                      <i class="bi bi-plus-circle me-1"></i> Add Questions
                    </a>
                  </c:when>
                  <c:otherwise>
                    <a href="${pageContext.request.contextPath}/startQuiz?quizId=${quiz.quizId}"
                       class="btn btn-sm btn-light text-primary fw-semibold">
                      <i class="bi bi-play-fill me-1"></i> Start Quiz
                    </a>
                  </c:otherwise>
                </c:choose>
              </div>
            </div>
          </div>
        </c:forEach>
      </div>
    </div>
  </c:if>
  
  <!-- ALL QUIZZES SECTION -->
  <div class="mb-5">
    <h2 class="h4 fw-semibold mb-3">All Quizzes</h2>
    <div class="row g-4">
      <c:forEach var="quiz" items="${allQuizzes}">
      <div class="col-12 col-sm-6 col-lg-4">
        <div class="glass-card p-4 h-100 d-flex flex-column">
          <h5 class="fw-semibold mb-1">
            <i class="bi bi-lightbulb-fill me-1"></i> 
            <a href="<%= request.getContextPath() %>/quiz-summary?quizId=${quiz.quizId}" class="link-light text-decoration-none">
              ${quiz.title}
            </a>
          </h5>
          <p class="small flex-grow-1">${quiz.description}</p>
          <p class="small text-light mb-2">
            <i class="bi bi-person-circle me-1"></i> by ${quiz.ownerUsername}
          </p>
          <div class="d-flex justify-content-between align-items-center mt-2">
  <span class="small">
    <i class="bi bi-question-circle"></i> ${quiz.questionCount} Qs
  </span>
            <c:choose>
              <c:when test="${quiz.questionCount == 0}">
                <a href="${pageContext.request.contextPath}/add-questions?quizId=${quiz.quizId}"
                   class="btn btn-sm btn-warning fw-semibold">
                  <i class="bi bi-plus-circle me-1"></i> Add Questions
                </a>
              </c:when>
              <c:otherwise>
                <a href="${pageContext.request.contextPath}/startQuiz?quizId=${quiz.quizId}"
                   class="btn btn-sm btn-light text-primary fw-semibold">
                  <i class="bi bi-play-fill me-1"></i> Start Quiz
                </a>
              </c:otherwise>
            </c:choose>
          </div>

        </div>
      </div>
    </c:forEach>

    <c:if test="${empty allQuizzes}">
      <div class="col-12">
        <div class="glass-card p-4 text-center">
          <h5 class="fw-semibold mb-1"><i class="bi bi-stars me-1"></i> No quizzes found</h5>
          <p class="small flex-grow-1">Be the first to create a quiz!</p>
          <a href="create_quiz.jsp" class="btn btn-light text-primary fw-semibold">Create Quiz</a>
        </div>
      </div>
    </c:if>
  </div>

</main>
<footer class="text-center py-3 text-white-50">
  © 2025 Quizzmosis · Made with <i class="bi bi-heart-fill"></i> by OverflowQuartet
</footer>

<!-- Bootstrap JS -->
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