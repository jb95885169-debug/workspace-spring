<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>비밀번호 찾기</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/auth.css">
</head>

<body>

<div class="auth-container">

    <h1>비밀번호 찾기</h1>

    <c:choose>

        <%-- 본인 확인에 성공하면 임시 비밀번호를 한 번만 보여 준다 --%>
        <c:when test="${not empty tempPassword}">

            <div class="auth-message">
                임시 비밀번호가 발급되었습니다.<br>
                이 비밀번호로 로그인한 뒤 바꿔 주세요.
            </div>

            <div class="temp-password">${tempPassword}</div>

            <div class="auth-links">
                <a href="${pageContext.request.contextPath}/login">로그인하러 가기</a>
            </div>

        </c:when>

        <c:otherwise>

            <c:if test="${not empty error}">
                <div class="auth-error">${error}</div>
            </c:if>

            <p class="auth-guide">
                가입할 때 쓴 이메일과 닉네임을 입력하면 임시 비밀번호를 알려 드립니다.
            </p>

            <form action="${pageContext.request.contextPath}/password/find" method="post">

                <input type="email"
                       name="email"
                       value="${passwordFindRequest.email}"
                       placeholder="이메일"
                       required
                       autofocus>

                <input type="text"
                       name="nickname"
                       value="${passwordFindRequest.nickname}"
                       placeholder="가입 시 등록한 닉네임"
                       required>

                <button type="submit">임시 비밀번호 받기</button>

            </form>

            <div class="auth-links">
                <a href="${pageContext.request.contextPath}/login">로그인으로 돌아가기</a>
                <a href="${pageContext.request.contextPath}/signup">회원가입</a>
            </div>

        </c:otherwise>

    </c:choose>

</div>

</body>
</html>
