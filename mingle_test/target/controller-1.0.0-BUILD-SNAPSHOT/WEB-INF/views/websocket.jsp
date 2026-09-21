<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>MINGLE WebSocket Test</title>

    <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>

    <script>
        var contextPath = "${pageContext.request.contextPath}";
    </script>

    <script src="${pageContext.request.contextPath}/resources/js/websocket.js"></script>
</head>

<body>

    <h2>MINGLE WebSocket 테스트</h2>

    <p>
        현재 사용자:
        <strong id="currentUser"></strong>
    </p>

    <button onclick="connect()">
        WebSocket 연결
    </button>

    <button onclick="disconnect()">
        연결 종료
    </button>

    <hr>

    <h3>좋아요 테스트</h3>

    <input
        type="text"
        id="targetUserId"
        placeholder="좋아요 받을 사용자 ID"
    />

    <button onclick="sendLikeToInput()">
        좋아요 보내기
    </button>

    <hr>

    <h3>알림</h3>

    <div id="messages"></div>

</body>
</html>
