<%@ page contentType="text/html; charset=UTF-8" %>

<!DOCTYPE html>

<html>
<head>
    <meta charset="UTF-8">
    <title>채팅방</title>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/resources/css/chat/chatRoom.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/common.css">
</head>

<body>

<div class="chat-room-container">

<!-- 채팅방 헤더 -->
<div class="chat-room-header">
    <a href="${pageContext.request.contextPath}/chat/list">
        <button type="button" class="back-btn">←</button>
    </a>
    <div class="chat-room-title">채팅</div>

    <!-- 매칭 나가기 (진행 중인 매칭 / 상대가 먼저 나간 매칭 모두) -->
    <button type="button"
            id="leaveMatchBtn"
            class="leave-match-btn"
            onclick="leaveMatch()">
        매칭 나가기
    </button>
</div>


<!-- 메시지 영역 (chatRoom.js가 /api/chats/{matchId}/messages로 채움) -->
<div id="chatMessages"
     class="chat-messages">
</div>


<!-- 상대가 매칭을 취소한 경우 (chatRoom.js의 switchToReadOnly) -->
<div id="readOnlyNotice" class="chat-input-area read-only-notice" hidden>
    상대방이 매칭을 취소했습니다. 대화 내용만 볼 수 있어요.
</div>

<div id="chatInputArea" class="chat-input-area">
    <button type="button" id="attachBtn" class="attach-btn">📎</button>
    <input type="file" id="fileInput" accept="image/*,video/*" hidden>
    <input type="text" id="messageInput" class="chat-input" placeholder="메시지를 입력하세요">
    <button type="button" id="sendBtn" class="send-btn">전송</button>
</div>


</div>

<script>

    // Context Path
    const contextPath =
        '${pageContext.request.contextPath}';

    // 로그인 회원 (세션의 userId, 없으면 null → common.js가 로그인 화면으로 보냄)
    const userId =
        Number('${sessionScope.userId}') || null;

    // 현재 채팅방 (주소창의 ?matchId=)
    const matchId =
        Number(new URLSearchParams(location.search).get("matchId")) || null;

</script>

<!-- 채팅 JS -->
<script src="${pageContext.request.contextPath}/resources/js/common.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/chat/chatRoom.js"></script>

</body>
</html>
