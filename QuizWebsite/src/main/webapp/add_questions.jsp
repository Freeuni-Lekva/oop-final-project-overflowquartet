<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Add Questions · Quiz Website</title>
    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <!-- Bootstrap Icons -->
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
        .glass-card h5 { color: #fff; }
        .glass-card p  { color: #e8e8e8; }
        footer {
            font-size: .875rem;
            color: rgba(255,255,255,.65);
        }
    </style>
</head>
<body>
<!-- NAVBAR (copied from home_page.jsp) -->
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
                <a class="nav-link" href="<%= request.getContextPath() %>/quizzes"><i class="bi bi-list-check"></i> Quizzes</a>
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
            <li class="nav-item"><a class="nav-link" href="<%= request.getContextPath() %>/achievements">
                <i class="bi bi-award-fill"></i> Achievements</a></li>
        </ul>
        <a href="<%= request.getContextPath() %>/LogoutServlet" class="btn btn-outline-light btn-sm">
            <i class="bi bi-box-arrow-right"></i> Log&nbsp;out
        </a>
    </div>
</nav>
<main class="container py-5">
    <div class="glass-card p-4 mx-auto" style="max-width: 700px;">
        <h2 class="fw-bold mb-4"><i class="bi bi-plus-circle"></i> Add Questions to Quiz</h2>
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        <form action="<%= request.getContextPath() %>/add-questions" method="post" id="questionForm" autocomplete="off">
            <input type="hidden" name="quizId" value="${quizId}" />
            <div class="mb-3">
                <label for="questionText" class="form-label">Question Text</label>
                <input type="text" class="form-control" id="questionText" name="questionText" required maxlength="300">
            </div>
            <div class="mb-3">
                <label for="questionType" class="form-label">Question Type</label>
                <select class="form-select" id="questionType" name="questionType" required onchange="toggleTypeFields()">
                    <option value="question_response">Short Answer</option>
                    <option value="fill_blank">Fill in the Blank</option>
                    <option value="picture_response">Picture Response</option>
                    <option value="multiple_choice">Multiple Choice</option>
                </select>
            </div>
            <!-- Image URL field for picture response questions -->
            <div class="mb-3 d-none" id="imageUrlField">
                <label for="imageUrl" class="form-label">Image URL</label>
                <div class="input-group">
                    <input type="url" class="form-control" id="imageUrl" name="imageUrl" 
                           placeholder="https://example.com/image.jpg" 
                           pattern="https?://.+">
                    <button type="button" class="btn btn-outline-light" id="previewBtn" onclick="previewImage()">
                        <i class="bi bi-eye"></i> Preview
                    </button>
                </div>
                <div class="form-text text-light">Enter the full URL of the image (must start with http:// or https://)</div>
                
                <!-- Image Preview Section -->
                <div class="mt-3 d-none" id="imagePreviewSection">
                    <label class="form-label">Image Preview:</label>
                    <div class="text-center p-3" style="background: rgba(255, 255, 255, 0.1); border-radius: 0.5rem;">
                        <img id="previewImage" src="" alt="Preview" 
                             class="img-fluid rounded question-image" 
                             style="max-height: 300px; max-width: 100%;"
                             onerror="handlePreviewError()">
                        <div id="previewError" class="alert alert-warning mt-2" style="display: none;">
                            <i class="bi bi-exclamation-triangle"></i> Sorry, we couldn't load the image. Please check the URL and try again.
                        </div>
                        <div id="previewSuccess" class="alert alert-success mt-2" style="display: none;">
                            <i class="bi bi-check-circle"></i> Image loaded successfully!
                        </div>
                    </div>
                </div>
            </div>
            <!-- Short Answer Answers -->
            <div class="mb-3" id="shortAnswerFields">
                <label class="form-label" id="answerLabel">Acceptable Answers (one per line)</label>
                <textarea class="form-control" name="answerText" rows="2" placeholder="e.g. George Washington\nWashington" required></textarea>
            </div>
            <!-- Multiple Choice Answers -->
            <div class="mb-3 d-none" id="mcFields">
                <label class="form-label">Choices</label>
                <div id="mcChoices">
                    <div class="input-group mb-2">
                        <div class="input-group-text">
                            <input class="form-check-input mt-0" type="checkbox" name="isCorrect" value="0" title="Correct answer">
                        </div>
                        <input type="text" class="form-control" name="choiceText" placeholder="Choice 1" required>
                    </div>
                    <div class="input-group mb-2">
                        <div class="input-group-text">
                            <input class="form-check-input mt-0" type="checkbox" name="isCorrect" value="1" title="Correct answer">
                        </div>
                        <input type="text" class="form-control" name="choiceText" placeholder="Choice 2" required>
                    </div>
                </div>
                <button type="button" class="btn btn-sm btn-outline-light mt-2" onclick="addChoice()">
                    <i class="bi bi-plus"></i> Add Choice
                </button>
            </div>
            <button type="submit" class="btn btn-light text-primary fw-semibold w-100 mt-3">
                <i class="bi bi-plus-circle"></i> Add Question
            </button>
        </form>
        <hr>
        <h4 class="fw-semibold mb-3">Questions Added</h4>
        <c:choose>
            <c:when test="${empty questions}">
                <p class="text-light">No questions added yet.</p>
            </c:when>
            <c:otherwise>
                <ul class="list-group mb-3">
                    <c:forEach var="q" items="${questions}">
                        <li class="list-group-item bg-transparent text-light">
                            <b>${q.questionText}</b> <span class="badge bg-info">${q.questionType}</span>
                        </li>
                    </c:forEach>
                </ul>
            </c:otherwise>
        </c:choose>
        <a href="<%= request.getContextPath() %>/quizzes" class="btn btn-success w-100 mt-2"
           <c:if test="${empty questions}">disabled</c:if>>Finish &amp; Return to Quizzes</a>
    </div>
</main>
<footer class="text-center py-3">
    © 2025 QuizMossis · Made with <i class="bi bi-heart-fill"></i> by OverflowQuartet
</footer>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function toggleTypeFields() {
        var type = document.getElementById('questionType').value;
        var shortAnswerFields = document.getElementById('shortAnswerFields');
        var answerTextarea = shortAnswerFields.querySelector('textarea[name="answerText"]');
        var mcFields = document.getElementById('mcFields');
        var mcInputs = mcFields.querySelectorAll('input, textarea, select');
        var imageUrlField = document.getElementById('imageUrlField');
        var imageUrlInput = document.getElementById('imageUrl');

        if (type === 'multiple_choice') {
            shortAnswerFields.classList.add('d-none');
            answerTextarea.required = false;
            mcFields.classList.remove('d-none');
            imageUrlField.classList.add('d-none');
            imageUrlInput.required = false;
            // Enable all MC inputs
            mcInputs.forEach(function(input) {
                input.disabled = false;
            });
        } else if (type === 'picture_response') {
            shortAnswerFields.classList.remove('d-none');
            answerTextarea.required = true;
            mcFields.classList.add('d-none');
            imageUrlField.classList.remove('d-none');
            imageUrlInput.required = true;
            // Disable all MC inputs so they don't block validation
            mcInputs.forEach(function(input) {
                input.disabled = true;
            });
        } else {
            shortAnswerFields.classList.remove('d-none');
            answerTextarea.required = true;
            mcFields.classList.add('d-none');
            imageUrlField.classList.add('d-none');
            imageUrlInput.required = false;
            // Disable all MC inputs so they don't block validation
            mcInputs.forEach(function(input) {
                input.disabled = true;
            });
        }
        // Optional: update label for clarity
        var label = document.getElementById('answerLabel');
        if (type === 'fill_blank') {
            label.textContent = "Correct Answers (one per line, for fill-in-the-blank)";
        } else if (type === 'picture_response') {
            label.textContent = "Acceptable Answers (one per line, for picture response)";
        } else {
            label.textContent = "Acceptable Answers (one per line)";
        }
    }
    function addChoice() {
        var idx = document.querySelectorAll('#mcChoices .input-group').length;
        var div = document.createElement('div');
        div.className = 'input-group mb-2';
        div.innerHTML = '<div class="input-group-text">' +
            '<input class="form-check-input mt-0" type="checkbox" name="isCorrect" value="' + idx + '" title="Correct answer">' +
            '</div>' +
            '<input type="text" class="form-control" name="choiceText" placeholder="Choice ' + (idx+1) + '" required>';
        document.getElementById('mcChoices').appendChild(div);
    }
    
    function previewImage() {
        var imageUrl = document.getElementById('imageUrl').value.trim();
        var previewSection = document.getElementById('imagePreviewSection');
        var previewImage = document.getElementById('previewImage');
        var previewError = document.getElementById('previewError');
        var previewSuccess = document.getElementById('previewSuccess');
        var previewBtn = document.getElementById('previewBtn');
        
        if (!imageUrl) {
            alert('Please enter an image URL first.');
            return;
        }
        
        if (!imageUrl.match(/^https?:\/\/.+/)) {
            alert('Please enter a valid URL starting with http:// or https://');
            return;
        }
        
        // Show loading state
        previewBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> Loading...';
        previewBtn.disabled = true;
        
        // Hide previous messages
        previewError.style.display = 'none';
        previewSuccess.style.display = 'none';
        
        // Show preview section
        previewSection.classList.remove('d-none');
        
        // Set image source
        previewImage.src = imageUrl;
        
        // Add load event listener
        previewImage.onload = function() {
            previewSuccess.style.display = 'block';
            previewError.style.display = 'none';
            previewBtn.innerHTML = '<i class="bi bi-eye"></i> Preview';
            previewBtn.disabled = false;
        };
    }
    
    function handlePreviewError() {
        var previewError = document.getElementById('previewError');
        var previewSuccess = document.getElementById('previewSuccess');
        var previewBtn = document.getElementById('previewBtn');
        
        previewError.style.display = 'block';
        previewSuccess.style.display = 'none';
        previewBtn.innerHTML = '<i class="bi bi-eye"></i> Preview';
        previewBtn.disabled = false;
    }
    // On page load, set correct fields
    document.addEventListener('DOMContentLoaded', function() {
        toggleTypeFields();
        // Prevent form submission if answerText is empty for non-multiple-choice
        document.getElementById('questionForm').addEventListener('submit', function(e) {
            var type = document.getElementById('questionType').value;
            if (type !== 'multiple_choice') {
                var answerTextarea = document.querySelector('textarea[name="answerText"]');
                if (!answerTextarea.value.trim()) {
                    answerTextarea.classList.add('is-invalid');
                    e.preventDefault();
                } else {
                    answerTextarea.classList.remove('is-invalid');
                }
            }
            
            // Validate image URL for picture response questions
            if (type === 'picture_response') {
                var imageUrlInput = document.getElementById('imageUrl');
                if (!imageUrlInput.value.trim()) {
                    imageUrlInput.classList.add('is-invalid');
                    e.preventDefault();
                } else if (!imageUrlInput.checkValidity()) {
                    imageUrlInput.classList.add('is-invalid');
                    e.preventDefault();
                } else {
                    imageUrlInput.classList.remove('is-invalid');
                }
            }
        });
        
        // Auto-preview when user pastes or finishes typing a URL
        var imageUrlInput = document.getElementById('imageUrl');
        var previewTimeout;
        
        imageUrlInput.addEventListener('input', function() {
            clearTimeout(previewTimeout);
            previewTimeout = setTimeout(function() {
                var url = imageUrlInput.value.trim();
                if (url && url.match(/^https?:\/\/.+/)) {
                    // Auto-preview after 1 second of no typing
                    previewImage();
                }
            }, 1000);
        });
        
        imageUrlInput.addEventListener('paste', function() {
            clearTimeout(previewTimeout);
            previewTimeout = setTimeout(function() {
                var url = imageUrlInput.value.trim();
                if (url && url.match(/^https?:\/\/.+/)) {
                    // Auto-preview after paste
                    previewImage();
                }
            }, 500);
        });
    });
</script>
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