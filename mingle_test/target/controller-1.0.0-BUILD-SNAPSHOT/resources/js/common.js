/* =========================================================
   공통 (WebSocket / 알림 / REST / 시간 포맷)

   페이지에서 먼저 선언해야 하는 전역 변수
   - contextPath
   - userId : 로그인 회원 ID (로그인하지 않았으면 null)
   ========================================================= */

let stompClient = null;


function loadStompJs(callback) {

    // 이미 로드되어 있으면 바로 실행
    if (window.StompJs) {
        callback();
        return;
    }

    const script =
        document.createElement("script");

    script.src =
        "https://cdn.jsdelivr.net/npm/@stomp/stompjs@7.0.0/bundles/stomp.umd.min.js";

    script.onload = function () {

        console.log("STOMP.js 로드 완료");

        callback();
    };

    script.onerror = function () {

        console.error("STOMP.js 로드 실패");
    };

    document.head.appendChild(script);
}


/* =========================================================
   WebSocket 연결
   서버가 로그인 세션으로 회원을 확인하므로 주소에 userId를 붙이지 않는다.
   ========================================================= */

function connectWebSocket() {

    if (stompClient && stompClient.connected) {
        return;
    }

    const protocol =
        location.protocol === "https:"
            ? "wss://"
            : "ws://";

    stompClient =
        new StompJs.Client({

            brokerURL:
                protocol +
                location.host +
                contextPath +
                "/ws-chat",

            reconnectDelay: 5000,


            /* -------------------------------------------------
               연결 성공 (재연결 때마다 다시 호출됨)
               ------------------------------------------------- */

            onConnect: function () {

                console.log("공통 WebSocket 연결 성공");

                // 공통 구독
                subscribeNotifications();
                subscribeErrors();

                // 페이지별 구독
                if (typeof onChatListConnect === "function") {
                    onChatListConnect();
                }

                if (typeof onChatRoomConnect === "function") {
                    onChatRoomConnect();
                }
            },


            /* -------------------------------------------------
               오류
               ------------------------------------------------- */

            onWebSocketError: function (error) {

                console.error("WebSocket 연결 오류:", error);
            },

            onStompError: function (frame) {

                console.error("STOMP 오류:", frame);
            }
        });

    stompClient.activate();
}


/* =========================================================
   공통 알림 구독 (/user/queue/notification)
   ========================================================= */

function subscribeNotifications() {

    stompClient.subscribe(

        "/user/queue/notification",

        function (message) {

            const notification =
                JSON.parse(message.body);

            console.log("알림 수신:", notification);

            // 지금 그 채팅방을 보고 있으면 메시지가 화면에 바로 뜨므로 토스트는 생략
            if (notification.type === "CHAT" && isViewingChatRoom(notification.targetId)) {
                return;
            }

            showNotification(notification);
        }
    );
}


/* =========================================================
   STOMP 처리 실패 (/user/queue/errors)
   메시지 전송이 거부됐을 때 등 (본문: { message })
   ========================================================= */

function subscribeErrors() {

    stompClient.subscribe(

        "/user/queue/errors",

        function (message) {

            const error =
                JSON.parse(message.body);

            console.warn("STOMP 처리 실패:", error);

            showNotification({
                type: "ERROR",
                message: error.message
            });
        }
    );
}


/* 채팅방 화면(chatRoom.jsp)만 matchId를 선언한다 */
function isViewingChatRoom(targetId) {

    return typeof matchId !== "undefined"
        && matchId !== null
        && Number(matchId) === Number(targetId);
}


/* =========================================================
   알림 표시
   ========================================================= */

function showNotification(notification) {

    const container =
        getNotificationContainer();

    const element =
        document.createElement("div");

    element.classList.add("notification-toast");


    /* -------------------------------------------------
       아이콘 / 제목
       ------------------------------------------------- */

    const icon =
        document.createElement("div");

    icon.classList.add("notification-icon");

    const title =
        document.createElement("div");

    title.classList.add("notification-title");

    if (notification.type === "LIKE") {

        icon.textContent = "♥";
        title.textContent = "새로운 좋아요";

    } else if (notification.type === "MATCH") {

        icon.textContent = "💬";
        title.textContent = "새로운 매칭";

    } else if (notification.type === "CHAT") {

        icon.textContent = "✉";
        title.textContent = notification.nickname + "님의 메시지";

    } else if (notification.type === "ERROR") {

        icon.textContent = "⚠";
        title.textContent = "처리 실패";

    } else {

        icon.textContent = "🔔";
        title.textContent = "알림";
    }


    /* -------------------------------------------------
       내용
       ------------------------------------------------- */

    const content =
        document.createElement("div");

    content.classList.add("notification-content");

    const message =
        document.createElement("div");

    message.classList.add("notification-message");

    message.textContent =
        notification.message || "";

    content.appendChild(title);
    content.appendChild(message);


    /* -------------------------------------------------
       닫기
       ------------------------------------------------- */

    const closeButton =
        document.createElement("button");

    closeButton.type = "button";
    closeButton.classList.add("notification-close");
    closeButton.textContent = "×";

    closeButton.addEventListener("click", function (event) {

        event.stopPropagation();
        removeNotification(element);
    });


    /* -------------------------------------------------
       조립
       ------------------------------------------------- */

    element.appendChild(icon);
    element.appendChild(content);
    element.appendChild(closeButton);


    /* -------------------------------------------------
       알림 클릭 → 해당 채팅방 (없으면 채팅 목록)
       오류 알림은 이동하지 않는다
       ------------------------------------------------- */

    if (notification.type !== "ERROR") {

        element.addEventListener("click", function () {

            location.href = notification.targetId
                ? contextPath + "/chat/room?matchId=" + notification.targetId
                : contextPath + "/chat/list";
        });
    }

    container.appendChild(element);


    // 표시 애니메이션
    requestAnimationFrame(function () {
        element.classList.add("show");
    });

    // 4초 후 자동 제거
    setTimeout(function () {
        removeNotification(element);
    }, 4000);
}


function getNotificationContainer() {

    let container =
        document.getElementById("notificationContainer");

    if (!container) {

        container =
            document.createElement("div");

        container.id = "notificationContainer";

        document.body.appendChild(container);
    }

    return container;
}


function removeNotification(element) {

    if (!element) {
        return;
    }

    element.classList.remove("show");

    setTimeout(function () {

        if (element.parentNode) {
            element.parentNode.removeChild(element);
        }

    }, 250);
}


/* =========================================================
   REST 조회 (실패하면 status가 담긴 Error를 던짐)
   ========================================================= */

function fetchJson(path) {

    return fetch(contextPath + path)
        .then(function (response) {

            if (!response.ok) {

                const error =
                    new Error("요청 실패: " + response.status);

                error.status = response.status;
                throw error;
            }

            return response.json();
        });
}


/* "/resources/..." 같은 서버 경로에 contextPath를 붙임 */
function resolveUrl(url) {

    if (url && url.charAt(0) === "/" && url.charAt(1) !== "/") {
        return contextPath + url;
    }

    return url;
}


/* =========================================================
   Swipe 공통 함수
   결과 문자열 반환: "MATCHED"(매칭 성사) / "OK"
   ========================================================= */

function sendSwipe(targetId, action) {

    return fetch(
        contextPath + "/api/swipes",
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                targetId: targetId,
                action: action
            })
        }
    )
    .then(function (response) {

        if (!response.ok) {

            // 실패하면 서버가 { message }로 이유를 알려줌 (예: 429 오늘 슈퍼 좋아요 소진)
            return response.json()
                .catch(function () {
                    return {};
                })
                .then(function (data) {

                    const error =
                        new Error(data.message || "Swipe 처리 실패: " + response.status);

                    error.status = response.status;
                    throw error;
                });
        }

        return response.text();
    })
    .then(function (result) {

        console.log("Swipe 성공:", result);

        return result;
    });
}


/* =========================================================
   시간 포맷 (서버의 Date는 밀리초 숫자로 옴)
   ========================================================= */

function formatTime(value) {

    const date =
        new Date(Number(value));

    if (isNaN(date.getTime())) {
        return "";
    }

    let hours =
        date.getHours();

    const minutes =
        String(date.getMinutes()).padStart(2, "0");

    const ampm =
        hours < 12 ? "오전" : "오후";

    hours = hours % 12;

    if (hours === 0) {
        hours = 12;
    }

    return ampm + " " + hours + ":" + minutes;
}


/* 날짜 Key (yyyy-MM-dd) */
function formatDateKey(value) {

    const date =
        new Date(Number(value));

    if (isNaN(date.getTime())) {
        return "";
    }

    return (
        date.getFullYear() +
        "-" +
        String(date.getMonth() + 1).padStart(2, "0") +
        "-" +
        String(date.getDate()).padStart(2, "0")
    );
}


/* 날짜 표시 (yyyy년 M월 d일) */
function formatDate(value) {

    const date =
        new Date(Number(value));

    if (isNaN(date.getTime())) {
        return "";
    }

    return (
        date.getFullYear() + "년 " +
        (date.getMonth() + 1) + "월 " +
        date.getDate() + "일"
    );
}


/* =========================================================
   페이지 이동
   ========================================================= */

/* 추천 회원(스와이프) 화면 (chatList.jsp 뒤로가기 버튼) */
function goUserList() {
    location.href = contextPath + "/users/list";
}


/* =========================================================
   공통 초기화
   ========================================================= */

document.addEventListener(
    "DOMContentLoaded",
    function () {

        const css =
            document.createElement("link");

        css.rel = "stylesheet";
        css.href = contextPath + "/resources/css/common.css";

        document.head.appendChild(css);

        // 로그인하지 않았으면 WebSocket 연결(401)을 시도하지 않고 로그인 화면으로
        if (!userId) {
            location.href = contextPath + "/login";
            return;
        }

        loadStompJs(connectWebSocket);
    }
);
