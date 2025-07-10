<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Quiz Website · Sign In</title>

    <!-- Bootstrap 5 -->
    <link
            href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
            rel="stylesheet">

    <style>
        /* full-height blue background */
        body {
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            background: linear-gradient(135deg, #0d6efd 0%, #024ec1 100%);
        }

        /* glassy card */
        .login-card {
            backdrop-filter: blur(8px);
            background: rgba(255, 255, 255, 0.15);
            border: 1px solid rgba(255, 255, 255, 0.25);
            border-radius: 1rem;
            box-shadow: 0 0.75rem 1.5rem rgba(0, 0, 0, 0.25);
            max-width: 400px;
            width: 100%;
        }

        /* white inputs on translucent bg look odd—use a subtle tint */
        .login-card .form-control,
        .login-card .form-check-input {
            background: rgba(255,255,255,0.85);
        }
        /* ─── Custom validation palette ────────────────────────────── */
        /* Pick any hue you like; this one is a warm coral-pink */
        :root {
            --bs-danger: #CC0000;          /* text & border colour */
            --bs-danger-rgb: 255, 122, 122;
        }

        /* input border + glow */
        .was-validated .form-control:invalid,
        .form-control.is-invalid {
            border-color: var(--bs-danger);

        }

        /* help text colour */
        .invalid-feedback,
        .valid-feedback {
            /* keep text readable on translucent card */
            color: var(--bs-danger);
        }

        .was-validated .form-check-input:valid,
        .form-check-input.is-valid {
            background-color: rgba(255,255,255,0.85) !important;
            border-color: rgba(255,255,255,0.6)   !important;
            box-shadow: none              !important;
            background-image: none         !important;
        }

        /* 2) the label that turns green */
        .was-validated .form-check-input:valid ~ .form-check-label,
        .form-check-input.is-valid      ~ .form-check-label {
            color: inherit                !important;
        }

        .signup-link:hover,
        .signup-link:focus {
            text-decoration: underline;
        }

    </style>
</head>

<body>
<h1 class="position-absolute top-0 start-50 translate-middle-x mt-4
             fw-bold text-white display-6 user-select-none"
    style="letter-spacing: .5px;">
    Quizz<span class="text-warning">mosis</span>
</h1>


<main class="login-card p-4 p-md-5 text-white">

    <h1 class="h3 fw-bold mb-4 text-center">Log In</h1>

    <!-- ★ change action to your servlet/controller path -->
    <form action="${pageContext.request.contextPath}/login" method="post" class="needs-validation" novalidate>
        <div class="mb-3">
            <label for="username" class="form-label">Username</label>
            <input
                    type="text"
                    class="form-control"
                    id="username"
                    name="username"
                    required
                    autofocus>
            <div class="invalid-feedback">Username required.</div>
        </div>

        <div class="mb-3">
            <label for="password" class="form-label">Password</label>
            <input
                    type="password"
                    class="form-control"
                    id="password"
                    name="password"
                    required>
            <div class="invalid-feedback">Password required.</div>
        </div>

<%--        <div class="d-flex justify-content-between align-items-center mb-4">--%>
<%--            <div class="form-check">--%>
<%--                <input class="form-check-input" type="checkbox" id="remember" name="remember">--%>
<%--                <label class="form-check-label" for="remember">Remember me</label>--%>
<%--            </div>--%>
<%--            <a href="#" class="link-light link-underline-opacity-0 small">Forgot password?</a>--%>
<%--        </div>--%>

        <button class="btn btn-light text-primary fw-semibold w-100 py-2 mt-4" type="submit">
            Sign in
        </button>

        <p class="text-center small mb-0">
            New here?
            <a href="${pageContext.request.contextPath}/register" class="fw-semibold link-light link-underline-opacity-0">
                Create an account
            </a>
        </p>
    </form>
</main>

<!-- Bootstrap JS (for validation helpers, etc.) -->
<script
        src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<!-- client-side validation -->
<script>
    (() => {
        'use strict';
        const forms = document.querySelectorAll('.needs-validation');
        Array.from(forms).forEach(form => {
            form.addEventListener('submit', event => {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        });
    })();
</script>
</body>
</html>
