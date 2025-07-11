<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Quiz Question · Quiz Website</title>
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
    .glass-card h5, .glass-card h4 { color: #fff; }
    .glass-card p { color: #e8e8e8; }
    .form-control, .form-select {
      background: rgba(255, 255, 255, 0.9);
      border: 1px solid rgba(255, 255, 255, 0.3);
    }
    .form-control:focus, .form-select:focus {
      background: rgba(255, 255, 255, 0.95);
      border-color: rgba(255, 255, 255, 0.5);
      box-shadow: 0 0 0 0.2rem rgba(255, 255, 255, 0.25);
    }
    .form-check-input:checked {
      background-color: #0d6efd !important;
      border-color: #0d6efd !important;
    }
    .question-image {
      max-height: 400px;
      max-width: 100%;
      border: 2px solid rgba(255, 255, 255, 0.3);
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
          <i class="bi bi-people-fill"></i> Friends</a>
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
    </ul>
    <a href="<%= request.getContextPath() %>/LogoutServlet" class="btn btn-outline-light btn-sm">
      <i class="bi bi-box-arrow-right"></i> Log&nbsp;out
    </a>
  </div>
</nav>

<main class="container py-5">
  <div class="glass-card p-4 mx-auto" style="max-width: 800px;">
    
    <c:if test="${not empty error}">
      <div class="alert alert-warning" role="alert">
        <h4 class="alert-heading"><i class="bi bi-exclamation-triangle"></i> No Questions Available</h4>
        <p>${error}</p>
        <hr>
        <a href="<%= request.getContextPath() %>/quizzes" class="btn btn-primary">
          <i class="bi bi-arrow-left"></i> Back to Quizzes
        </a>
      </div>
    </c:if>
    
    <c:if test="${not empty question}">
      <!-- Multi‐page -->
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h4 class="mb-0">Question ${page} of ${total}</h4>
        <div class="progress" style="width: 200px; height: 8px;">
          <div class="progress-bar" role="progressbar" style="width: ${(page / total) * 100}%"></div>
        </div>
      </div>
      
      <div class="mb-4">
        <h5 class="fw-semibold">${question.questionText}</h5>
        
        <!-- Display image for picture-response questions -->
        <c:if test="${question.questionType == 'picture_response' and not empty question.imageUrl}">
          <div class="text-center mb-4">
            <img src="${question.imageUrl}" alt="Question Image" 
                 class="img-fluid rounded question-image" 
                 onerror="this.style.display='none'; this.nextElementSibling.style.display='block';">
            <div class="alert alert-warning mt-2" style="display: none;">
              <i class="bi bi-exclamation-triangle"></i> Sorry, we couldn't load the image for this question. The quiz creator may need to update the image URL.
            </div>
          </div>
        </c:if>
      </div>
      
      <form action="<%= request.getContextPath() %>/submitAnswer" method="post">
        <input type="hidden" name="questionId" value="${question.questionId}"/>
        <input type="hidden" name="page" value="${page}"/>
        <input type="hidden" name="total" value="${total}"/>
        
        <c:choose>
          <c:when test="${question.questionType == 'multiple_choice'}">
            <div class="mb-4">
              <c:forEach var="c" items="${question.choices}">
                <div class="form-check mb-3">
                  <input class="form-check-input" type="radio"
                         name="answer" id="c${c.choiceId}"
                         value="${c.choiceId}" required/>
                  <label class="form-check-label" for="c${c.choiceId}">
                    ${c.choiceText}
                  </label>
                </div>
              </c:forEach>
            </div>
          </c:when>
          <c:otherwise>
            <div class="mb-4">
              <input type="text" name="answerText" class="form-control form-control-lg" 
                     placeholder="Enter your answer..." required/>
            </div>
          </c:otherwise>
        </c:choose>

        <c:if test="${not empty feedbackCorrect}">
          <div class="alert ${feedbackCorrect ? 'alert-success' : 'alert-danger'} mb-4">
            <i class="bi ${feedbackCorrect ? 'bi-check-circle' : 'bi-x-circle'}"></i>
            You answered "${userAnswer}" —
            <strong>${feedbackCorrect ? 'Correct!' : 'Incorrect.'}</strong>
          </div>
        </c:if>

        <div class="d-flex justify-content-between">
          <c:if test="${page > 1}">
            <a href="<%= request.getContextPath() %>/showQuestion?page=${page - 1}" 
               class="btn btn-outline-light">
              <i class="bi bi-arrow-left"></i> Previous
            </a>
          </c:if>
          <button class="btn btn-light text-primary fw-semibold" type="submit">
            <c:choose>
              <c:when test="${page == total}">
                <i class="bi bi-check-circle"></i> Finish Quiz
              </c:when>
              <c:otherwise>
                <i class="bi bi-arrow-right"></i> Next Question
              </c:otherwise>
            </c:choose>
          </button>
        </div>
      </form>
    </c:if>

    <c:if test="${not empty questions}">
      <!-- Single‐page -->
      <h4 class="mb-4">Complete Quiz</h4>
      <form action="<%= request.getContextPath() %>/submitAnswer" method="post">
        <c:forEach var="q" items="${questions}" varStatus="st">
          <div class="mb-4 p-3" style="background: rgba(255, 255, 255, 0.1); border-radius: 0.5rem;">
            <h5 class="fw-semibold mb-3">Question ${st.index + 1}: ${q.questionText}</h5>
            
            <!-- Display image for picture-response questions in single-page mode -->
            <c:if test="${q.questionType == 'picture_response' and not empty q.imageUrl}">
              <div class="text-center mb-3">
                <img src="${q.imageUrl}" alt="Question Image" 
                     class="img-fluid rounded question-image" 
                     onerror="this.style.display='none'; this.nextElementSibling.style.display='block';">
                <div class="alert alert-warning mt-2" style="display: none;">
                  <i class="bi bi-exclamation-triangle"></i> Sorry, we couldn't load the image for this question. The quiz creator may need to update the image URL.
                </div>
              </div>
            </c:if>
            <c:choose>
              <c:when test="${q.questionType == 'multiple_choice'}">
                <c:forEach var="c" items="${q.choices}">
                  <div class="form-check mb-2">
                    <input class="form-check-input" type="radio"
                           name="answer_${q.questionId}"
                           id="q${q.questionId}c${c.choiceId}"
                           value="${c.choiceId}" required/>
                    <label class="form-check-label"
                           for="q${q.questionId}c${c.choiceId}">
                        ${c.choiceText}
                    </label>
                  </div>
                </c:forEach>
              </c:when>
              <c:otherwise>
                <input type="text" name="answerText_${q.questionId}"
                       class="form-control" placeholder="Enter your answer..." required/>
              </c:otherwise>
            </c:choose>
          </div>
        </c:forEach>
        <button class="btn btn-success btn-lg w-100">
          <i class="bi bi-check-circle"></i> Finish Quiz
        </button>
      </form>
    </c:if>
    
    <c:if test="${empty question and empty questions and empty error}">
      <div class="text-center">
        <div class="spinner-border text-light mb-3" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
        <h4>Loading Quiz...</h4>
        <p>Please wait while we load your quiz questions.</p>
      </div>
    </c:if>
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
