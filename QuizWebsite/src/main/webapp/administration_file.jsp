<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Quiz Website · Admin</title>

    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet" />
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" />

    <style>
        body{
            min-height:100vh;
            background:linear-gradient(135deg,#0d6efd 0%,#024ec1 100%);
            color:#fff;
        }
        .glass-card{
            backdrop-filter:blur(8px);
            background:rgba(255,255,255,.15);
            border:1px solid rgba(255,255,255,.25);
            border-radius:1rem;
            box-shadow:0 .75rem 1.5rem rgba(0,0,0,.3);
        }
        .glass-card h4{color:#fff}
        table thead th{color:#dfe9ff}
        table tbody td{color:#f8f9fa}
        .table-glass{
            --bs-table-bg:transparent;
            --bs-table-bordered-color:#6ea8fe40;
            --bs-table-border-color:rgba(255,255,255,.25);
        }
    </style>
</head>

<body>
<!-- ══ NAVBAR (reuse your existing nav) ═══════════════════════════════ -->
<nav class="navbar navbar-expand-lg navbar-dark glass-card mx-3 mt-3 px-3">
    <a class="navbar-brand fw-semibold" href="${pageContext.request.contextPath}/">QuizSite</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMain">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navMain">
        <ul class="navbar-nav me-auto">
            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/">Home</a></li>
            <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/quizzes">Quizzes</a></li>
            <li class="nav-item"><a class="nav-link active fw-semibold" href="#">Admin</a></li>
        </ul>
        <a href="${pageContext.request.contextPath}/logout" class="btn btn-outline-light btn-sm">Log&nbsp;out</a>
    </div>
</nav>

<main class="container py-5">
    <!-- ══ USERS MANAGEMENT ════════════════════════════════════════════ -->
    <section class="glass-card p-4 mb-5">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h4 class="mb-0"><i class="bi bi-people-fill me-2"></i>Users</h4>
            <form class="d-flex" method="get" action="${pageContext.request.contextPath}/admin/users">
                <input class="form-control form-control-sm me-2" name="q" placeholder="Search username…">
                <button class="btn btn-light btn-sm text-primary fw-semibold" type="submit">Search</button>
            </form>
        </div>

        <div class="table-responsive">
            <table class="table table-sm table-bordered table-glass align-middle">
                <thead>
                <tr>
                    <th>ID</th><th>Username</th><th>Display Name</th><th>Created</th><th>Last Login</th><th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <!--  JSTL loop  -->
                <c:forEach var="u" items="${users}">
                    <tr>
                        <td>${u.userId}</td>
                        <td>${u.username}</td>
                        <td>${u.displayName}</td>
                        <td>${u.createdAt}</td>
                        <td>${u.lastLogin}</td>
                        <td class="text-center">
                            <!-- Disable / Enable -->
                            <form class="d-inline" method="post" action="${pageContext.request.contextPath}/admin/user/toggle">
                                <input type="hidden" name="userId" value="${u.userId}">
                                <button class="btn btn-sm btn-outline-light"
                                        title="Disable / Enable">
                                    <i class="bi bi-toggle-${u.enabled ? 'on' : 'off'}"></i>
                                </button>
                            </form>
                            <!-- Delete -->
                            <button class="btn btn-sm btn-danger"
                                    data-bs-toggle="modal"
                                    data-bs-target="#confirmUserDelete"
                                    data-userid="${u.userId}"
                                    data-username="${u.username}">
                                <i class="bi bi-trash"></i>
                            </button>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </section>

    <!-- ══ QUIZZES MANAGEMENT ══════════════════════════════════════════ -->
    <section class="glass-card p-4">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h4 class="mb-0"><i class="bi bi-ui-checks-grid me-2"></i>Quizzes</h4>
            <form class="d-flex" method="get" action="${pageContext.request.contextPath}/admin/quizzes">
                <input class="form-control form-control-sm me-2" name="q" placeholder="Search title…">
                <button class="btn btn-light btn-sm text-primary fw-semibold" type="submit">Search</button>
            </form>
        </div>

        <div class="table-responsive">
            <table class="table table-sm table-bordered table-glass align-middle">
                <thead>
                <tr>
                    <th>ID</th><th>Title</th><th>Owner</th><th>Questions</th><th>Created</th><th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <!-- JSTL loop -->
                <c:forEach var="q" items="${quizzes}">
                    <tr>
                        <td>${q.quizId}</td>
                        <td>${q.title}</td>
                        <td>${q.ownerUsername}</td>
                        <td>${q.questionCount}</td>
                        <td>${q.creationDate}</td>
                        <td class="text-center">
                            <!-- View -->
                            <a class="btn btn-sm btn-outline-light me-1"
                               href="${pageContext.request.contextPath}/quiz?id=${q.quizId}"
                               target="_blank" title="Preview">
                                <i class="bi bi-box-arrow-up-right"></i>
                            </a>
                            <!-- Delete -->
                            <button class="btn btn-sm btn-danger"
                                    data-bs-toggle="modal"
                                    data-bs-target="#confirmQuizDelete"
                                    data-quizid="${q.quizId}"
                                    data-quiztitle="${q.title}">
                                <i class="bi bi-trash"></i>
                            </button>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </section>
</main>

<!-- ══ DELETE-CONFIRM MODALS ═════════════════════════════════════════ -->
<!-- User -->
<div class="modal fade" id="confirmUserDelete" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content glass-card p-0">
            <div class="modal-header border-0">
                <h5 class="modal-title"><i class="bi bi-trash me-2"></i>Delete User</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                Are you sure you want to delete user <strong id="delUserName"></strong> (ID&nbsp;<span id="delUserId"></span>)?
            </div>
            <div class="modal-footer border-0">
                <button class="btn btn-outline-light" data-bs-dismiss="modal">Cancel</button>
                <form id="userDeleteForm" method="post" action="${pageContext.request.contextPath}/admin/user/delete">
                    <input type="hidden" name="userId" id="userDeleteInput">
                    <button class="btn btn-danger">Delete</button>
                </form>
            </div>
        </div>
    </div>
</div>

<!-- Quiz -->
<div class="modal fade" id="confirmQuizDelete" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content glass-card p-0">
            <div class="modal-header border-0">
                <h5 class="modal-title"><i class="bi bi-trash me-2"></i>Delete Quiz</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                Delete quiz <strong id="delQuizTitle"></strong> (ID&nbsp;<span id="delQuizId"></span>)? This cannot be undone.
            </div>
            <div class="modal-footer border-0">
                <button class="btn btn-outline-light" data-bs-dismiss="modal">Cancel</button>
                <form id="quizDeleteForm" method="post" action="${pageContext.request.contextPath}/admin/quiz/delete">
                    <input type="hidden" name="quizId" id="quizDeleteInput">
                    <button class="btn btn-danger">Delete</button>
                </form>
            </div>
        </div>
    </div>
</div>
<footer class="text-center py-3 text-white-50">
    © 2025 Quizzmosis · Made with <i class="bi bi-heart-fill"></i> by OverflowQuartet
</footer>

<!-- ══ BOOTSTRAP & MODAL SCRIPT ═════════════════════════════════════ -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    /* Fill delete modals with row data */
    const userModal = document.getElementById('confirmUserDelete');
    userModal.addEventListener('show.bs.modal', event => {
        const btn = event.relatedTarget;
        document.getElementById('delUserName').textContent = btn.dataset.username;
        document.getElementById('delUserId').textContent   = btn.dataset.userid;
        document.getElementById('userDeleteInput').value   = btn.dataset.userid;
    });

    const quizModal = document.getElementById('confirmQuizDelete');
    quizModal.addEventListener('show.bs.modal', event => {
        const btn = event.relatedTarget;
        document.getElementById('delQuizTitle').textContent = btn.dataset.quiztitle;
        document.getElementById('delQuizId').textContent    = btn.dataset.quizid;
        document.getElementById('quizDeleteInput').value    = btn.dataset.quizid;
    });
</script>

</body>
</html>
