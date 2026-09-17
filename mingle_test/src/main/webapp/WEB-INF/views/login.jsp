<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>로그인</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/auth.css">
</head>

<body>

<div class="auth-container">

    <h1>Mingle</h1>

    <c:if test="${not empty error}">
        <div class="auth-error">${error}</div>
    </c:if>

    <c:if test="${not empty message}">
        <div class="auth-message">${message}</div>
    </c:if>

    <!-- 로그인 처리는 Spring Security (security-context.xml의 form-login) -->
    <form action="${pageContext.request.contextPath}/login" method="post">

        <input type="email"
               name="email"
               placeholder="이메일"
               required
               autofocus>

        <input type="password"
               name="password"
               placeholder="비밀번호"
               required>

        <button type="submit">로그인</button>

    </form>

    <div class="auth-links">
        <a href="${pageContext.request.contextPath}/signup">회원가입</a>
        <a href="${pageContext.request.contextPath}/password/find">비밀번호 찾기</a>
    </div>

</div>

</body>
</html>
