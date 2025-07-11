<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Test JSP</title>
</head>
<body>
    <h1>Test JSP</h1>
    <p>Current time: <%= new java.util.Date() %></p>
    
    <c:set var="testVar" value="Hello JSTL!" />
    <p>JSTL test: <c:out value="${testVar}" /></p>
    
    <c:if test="${true}">
        <p>JSTL if statement works!</p>
    </c:if>
    
    <c:choose>
        <c:when test="${true}">
            <p>JSTL choose/when works!</p>
        </c:when>
        <c:otherwise>
            <p>This should not show</p>
        </c:otherwise>
    </c:choose>
</body>
</html> 