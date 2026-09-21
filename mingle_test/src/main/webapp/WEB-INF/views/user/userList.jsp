<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Mingle</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/css/user/userList.css">
</head>

<body>
<div class="user-list-header">

    <button type="button"
            onclick="location.href='${pageContext.request.contextPath}/chat/list'">
        💬 채팅리스트
    </button>

    <a class="logout-link" href="${pageContext.request.contextPath}/feeds">피드</a>
    <a class="logout-link" href="${pageContext.request.contextPath}/subscription">구독</a>
    <% if (request.isUserInRole("ADMIN")) { %>
        <a class="logout-link" href="${pageContext.request.contextPath}/admin/subscriptions">관리자</a>
    <% } %>
    <a class="logout-link" href="${pageContext.request.contextPath}/profile">내 프로필</a>
    <a class="logout-link" href="${pageContext.request.contextPath}/logout">로그아웃</a>

</div>
<div class="user-list-container">

    <header class="user-list-title">
        <h1>Mingle</h1>
        <p>새로운 인연을 찾아보세요.</p>
    </header>

    <!-- 추천 카드 (userList.js가 /api/users로 채움) -->
    <main class="user-card-list" id="userCardList">

        <div class="user-card-empty">

            <h2>오늘의 추천을 모두 확인했어요!</h2>

            <p>
                새로운 인연을 기다려보세요.
            </p>

        </div>

    </main>


    <!-- ========================= -->
    <!-- 유저 상세 모달 -->
    <!-- ========================= -->
    <div id="userDetailModal" class="user-detail-modal">

        <div class="user-detail-content">

            <!-- 닫기 버튼 -->
            <button type="button"
                    id="userDetailCloseBtn"
                    class="user-detail-close-btn">
                ×
            </button>

            <!-- 프로필 이미지 -->
            <div class="user-detail-image">
                <img id="userDetailPhoto"
                     src=""
                     alt="프로필 사진">
            </div>

            <!-- 유저 정보 -->
            <div class="user-detail-info">
                <h2 id="userDetailNickname"></h2>
                <div id="userDetailBasicInfo"></div>
                <div id="userDetailJob"></div>
                <div id="userDetailRegion"></div>
                <p id="userDetailIntroduction"></p>
                <div id="userDetailInterests"></div>
            </div>
        </div>
    </div>
</div>


<script>
    const contextPath = '${pageContext.request.contextPath}';

    // 로그인 회원 (세션의 userId, 없으면 null → common.js가 로그인 화면으로 보냄)
    const userId = Number('${sessionScope.userId}') || null;
</script>

<script src="${pageContext.request.contextPath}/resources/js/common.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/swipe/swipe.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/user/userList.js"></script>

</body>
</html>
