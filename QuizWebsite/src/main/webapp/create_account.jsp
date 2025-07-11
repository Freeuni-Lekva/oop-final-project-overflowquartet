<%-- Redirect to home if already signed in --%>
<% if (session.getAttribute("user") != null) {
    response.sendRedirect(request.getContextPath() + "/home_page.jsp");
    return;
} %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Quizzmosis</title>

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
      margin-top: 5rem; /* Prevent overlap with site title */
    }

    /* subtle tint for inputs */
    .login-card .form-control,
    .login-card .form-check-input {
      background: rgba(255,255,255,0.85);
    }

    /* ─── Custom validation palette ────────────────────────────── */
    :root {
      --bs-danger: #CC0000;           /* text & border colour */
      --bs-danger-rgb: 204, 0, 0;
    }
    .was-validated .form-control:invalid,
    .form-control.is-invalid {
      border-color: var(--bs-danger);
    }
    .invalid-feedback,
    .valid-feedback {
      color: var(--bs-danger);
    }
    .was-validated .form-check-input:valid,
    .form-check-input.is-valid {
      background-color: rgba(255,255,255,0.85) !important;
      border-color: rgba(255,255,255,0.6)   !important;
      box-shadow: none              !important;
      background-image: none        !important;
    }
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
<!-- site title -->
<h1 class="position-absolute top-0 start-50 translate-middle-x mt-4
             fw-bold text-white display-6 user-select-none"
    style="letter-spacing: .5px;">
  Quizz<span class="text-warning">mosis</span>
</h1>

<main class="login-card p-4 p-md-5 text-white">
  <h1 class="h3 fw-bold mb-4 text-center">Create Account</h1>

  <form action="<%= request.getContextPath() %>/RegisterServlet"
        method="post"
        class="needs-validation"
        novalidate>

    <div class="mb-3">
      <label for="username" class="form-label">Username</label>
      <input
              type="text"
              class="form-control"
              id="username"
              name="username"
              required
              autofocus>
      <div class="invalid-feedback">Choose a username.</div>
    </div>

    <div class="mb-3">
      <label for="displayName" class="form-label">
        Display Name <small class="text-muted">(optional)</small>
      </label>
      <input
              type="text"
              class="form-control"
              id="displayName"
              name="displayName">
    </div>

    <div class="mb-3">
      <label for="password" class="form-label">Password</label>
      <input
              type="password"
              class="form-control"
              id="password"
              name="password"
              required
              minlength="6">
      <div class="invalid-feedback">Password (≥ 6 chars) required.</div>
    </div>

    <div class="mb-4">
      <label for="confirm" class="form-label">Confirm Password</label>
      <input
              type="password"
              class="form-control"
              id="confirm"
              name="confirm"
              required>
      <div class="invalid-feedback">Passwords must match.</div>
    </div>

    <button
            class="btn btn-light text-primary fw-semibold w-100 py-2 mt-4"
            type="submit">
      Sign up
    </button>

    <p class="text-center small mb-0 mt-3">
      Already have an account?
      <a
              href="<%= request.getContextPath() %>/LoginServlet"
              class="fw-semibold link-light link-underline-opacity-0 signup-link">
        Log in
      </a>
    </p>
  </form>
</main>

<!-- Bootstrap JS -->
<script
        src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js">
</script>

<!-- combined validation + confirm-password check -->
<script>
  (() => {
    'use strict';
    const forms = document.querySelectorAll('.needs-validation');
    Array.from(forms).forEach(form => {
      const pwd = form.querySelector('#password');
      const conf = form.querySelector('#confirm');

      form.addEventListener('submit', event => {
        if (pwd && conf) {
          if (pwd.value !== conf.value) {
            conf.setCustomValidity('Passwords do not match');
          } else {
            conf.setCustomValidity('');
          }
        }

        if (!form.checkValidity()) {
          event.preventDefault();
          event.stopPropagation();
        }
        form.classList.add('was-validated');
      }, false);
    });
  })();
</script>

<script>
// Force reload on back navigation to prevent showing cached content after logout or login
window.addEventListener('pageshow', function(event) {
    if (event.persisted) {
        window.location.reload();
    }
});
</script>
</body>
</html>
