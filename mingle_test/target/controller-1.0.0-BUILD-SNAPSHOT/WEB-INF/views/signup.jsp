<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>회원가입</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/auth.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/interest.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/photoUpload.css">
</head>

<body>

<div class="auth-container auth-container-wide">

    <h1>회원가입</h1>

    <c:if test="${not empty error}">
        <div class="auth-error">${error}</div>
    </c:if>

    <!-- 사진은 미리 temp에 올라가 있고, 여기서는 임시 파일명만 함께 보낸다 -->
    <form action="${pageContext.request.contextPath}/signup" method="post">

        <!-- ================= 계정 ================= -->
        <h2>계정</h2>

        <input type="email"
               name="email"
               value="${signupRequest.email}"
               placeholder="이메일"
               required>

        <input type="password"
               name="password"
               placeholder="비밀번호 (8자 이상)"
               minlength="8"
               required>

        <input type="password"
               name="passwordConfirm"
               placeholder="비밀번호 재입력"
               minlength="8"
               required>

        <input type="text"
               name="phone"
               value="${signupRequest.phone}"
               placeholder="휴대폰 번호 (010-1234-5678)"
               required>


        <!-- ================= 프로필 ================= -->
        <h2>프로필</h2>

        <input type="text"
               name="nickname"
               value="${signupRequest.nickname}"
               placeholder="닉네임 (20자까지)"
               maxlength="20"
               required>

        <label for="birthDate">생년월일</label>
        <input type="date"
               id="birthDate"
               name="birthDate"
               value="<fmt:formatDate value='${signupRequest.birthDate}' pattern='yyyy-MM-dd'/>"
               required>

        <label for="gender">성별</label>
        <select id="gender" name="gender" required>
            <option value="">선택해 주세요</option>
            <option value="MALE"   ${signupRequest.gender == 'MALE'   ? 'selected' : ''}>남성</option>
            <option value="FEMALE" ${signupRequest.gender == 'FEMALE' ? 'selected' : ''}>여성</option>
        </select>

        <input type="text"
               name="region"
               value="${signupRequest.region}"
               placeholder="거주 지역 (예: 서울 마포구)"
               required>


        <!-- ================= 관심사 ================= -->
        <h2>관심사 (3개 이상)</h2>

        <div class="interest-list">
            <c:forEach var="interest" items="${interests}">

                <label class="interest-item">
                    <input type="checkbox"
                           name="interestIds"
                           value="${interest.id}"
                           ${not empty signupRequest.interestIds
                             and signupRequest.interestIds.contains(interest.id) ? 'checked' : ''}>
                    ${interest.name}
                </label>

            </c:forEach>
        </div>


        <!-- ================= 프로필 사진 ================= -->
        <h2>프로필 사진 (5장까지)</h2>

        <p class="auth-guide">
            첫 번째 사진이 대표 사진이 됩니다. 가입을 마쳐야 저장되고, 나중에 바꿀 수 있어요.
        </p>

        <input type="file" id="photoInput" accept="image/*" multiple>

        <div id="tempPhotoList" class="temp-photo-list"></div>


        <!-- ================= 선택 입력 ================= -->
        <h2>선택 입력</h2>

        <input type="text"
               name="job"
               value="${signupRequest.job}"
               placeholder="직업">

        <input type="number"
               name="height"
               value="${signupRequest.height}"
               placeholder="키 (cm)"
               min="100"
               max="250">

        <button type="submit">가입하기</button>

    </form>

    <div class="auth-links">
        <a href="${pageContext.request.contextPath}/login">로그인으로 돌아가기</a>
    </div>

</div>


<script>
    const contextPath = '${pageContext.request.contextPath}';

    // 가입 시 올릴 수 있는 장수
    const photoLimit = 5;
</script>

<script src="${pageContext.request.contextPath}/resources/js/photoUpload.js"></script>

</body>
</html>
