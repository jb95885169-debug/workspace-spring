<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>내 프로필</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/auth.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/profile.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/interest.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/photoUpload.css">
</head>

<body>

<div class="auth-container auth-container-wide">

    <h1>내 프로필</h1>

    <c:if test="${not empty error}">
        <div class="auth-error">${error}</div>
    </c:if>

    <c:if test="${param.saved != null}">
        <div class="auth-message">프로필을 저장했습니다.</div>
    </c:if>


    <!-- ================= 등록된 사진 ================= -->
    <h2>등록된 사진</h2>

    <c:choose>
        <c:when test="${empty profile.photos}">
            <p class="auth-guide">아직 등록한 사진이 없습니다. 아래에서 추가해 주세요.</p>
        </c:when>

        <c:otherwise>
            <div class="photo-list">
                <c:forEach var="photo" items="${profile.photos}">

                    <div class="photo-item ${photo.isPrimary == 1 ? 'primary' : ''}">

                        <img src="${pageContext.request.contextPath}${photo.photoUrl}" alt="프로필 사진">

                        <c:if test="${photo.isPrimary == 1}">
                            <span class="photo-badge">대표</span>
                        </c:if>

                        <div class="photo-actions">
                            <c:if test="${photo.isPrimary != 1}">
                                <button type="button"
                                        class="photo-primary-btn"
                                        data-photo-id="${photo.id}">대표로</button>
                            </c:if>

                            <button type="button"
                                    class="photo-delete-btn"
                                    data-photo-id="${photo.id}">삭제</button>
                        </div>

                    </div>

                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>


    <form action="${pageContext.request.contextPath}/profile" method="post">

        <!-- ================= 사진 추가 ================= -->
        <h2>사진 추가</h2>

        <p class="auth-guide">
            사진을 고르면 먼저 임시 저장되고, 아래 저장 버튼을 눌러야 등록됩니다.
        </p>

        <input type="file" id="photoInput" accept="image/*" multiple>

        <div id="tempPhotoList" class="temp-photo-list"></div>


        <!-- ================= 계정 ================= -->
        <h2>계정</h2>

        <label>이메일</label>
        <input type="email" value="${profile.email}" disabled>

        <label for="phone">휴대폰 번호</label>
        <input type="text"
               id="phone"
               name="phone"
               value="${profile.phone}"
               required>


        <!-- ================= 프로필 ================= -->
        <h2>프로필</h2>

        <label for="nickname">닉네임</label>
        <input type="text"
               id="nickname"
               name="nickname"
               value="${profile.nickname}"
               maxlength="20"
               required>

        <label>생년월일 / 성별 (바꿀 수 없음)</label>
        <input type="text"
               value="<fmt:formatDate value='${profile.birthDate}' pattern='yyyy-MM-dd'/> / ${profile.gender == 'MALE' ? '남성' : '여성'}"
               disabled>

        <label for="region">거주 지역</label>
        <input type="text"
               id="region"
               name="region"
               value="${profile.region}"
               required>

        <label for="job">직업</label>
        <input type="text"
               id="job"
               name="job"
               value="${profile.job}"
               placeholder="직업">

        <label for="height">키 (cm)</label>
        <input type="number"
               id="height"
               name="height"
               value="${profile.height}"
               min="100"
               max="250">


        <!-- ================= 관심사 ================= -->
        <h2>관심사 (3개 이상)</h2>

        <div class="interest-list">
            <c:forEach var="interest" items="${interests}">

                <label class="interest-item">
                    <input type="checkbox"
                           name="interestIds"
                           value="${interest.id}"
                           ${not empty profile.interestIds
                             and profile.interestIds.contains(interest.id) ? 'checked' : ''}>
                    ${interest.name}
                </label>

            </c:forEach>
        </div>

        <button type="submit">저장</button>

    </form>

    <div class="auth-links">
        <a href="${pageContext.request.contextPath}/users/list">추천 회원으로</a>
        <a href="${pageContext.request.contextPath}/chat/list">채팅 목록</a>
        <a href="${pageContext.request.contextPath}/subscription">구독</a>
        <a href="${pageContext.request.contextPath}/logout">로그아웃</a>
    </div>

</div>


<script>
    const contextPath = '${pageContext.request.contextPath}';

    // 더 올릴 수 있는 장수 (등록된 사진을 뺀 나머지)
    const photoLimit = ${5 - (empty profile.photos ? 0 : profile.photos.size())};
</script>

<script src="${pageContext.request.contextPath}/resources/js/photoUpload.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/profile/profile.js"></script>

</body>
</html>
