<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Register</title>
</head>
<body>
<h2>Register</h2>
<% String error = (String) request.getAttribute("error"); if (error != null) { %>
    <div style="color:red"><%= error %></div>
<% } %>
<form method="post" action="user?action=register">
    <label>Username: <input type="text" name="username" required></label><br>
    <label>Password: <input type="password" name="password" required></label><br>
    <label>Display Name: <input type="text" name="displayName" required></label><br>
    <label>Email: <input type="email" name="email"></label><br>
    <button type="submit">Register</button>
</form>
<a href="login.jsp">Already have an account? Login</a>
</body>
</html> 