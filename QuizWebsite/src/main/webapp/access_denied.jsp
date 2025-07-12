<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width,initial-scale=1"/>
    <title>Access Denied · Quizzmosis</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet"/>
    <style>
        body { min-height:100vh; background:linear-gradient(135deg,#0d6efd,#024ec1); color:#fff; }
        .glass-card {
            backdrop-filter:blur(8px);
            background:rgba(255,255,255,0.15);
            border:1px solid rgba(255,255,255,0.25);
            border-radius:1rem;
            box-shadow:0 .75rem 1.5rem rgba(0,0,0,0.3);
        }
    </style>
</head>
<body>

<!-- NAVBAR -->
<nav class="navbar navbar-expand-lg navbar-dark glass-card mx-3 mt-3 px-3">
    <a class="navbar-brand fw-bold" href="${ctx}/HomeServlet">Quizzmosis</a>
    <div class="navbar-nav ms-auto">
        <a href="${ctx}/HomeServlet" class="btn btn-outline-light btn-sm">
            <i class="bi bi-house-door-fill"></i> Go Home
        </a>
    </div>
</nav>

<!-- MAIN CONTENT -->
<main class="container d-flex align-items-center justify-content-center" style="min-height: 80vh;">
    <div class="text-center">
        <div class="glass-card p-5">
            <i class="bi bi-shield-exclamation display-1 text-warning mb-4"></i>
            <h1 class="display-5 fw-bold mb-3">Access Denied</h1>
            <p class="lead mb-4">You don't have permission to access this page. Administrator privileges are required.</p>
            <div class="d-flex justify-content-center gap-3">
                <a href="${ctx}/HomeServlet" class="btn btn-light btn-lg">
                    <i class="bi bi-house-door-fill me-2"></i>Go Home
                </a>
                <a href="${ctx}/LogoutServlet" class="btn btn-outline-light btn-lg">
                    <i class="bi bi-box-arrow-right me-2"></i>Logout
                </a>
            </div>
        </div>
    </div>
</main>

<!-- FOOTER -->
<footer class="text-center py-3">
    &copy; 2025 Quizzmosis &middot; Made with <i class="bi bi-heart-fill"></i> by OverflowQuartet
</footer>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html> 