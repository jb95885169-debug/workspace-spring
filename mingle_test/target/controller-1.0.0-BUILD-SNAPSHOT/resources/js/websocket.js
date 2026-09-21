var stompClient = null;

var userId = new URLSearchParams(
    window.location.search
).get("userId");


// 페이지 로딩 시 사용자 표시
window.onload = function() {

    document.getElementById("currentUser").innerText =
        userId || "사용자 없음";
};


// =========================
// WebSocket 연결
// =========================
function connect() {

    if (!userId) {
        alert("userId가 없습니다.");
        return;
    }

    console.log("현재 사용자:", userId);

    var socket = new SockJS(
        contextPath + "/ws"
    );

    stompClient = Stomp.over(socket);

    stompClient.connect(
        {
            userId: userId
        },

        function(frame) {

            console.log("Connected:", frame);

            addMessage(
                "WebSocket 연결됨"
            );

            // 개인 알림 구독
            stompClient.subscribe(
                "/user/queue/notification",

                function(message) {

                    console.log(
                        "받은 알림:",
                        message.body
                    );

                    var notification =
                        JSON.parse(message.body);

                    showNotification(
                        notification
                    );
                }
            );
        },

        function(error) {

            console.error(
                "WebSocket 연결 실패:",
                error
            );
        }
    );
}


// =========================
// 좋아요 보내기
// =========================
function sendLike(targetUserId) {

    if (!stompClient ||
        !stompClient.connected) {

        alert("먼저 WebSocket을 연결하세요.");
        return;
    }

    if (!targetUserId) {
        alert("상대방 ID가 없습니다.");
        return;
    }

    console.log(
        userId + " → " + targetUserId
    );

    stompClient.send(
        "/app/like",
        {},

        JSON.stringify({

            fromUserId: userId,

            toUserId: targetUserId

        })
    );
}


// 입력창에서 좋아요 보내기
function sendLikeToInput() {

    var targetUserId =
        document.getElementById(
            "targetUserId"
        ).value;

    sendLike(targetUserId);
}


// =========================
// 받은 알림 표시
// =========================
function showNotification(notification) {

    var messages =
        document.getElementById("messages");

    var messageDiv =
        document.createElement("div");

    messageDiv.style.marginBottom = "10px";

    messageDiv.innerHTML =
        "<p>🔔 " +
        notification.message +
        "</p>" +

        "<button type='button' " +
        "onclick=\"sendLike('" +
        notification.fromUserId +
        "')\">" +
        "좋아요 보내기" +
        "</button>";

    messages.appendChild(
        messageDiv
    );
}


// =========================
// 메시지 출력
// =========================
function addMessage(message) {

    var messages =
        document.getElementById("messages");

    messages.innerHTML +=
        "<p>" + message + "</p>";
}


// =========================
// WebSocket 종료
// =========================
function disconnect() {

    if (stompClient !== null) {

        stompClient.disconnect(
            function() {

                console.log(
                    "WebSocket disconnected"
                );

                addMessage(
                    "WebSocket 연결 종료"
                );
            }
        );
    }
}
