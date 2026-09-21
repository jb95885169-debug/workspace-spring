/* =========================================================
   채팅방 상태
   ========================================================= */

let oldestMessageId = 0;

let loadingMessages = false;

let hasMoreMessages = true;

let firstPageLoaded = false;

// 상대가 매칭을 취소해 대화 내용만 볼 수 있는 상태
let readOnly = false;


/* =========================================================
   Chat Room WebSocket 초기화 (common.js가 연결 후 호출)
   ========================================================= */

function onChatRoomConnect() {

    subscribeChatMessages();

    subscribeChatRead();

    subscribeMatchCancelled();

    // 채팅방 입장 시 기존 안 읽은 메시지를 읽음 처리
    readMessages();
}


function isCurrentRoom(data) {

    return Number(data.matchId) === Number(matchId);
}


function isMyMessage(message) {

    return Number(message.senderId) === Number(userId);
}


/* =========================================================
   채팅 메시지 구독 (/user/queue/chat)
   내가 참여한 모든 채팅방의 메시지가 오므로 현재 채팅방 것만 표시
   ========================================================= */

function subscribeChatMessages() {

    stompClient.subscribe(

        "/user/queue/chat",

        function (frame) {

            const chatMessage =
                JSON.parse(frame.body);

            if (!isCurrentRoom(chatMessage)) {
                return;
            }

            console.log("채팅 메시지 수신:", chatMessage);

            addMessage(chatMessage);

            // 상대방 메시지는 즉시 읽음 처리
            if (!isMyMessage(chatMessage)) {
                readMessages();
            }
        }
    );
}


/* =========================================================
   읽음 이벤트 구독 (/user/queue/chat-read)
   상대가 내 메시지를 읽었을 때 온다
   ========================================================= */

function subscribeChatRead() {

    stompClient.subscribe(

        "/user/queue/chat-read",

        function (frame) {

            const readData =
                JSON.parse(frame.body);

            if (isCurrentRoom(readData)) {
                removeUnreadCounts();
            }
        }
    );
}


/* =========================================================
   상대가 매칭 취소 (/user/queue/match-cancelled)
   채팅방에 있는 동안 취소되면 바로 읽기 전용으로
   ========================================================= */

function subscribeMatchCancelled() {

    stompClient.subscribe(

        "/user/queue/match-cancelled",

        function (frame) {

            const data =
                JSON.parse(frame.body);

            if (isCurrentRoom(data)) {
                switchToReadOnly(true);
            }
        }
    );
}


/* =========================================================
   채팅방 정보 (GET /api/chats/{matchId} → { matchId, readOnly })
   볼 수 없는 채팅방이면 404 / 409 → 목록으로
   ========================================================= */

function loadChatRoom() {

    fetch(contextPath + "/api/chats/" + matchId)
        .then(function (response) {

            return response.json()
                .catch(function () {
                    return {};
                })
                .then(function (data) {

                    if (!response.ok) {

                        alert(data.message || "들어갈 수 없는 채팅방입니다.");

                        // 403은 구독이 필요한 경우 (무료 회원)
                        location.href = (response.status === 403)
                            ? contextPath + "/subscription"
                            : contextPath + "/chat/list";
                        return;
                    }

                    if (data.readOnly) {
                        switchToReadOnly(false);
                    }

                    loadMessages();
                });
        })
        .catch(function (error) {

            console.error("채팅방 조회 오류:", error);
        });
}


/* 대화 내용만 볼 수 있게 (입력창 숨김, 매칭 나가기 버튼은 그대로) */
function switchToReadOnly(notify) {

    if (readOnly) {
        return;
    }

    readOnly = true;

    const inputArea = document.getElementById("chatInputArea");
    const notice = document.getElementById("readOnlyNotice");

    if (inputArea) {
        inputArea.hidden = true;
    }

    if (notice) {
        notice.hidden = false;
    }

    if (notify) {

        showNotification({
            type: "CANCELLED",
            message: "상대방이 매칭을 취소했습니다. 대화 내용만 볼 수 있어요."
        });
    }
}


/* =========================================================
   메시지 조회 (처음 입장 + 위로 스크롤하면 이전 메시지)
   GET /api/chats/{matchId}/messages?lastMessageId=
   ========================================================= */

function loadMessages() {

    if (loadingMessages || !hasMoreMessages) {
        return;
    }

    loadingMessages = true;

    const chatMessages =
        document.getElementById("chatMessages");

    const previousScrollHeight =
        chatMessages.scrollHeight;

    const previousScrollTop =
        chatMessages.scrollTop;

    fetch(
        contextPath +
        "/api/chats/" + matchId +
        "/messages?lastMessageId=" + oldestMessageId
    )
    .then(function (response) {

        // 없는 매칭·참여자 아님(404) / 이미 취소된 매칭(409) / 구독 필요(403) → 본문 { message }
        if (response.status === 404 || response.status === 409 || response.status === 403) {

            return response.json()
                .catch(function () {
                    return {};
                })
                .then(function (data) {

                    alert(data.message || "들어갈 수 없는 채팅방입니다.");

                    location.href = (response.status === 403)
                        ? contextPath + "/subscription"
                        : contextPath + "/chat/list";

                    return null;
                });
        }

        if (!response.ok) {
            throw new Error("메시지 조회 실패: " + response.status);
        }

        return response.json();
    })
    .then(function (messages) {

        if (messages === null) {
            return;
        }

        // 서버가 오래된 순으로 주므로 받은 순서 그대로 묶어서 맨 앞에 붙인다
        const page = document.createDocumentFragment();

        messages.forEach(function (message) {

            // WebSocket으로 먼저 들어온 메시지는 건너뛴다
            if (!findMessageElement(message.id)) {
                page.appendChild(createMessageElement(message));
            }
        });

        chatMessages.prepend(page);

        // 한 번에 몇 개씩 오는지는 서버(ChatMessageService.MESSAGE_PAGE_SIZE)가 정하므로
        // 개수로 판단하지 않고, 더 이전 메시지가 없어서 빈 목록이 오면 그만 조회
        if (messages.length === 0) {
            hasMoreMessages = false;
        }

        updateOldestMessageId();
        rebuildDateSeparators();

        if (!firstPageLoaded) {

            // 처음 입장하면 맨 아래
            firstPageLoaded = true;
            chatMessages.scrollTop = chatMessages.scrollHeight;

        } else {

            // 이전 메시지를 붙인 만큼 스크롤 위치 유지
            chatMessages.scrollTop =
                previousScrollTop +
                (chatMessages.scrollHeight - previousScrollHeight);
        }
    })
    .catch(function (error) {

        console.error("메시지 조회 오류:", error);
    })
    .finally(function () {

        loadingMessages = false;
    });
}


function findMessageElement(messageId) {

    return document.querySelector(
        '#chatMessages .message[data-message-id="' + messageId + '"]'
    );
}


/* 가장 오래된 메시지 ID (다음 조회 기준) */
function updateOldestMessageId() {

    let minId = Infinity;

    document
        .querySelectorAll("#chatMessages .message")
        .forEach(function (message) {

            const id =
                Number(message.dataset.messageId);

            if (id < minId) {
                minId = id;
            }
        });

    if (minId !== Infinity) {
        oldestMessageId = minId;
    }
}


/* =========================================================
   메시지 추가 (WebSocket으로 받은 새 메시지)
   ========================================================= */

function addMessage(message) {

    const chatMessages =
        document.getElementById("chatMessages");

    // 처음 조회와 WebSocket으로 같은 메시지가 두 번 올 수 있음
    if (!chatMessages || findMessageElement(message.id)) {
        return;
    }

    const messages =
        chatMessages.querySelectorAll(".message");

    const lastMessage =
        messages[messages.length - 1];

    // 날짜가 바뀌면 구분선
    if (
        message.createdAt &&
        (!lastMessage ||
            formatDateKey(lastMessage.dataset.createdAt) !== formatDateKey(message.createdAt))
    ) {
        chatMessages.appendChild(createDateSeparator(message.createdAt));
    }

    chatMessages.appendChild(createMessageElement(message));

    // 마지막으로 이동
    chatMessages.scrollTop = chatMessages.scrollHeight;
}


/* =========================================================
   메시지 DOM 생성 (ChatMessageResponse)
   ========================================================= */

function createMessageElement(message) {

    const messageDiv =
        document.createElement("div");

    messageDiv.classList.add(
        "message",
        isMyMessage(message) ? "my-message" : "other-message"
    );

    messageDiv.dataset.messageId = message.id;
    messageDiv.dataset.senderId = message.senderId;
    messageDiv.dataset.createdAt = message.createdAt || "";


    /* -------------------------------------------------
       메시지 내용
       ------------------------------------------------- */

    const content =
        document.createElement("div");

    content.classList.add("message-content");

    if (message.messageType === "IMAGE") {

        const image =
            document.createElement("img");

        image.src = resolveUrl(message.fileUrl);
        image.alt = "사진";
        image.classList.add("message-image");

        content.appendChild(image);

    } else if (message.messageType === "VIDEO") {

        const video =
            document.createElement("video");

        video.src = resolveUrl(message.fileUrl);
        video.controls = true;
        video.classList.add("message-video");

        content.appendChild(video);

    } else {

        content.textContent = message.content || "";
    }


    /* -------------------------------------------------
       정보
       ------------------------------------------------- */

    const info =
        document.createElement("div");

    info.classList.add("message-info");

    // 내가 보낸 메시지 중 상대가 아직 안 읽은 것
    if (isMyMessage(message) && Number(message.isRead) === 0) {

        const unread =
            document.createElement("span");

        unread.classList.add("message-unread-count");
        unread.textContent = "1";

        info.appendChild(unread);
    }

    if (message.createdAt) {

        const time =
            document.createElement("span");

        time.classList.add("message-time");
        time.textContent = formatTime(message.createdAt);

        info.appendChild(time);
    }

    messageDiv.appendChild(content);
    messageDiv.appendChild(info);

    return messageDiv;
}


/* =========================================================
   날짜 구분선
   ========================================================= */

function createDateSeparator(createdAt) {

    const separator =
        document.createElement("div");

    separator.classList.add("date-separator");
    separator.dataset.date = formatDateKey(createdAt);

    const span =
        document.createElement("span");

    span.textContent = formatDate(createdAt);

    separator.appendChild(span);

    return separator;
}


function rebuildDateSeparators() {

    const chatMessages =
        document.getElementById("chatMessages");

    if (!chatMessages) {
        return;
    }

    chatMessages
        .querySelectorAll(".date-separator")
        .forEach(function (separator) {
            separator.remove();
        });

    let previousDate = null;

    chatMessages
        .querySelectorAll(".message")
        .forEach(function (message) {

            const createdAt =
                message.dataset.createdAt;

            if (!createdAt) {
                return;
            }

            const currentDate =
                formatDateKey(createdAt);

            if (currentDate !== previousDate) {

                chatMessages.insertBefore(
                    createDateSeparator(createdAt),
                    message
                );

                previousDate = currentDate;
            }
        });
}


/* =========================================================
   채팅 스크롤 (위로 올리면 이전 메시지)
   ========================================================= */

function initChatScroll() {

    const chatMessages =
        document.getElementById("chatMessages");

    if (!chatMessages) {
        return;
    }

    chatMessages.addEventListener("scroll", function () {

        if (firstPageLoaded && this.scrollTop <= 50) {
            loadMessages();
        }
    });
}


/* =========================================================
   메시지 전송 (STOMP /app/chat/send)
   보낸 사람은 서버가 로그인 정보로 정하므로 senderId는 보내지 않는다.
   ========================================================= */

function ensureConnected() {

    if (stompClient && stompClient.connected) {
        return true;
    }

    alert("채팅 서버에 연결 중입니다. 잠시 후 다시 시도해 주세요.");
    return false;
}


function sendMessage() {

    const input =
        document.getElementById("messageInput");

    if (!input) {
        return;
    }

    const content =
        input.value.trim();

    if (readOnly || content === "" || !ensureConnected()) {
        return;
    }

    stompClient.publish({
        destination: "/app/chat/send",
        body: JSON.stringify({
            matchId: matchId,
            messageType: "TEXT",
            content: content
        })
    });

    input.value = "";
    input.focus();
}


/* =========================================================
   파일 첨부 버튼
   ========================================================= */

function initAttachButton() {

    const attachBtn = document.getElementById("attachBtn");
    const fileInput = document.getElementById("fileInput");

    if (!attachBtn || !fileInput) {
        return;
    }

    attachBtn.addEventListener("click", function () {
        fileInput.click();
    });

    fileInput.addEventListener("change", function () {

        const file = fileInput.files[0];

        if (!file) {
            return;
        }

        uploadAndSendFile(file);

        fileInput.value = "";
    });
}


/* =========================================================
   파일 업로드 후 전송
   POST /chat/upload (file, matchId) → { url, messageType }
   실패하면 서버가 { message }로 이유를 알려준다
   ========================================================= */

function uploadAndSendFile(file) {

    const formData = new FormData();
    formData.append("matchId", matchId);
    formData.append("file", file);

    fetch(contextPath + "/chat/upload", {
        method: "POST",
        body: formData
    })
    .then(function (response) {

        return response.json()
            .catch(function () {
                return {};
            })
            .then(function (data) {

                if (!response.ok) {
                    throw new Error(data.message || "파일 업로드에 실패했습니다.");
                }

                return data;
            });
    })
    .then(function (data) {

        sendFileMessage(data.url, data.messageType);
    })
    .catch(function (error) {

        console.error("파일 업로드 오류:", error);
        alert(error.message);
    });
}


function sendFileMessage(fileUrl, messageType) {

    if (!ensureConnected()) {
        return;
    }

    stompClient.publish({
        destination: "/app/chat/send",
        body: JSON.stringify({
            matchId: matchId,
            messageType: messageType,
            fileUrl: fileUrl
        })
    });
}


/* =========================================================
   읽음 처리 (STOMP /app/chat/read)
   ========================================================= */

function readMessages() {

    if (!stompClient || !stompClient.connected) {
        return;
    }

    stompClient.publish({
        destination: "/app/chat/read",
        body: JSON.stringify({
            matchId: matchId
        })
    });
}


/* 현재 화면의 내가 보낸 메시지 안 읽음 표시 제거 */
function removeUnreadCounts() {

    document
        .querySelectorAll(".my-message .message-unread-count")
        .forEach(function (element) {
            element.remove();
        });
}


/* =========================================================
   전송 버튼 / Enter 입력
   ========================================================= */

function initSendButton() {

    const sendBtn =
        document.getElementById("sendBtn");

    if (sendBtn) {
        sendBtn.addEventListener("click", sendMessage);
    }
}


function initMessageInput() {

    const input =
        document.getElementById("messageInput");

    if (!input) {
        return;
    }

    input.addEventListener("keydown", function (event) {

        // 한글 조합 중 Enter는 무시 (마지막 글자가 한 번 더 전송되는 문제 방지)
        if (event.key === "Enter" && !event.isComposing) {

            event.preventDefault();
            sendMessage();
        }
    });
}


/* =========================================================
   매칭 나가기 (DELETE /api/matches/{matchId})
   진행 중인 매칭이면 상대는 대화 내용만 볼 수 있게 되고,
   상대가 먼저 나간 매칭이면 양쪽 모두에서 사라진다.
   204 성공 / 404 없는 매칭 / 409 이미 나간 매칭 (실패 시 본문 { message })
   ========================================================= */

function leaveMatch() {

    const message = readOnly
        ? "매칭에서 나가시겠어요? 이 대화는 목록에서 사라지고 다시 볼 수 없습니다."
        : "매칭에서 나가시겠어요? 상대방은 더 이상 메시지를 보낼 수 없고, 이 대화는 목록에서 사라집니다.";

    if (!confirm(message)) {
        return;
    }

    fetch(contextPath + "/api/matches/" + matchId, {
        method: "DELETE"
    })
    .then(function (response) {

        if (response.status === 204) {

            alert("매칭에서 나갔습니다.");
            location.href = contextPath + "/chat/list";
            return;
        }

        return response.json()
            .catch(function () {
                return {};
            })
            .then(function (data) {

                alert(data.message || "매칭 취소에 실패했습니다.");

                // 이미 취소됐거나 없는 매칭이면 이 방에 있을 이유가 없음
                if (response.status === 404 || response.status === 409) {
                    location.href = contextPath + "/chat/list";
                }
            });
    })
    .catch(function (error) {

        console.error(error);
        alert("오류가 발생했습니다.");
    });
}


/* =========================================================
   페이지 시작
   ========================================================= */

document.addEventListener("DOMContentLoaded", function () {

    // 로그인하지 않았으면 common.js가 로그인 화면으로 보냄
    if (!userId) {
        return;
    }

    if (!matchId) {
        location.href = contextPath + "/chat/list";
        return;
    }

    // 채팅방 정보(읽기 전용 여부) 확인 후 메시지 조회
    loadChatRoom();
    initChatScroll();
    initSendButton();
    initMessageInput();
    initAttachButton();
});
