<%@ page contentType="text/html; charset=UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>메시지</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/resources/css/chat/chatList.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/common.css">
</head>

<body>

<div class="chat-list-container">

    <!-- =========================
         헤더
         ========================= -->

    <header class="chat-list-header">
    	<button type="button" onclick="goUserList()">
    		←
		</button>
        <h2>메시지</h2>
        <a class="logout-link" href="${pageContext.request.contextPath}/feeds">피드</a>
        <a class="logout-link" href="${pageContext.request.contextPath}/logout">로그아웃</a>
    </header>


    <!-- =========================
         좋아요 (chatList.js가 /api/users/likes로 채움)
         ========================= -->

    <section class="like-section">

        <div class="section-header">
            <h3>좋아요</h3>
        </div>

        <div class="like-list" id="likeList"></div>

    </section>


    <!-- =========================
         채팅 (chatList.js가 /api/chats로 채움)
         ========================= -->

    <section class="chat-section">

        <div class="section-header">
            <h3>채팅</h3>
        </div>

        <div class="chat-list" id="chatList"></div>

    </section>

</div>


<script>
    const contextPath =
        '${pageContext.request.contextPath}';

    // 로그인 회원 (세션의 userId, 없으면 null → common.js가 로그인 화면으로 보냄)
    const userId =
        Number('${sessionScope.userId}') || null;
</script>
<script src="${pageContext.request.contextPath}/resources/js/common.js"></script>

<script src="${pageContext.request.contextPath}/resources/js/chat/chatList.js"></script>

</body>
</html>
