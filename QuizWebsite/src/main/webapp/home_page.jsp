<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Quiz Website · Home</title>

    <!-- Bootstrap 5 -->
    <link
            href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
            rel="stylesheet" />

    <!-- Bootstrap Icons (optional – used for nav + cards) -->
    <link
            rel="stylesheet"
            href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" />

    <style>
        /* brand backdrop */
        body {
            min-height: 100vh;
            background: linear-gradient(135deg, #0d6efd 0%, #024ec1 100%);
            color: #fff;
        }

        /* hero section */
        .hero {
            padding: 6rem 0 5rem;
        }

        /* glassy card look reused from login */
        .glass-card {
            backdrop-filter: blur(8px);
            background: rgba(255, 255, 255, 0.15);
            border: 1px solid rgba(255, 255, 255, 0.25);
            border-radius: 1rem;
            box-shadow: 0 0.75rem 1.5rem rgba(0, 0, 0, 0.3);
        }
        .glass-card h5 { color: #fff; }       /* force title text white on cards */
        .glass-card p  { color: #e8e8e8; }    /* softer paragraph */

        /* footer styling */
        footer {
            font-size: .875rem;
            color: rgba(255,255,255,.65);
        }
    </style>
</head>

<body>

<!-- ─── TOP NAV ─────────────────────────────────────────────────────── -->
<nav class="navbar navbar-expand-lg navbar-dark glass-card mx-3 mt-3 px-3">
    <a class="navbar-brand fw-semibold" href="<%= request.getContextPath() %>/">QuizSite</a>

    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMain">
        <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navMain">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
            <li class="nav-item"><a class="nav-link active" href="#">Home</a></li>
            <li class="nav-item"><a class="nav-link" href="<%= request.getContextPath() %>/quizzes">Quizzes</a></li>
            <li class="nav-item"><a class="nav-link" href="<%= request.getContextPath() %>/create">Create Quiz</a></li>
        </ul>

        <!-- show username if logged in -->
        <span class="navbar-text me-3">
        <!-- ${sessionScope.user.displayName} -->
      </span>
        <a href="<%= request.getContextPath() %>/LogoutServlet" class="btn btn-outline-light btn-sm">
            Log out
        </a>
    </div>
</nav>

<!-- ─── HERO ────────────────────────────────────────────────────────── -->
<header class="hero text-center">
    <div class="container">
        <h1 class="display-5 fw-bold">Test Your Knowledge, Challenge Friends!</h1>
        <p class="lead mb-4">Browse thousands of community-made quizzes or craft your own in minutes.</p>
        <a href="<%= request.getContextPath() %>/quizzes" class="btn btn-light btn-lg text-primary fw-semibold">
            Browse Quizzes
        </a>
    </div>
</header>

<!-- ─── FEATURED QUIZZES / DASHBOARD ────────────────────────────────── -->
<section class="container pb-5">
    <h2 class="h4 fw-semibold mb-3">Featured Quizzes</h2>

    <div class="row g-4">
        <!-- Loop real data here -->
        <c:forEach var="quiz" items="${featuredQuizzes}">
            <div class="col-12 col-sm-6 col-lg-4">
                <div class="glass-card p-4 h-100 d-flex flex-column">
                    <h5 class="fw-semibold mb-1">
                        <i class="bi bi-lightbulb-fill me-1"></i> ${quiz.title}
                    </h5>
                    <p class="small flex-grow-1">${quiz.description}</p>
                    <div class="d-flex justify-content-between align-items-center mt-2">
                        <span class="small"><i class="bi bi-question-circle"></i> ${quiz.questionCount} Qs</span>
                        <a href="<%= request.getContextPath() %>/quiz?id=${quiz.quizId}"
                           class="btn btn-sm btn-light text-primary fw-semibold">
                            Play
                        </a>
                    </div>
                </div>
            </div>
        </c:forEach>

        <!-- Placeholder card if you don’t have server data yet -->
        <!-- remove when JSTL loop works -->
        <div class="col-12 col-sm-6 col-lg-4 d-none" id="placeholder-card">
            <div class="glass-card p-4 h-100">
                <h5 class="fw-semibold mb-1"><i class="bi bi-stars me-1"></i> Sample Quiz</h5>
                <p class="small flex-grow-1">A demo description so you can see the layout.</p>
                <div class="d-flex justify-content-between align-items-center mt-2">
                    <span class="small"><i class="bi bi-question-circle"></i> 10 Qs</span>
                    <a href="#" class="btn btn-sm btn-light text-primary fw-semibold">Play</a>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- ─── FOOTER ──────────────────────────────────────────────────────── -->
<footer class="text-center py-3">
    © 2025 QuizMossis · Made with <i class="bi bi-heart-fill"></i> by OverflowQuartet
</footer>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
