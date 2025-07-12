<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width,initial-scale=1"/>
    <title>Admin Dashboard · Quizzmosis</title>
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
        .glass-card h5, .glass-card h6, .glass-card p { color:#fff; }
        .admin-section { margin-bottom: 3rem; }
        .table { color: #fff; }
        .table th { border-color: rgba(255,255,255,0.2); }
        .table td { border-color: rgba(255,255,255,0.1); }
        .btn-admin { margin: 0.25rem; }
        .form-control, .form-select { background: rgba(255,255,255,0.1); border: 1px solid rgba(255,255,255,0.3); color: #fff; }
        .form-control:focus, .form-select:focus { background: rgba(255,255,255,0.15); border-color: rgba(255,255,255,0.5); color: #fff; box-shadow: 0 0 0 0.25rem rgba(255,255,255,0.25); }
        .form-control::placeholder { color: rgba(255,255,255,0.7); }
        .form-select option { background: #0d6efd; color: #fff; }
    </style>
</head>
<body>

<!-- NAVBAR -->
<nav class="navbar navbar-expand-lg navbar-dark glass-card mx-3 mt-3 px-3">
    <a class="navbar-brand fw-bold" href="${ctx}/HomeServlet">Quizzmosis</a>
    <button class="navbar-toggler" data-bs-toggle="collapse" data-bs-target="#navMain">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navMain">
        <ul class="navbar-nav me-auto">
            <li class="nav-item"><a class="nav-link" href="${ctx}/HomeServlet">
                <i class="bi bi-house-door-fill"></i> Home</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/quizzes">
                <i class="bi bi-list-check"></i> Quizzes</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/create">
                <i class="bi bi-plus-circle"></i> Create Quiz</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/leaderboard">
                <i class="bi bi-trophy-fill"></i> Leaderboard</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/friends">
                <i class="bi bi-people-fill"></i> Friends</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/messages">
                <i class="bi bi-envelope-fill"></i> Messages</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/history">
                <i class="bi bi-clock-history"></i> History</a></li>
            <li class="nav-item"><a class="nav-link" href="${ctx}/achievements">
                <i class="bi bi-award-fill"></i> Achievements</a></li>
            <li class="nav-item"><a class="nav-link active" href="${ctx}/admin">
                <i class="bi bi-shield-fill"></i> Admin</a></li>
        </ul>
        <a href="${ctx}/LogoutServlet" class="btn btn-outline-light btn-sm">
            <i class="bi bi-box-arrow-right"></i> Log&nbsp;out
        </a>
    </div>
</nav>

<!-- HEADER -->
<header class="text-center py-5">
    <div class="container">
        <h1 class="display-5 fw-bold">
            <i class="bi bi-shield-fill me-3"></i>Administrator Dashboard
        </h1>
        <p class="lead">Manage users, quizzes, and announcements</p>
    </div>
</header>

<!-- MESSAGE DISPLAY -->
<c:if test="${not empty sessionScope.adminMessage}">
    <div class="container mb-4">
        <div class="alert alert-${sessionScope.adminMessageType} alert-dismissible fade show" role="alert">
            ${sessionScope.adminMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </div>
    <% session.removeAttribute("adminMessage"); %>
    <% session.removeAttribute("adminMessageType"); %>
</c:if>

<main class="container">

    <!-- ANNOUNCEMENTS SECTION -->
    <section class="admin-section">
        <div class="glass-card p-4">
            <h3 class="mb-4">
                <i class="bi bi-megaphone-fill me-2"></i>Announcements
            </h3>
            
            <!-- Create Announcement Form -->
            <div class="mb-4">
                <h5>Create New Announcement</h5>
                <form action="${ctx}/admin/action" method="post" class="row g-3">
                    <input type="hidden" name="action" value="create_announcement">
                    <div class="col-md-6">
                        <input type="text" class="form-control" name="title" placeholder="Announcement Title" required>
                    </div>
                    <div class="col-md-6">
                        <button type="submit" class="btn btn-success">
                            <i class="bi bi-plus-circle me-1"></i>Create Announcement
                        </button>
                    </div>
                    <div class="col-12">
                        <textarea class="form-control" name="content" rows="3" placeholder="Announcement Content" required></textarea>
                    </div>
                </form>
            </div>
            
            <!-- Announcements List -->
            <div class="table-responsive">
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th>Title</th>
                            <th>Content</th>
                            <th>Status</th>
                            <th>Created</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="announcement" items="${allAnnouncements}">
                            <tr>
                                <td>${announcement.title}</td>
                                <td>${announcement.content}</td>
                                <td>
                                    <span class="badge ${announcement.active ? 'bg-success' : 'bg-secondary'}">
                                        ${announcement.active ? 'Active' : 'Inactive'}
                                    </span>
                                </td>
                                <td><fmt:formatDate value="${announcement.createdAt}" pattern="MMM dd, yyyy"/></td>
                                <td>
                                    <form action="${ctx}/admin/action" method="post" style="display: inline;">
                                        <input type="hidden" name="action" value="toggle_announcement">
                                        <input type="hidden" name="announcement_id" value="${announcement.announcementId}">
                                        <button type="submit" class="btn btn-sm ${announcement.active ? 'btn-warning' : 'btn-success'} btn-admin">
                                            <i class="bi ${announcement.active ? 'bi-eye-slash' : 'bi-eye'}"></i>
                                            ${announcement.active ? 'Deactivate' : 'Activate'}
                                        </button>
                                    </form>
                                    <form action="${ctx}/admin/action" method="post" style="display: inline;" 
                                          onsubmit="return confirm('Are you sure you want to delete this announcement?')">
                                        <input type="hidden" name="action" value="delete_announcement">
                                        <input type="hidden" name="announcement_id" value="${announcement.announcementId}">
                                        <button type="submit" class="btn btn-sm btn-danger btn-admin">
                                            <i class="bi bi-trash"></i> Delete
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </section>

    <!-- USERS SECTION -->
    <section class="admin-section">
        <div class="glass-card p-4">
            <h3 class="mb-4">
                <i class="bi bi-people-fill me-2"></i>User Management
            </h3>
            
            <div class="table-responsive">
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th>Username</th>
                            <th>Display Name</th>
                            <th>Role</th>
                            <th>Created</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="user" items="${allUsers}">
                            <tr>
                                <td>${user.username}</td>
                                <td>${user.displayName}</td>
                                <td>
                                    <span class="badge ${user.admin ? 'bg-danger' : 'bg-primary'}">
                                        ${user.admin ? 'Admin' : 'User'}
                                    </span>
                                </td>
                                <td><fmt:formatDate value="${user.createdAt}" pattern="MMM dd, yyyy"/></td>
                                <td>
                                    <c:if test="${!user.admin}">
                                        <form action="${ctx}/admin/action" method="post" style="display: inline;">
                                            <input type="hidden" name="action" value="promote_user">
                                            <input type="hidden" name="user_id" value="${user.userId}">
                                            <button type="submit" class="btn btn-sm btn-warning btn-admin">
                                                <i class="bi bi-arrow-up-circle"></i> Promote to Admin
                                            </button>
                                        </form>
                                    </c:if>
                                    <c:if test="${user.admin}">
                                        <form action="${ctx}/admin/action" method="post" style="display: inline;">
                                            <input type="hidden" name="action" value="demote_user">
                                            <input type="hidden" name="user_id" value="${user.userId}">
                                            <button type="submit" class="btn btn-sm btn-info btn-admin">
                                                <i class="bi bi-arrow-down-circle"></i> Demote from Admin
                                            </button>
                                        </form>
                                    </c:if>
                                    <form action="${ctx}/admin/action" method="post" style="display: inline;" 
                                          onsubmit="return confirm('Are you sure you want to delete this user? This action cannot be undone.')">
                                        <input type="hidden" name="action" value="delete_user">
                                        <input type="hidden" name="user_id" value="${user.userId}">
                                        <button type="submit" class="btn btn-sm btn-danger btn-admin">
                                            <i class="bi bi-trash"></i> Delete User
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </section>

    <!-- QUIZZES SECTION -->
    <section class="admin-section">
        <div class="glass-card p-4">
            <h3 class="mb-4">
                <i class="bi bi-list-check me-2"></i>Quiz Management
            </h3>
            
            <div class="table-responsive">
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th>Title</th>
                            <th>Owner</th>
                            <th>Description</th>
                            <th>Created</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="quiz" items="${allQuizzes}">
                            <tr>
                                <td>${quiz.title}</td>
                                <td>${quiz.ownerUsername}</td>
                                <td>${quiz.description}</td>
                                <td><fmt:formatDate value="${quiz.creationDate}" pattern="MMM dd, yyyy"/></td>
                                <td>
                                    <form action="${ctx}/admin/action" method="post" style="display: inline;">
                                        <input type="hidden" name="action" value="clear_quiz_history">
                                        <input type="hidden" name="quiz_id" value="${quiz.quizId}">
                                        <button type="submit" class="btn btn-sm btn-warning btn-admin" 
                                                onclick="return confirm('Are you sure you want to clear all history for this quiz?')">
                                            <i class="bi bi-clock-history"></i> Clear History
                                        </button>
                                    </form>
                                    <form action="${ctx}/admin/action" method="post" style="display: inline;" 
                                          onsubmit="return confirm('Are you sure you want to delete this quiz? This action cannot be undone.')">
                                        <input type="hidden" name="action" value="delete_quiz">
                                        <input type="hidden" name="quiz_id" value="${quiz.quizId}">
                                        <button type="submit" class="btn btn-sm btn-danger btn-admin">
                                            <i class="bi bi-trash"></i> Delete Quiz
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </section>

</main>

<!-- FOOTER -->
<footer class="text-center py-3">
    &copy; 2025 Quizzmosis &middot; Made with <i class="bi bi-heart-fill"></i> by OverflowQuartet
</footer>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html> 