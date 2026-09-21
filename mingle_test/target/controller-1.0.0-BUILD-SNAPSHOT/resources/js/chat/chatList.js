/* =========================================================
   Chat List WebSocket 초기화 (common.js가 연결 후 호출)
   ========================================================= */

function onChatListConnect() {

    subscribeChatList();
    subscribeReceivedLikes();
}


/* =========================================================
   채팅리스트 WebSocket (/user/queue/chat-list)
   ========================================================= */

function subscribeChatList() {

    stompClient.subscribe(

        "/user/queue/chat-list",

        function (message) {

            const data =
                JSON.parse(message.body);

            console.log("채팅리스트 이벤트:", data);

            renderChatItem(data);

            // 매칭되면 받은 좋아요 목록에서 제거
            removeReceivedLike(data.userId);
        }
    );
}


/* =========================================================
   받은 좋아요 WebSocket (/user/queue/like)
   ========================================================= */

function subscribeReceivedLikes() {

    stompClient.subscribe(

        "/user/queue/like",

        function (message) {

            const user =
                JSON.parse(message.body);

            console.log("새로운 좋아요:", user);

            addReceivedLike(user, false);
        }
    );
}


/* =========================================================
   채팅 목록 (서버가 정한 개수씩, 아래로 스크롤하면 다음 페이지)
   GET /api/chats?page= → { items, page, size, totalCount, hasNext }
   ========================================================= */

// 지금까지 받은 페이지 (0이면 아직 못 받음)
let chatPage = 0;

let loadingChats = false;

let hasMoreChats = true;


function loadChatList() {

    if (loadingChats || !hasMoreChats) {
        return;
    }

    loadingChats = true;

    fetchJson("/api/chats?page=" + (chatPage + 1))
        .then(function (data) {

            const chatList =
                document.getElementById("chatList");

            data.items.forEach(function (match) {

                // WebSocket으로 먼저 들어온 항목은 이미 최신이므로 그대로 둠
                if (!findChatItem(match.matchId)) {
                    chatList.appendChild(createChatItem(match));
                }
            });

            chatPage = data.page;
            hasMoreChats = data.hasNext;

            toggleEmptyMessage("chatList", ".chat-item", "chat-list-empty", "아직 채팅이 없습니다.");
        })
        .catch(function (error) {

            console.error("채팅 목록 조회 오류:", error);
        })
        .finally(function () {

            loadingChats = false;
        });
}


/* 화면 아래에 가까워지면 다음 페이지 */
function initChatListScroll() {

    window.addEventListener("scroll", function () {

        const bottom =
            window.innerHeight + window.scrollY >= document.body.offsetHeight - 100;

        if (bottom) {
            loadChatList();
        }
    });
}


/* =========================================================
   받은 좋아요 (서버가 정한 개수씩, 옆으로 끝까지 밀면 다음 페이지)
   GET /api/users/likes?page= → { items, page, size, totalCount, hasNext }
   ========================================================= */

let likePage = 0;

let loadingLikes = false;

let hasMoreLikes = true;


function loadReceivedLikes() {

    if (loadingLikes || !hasMoreLikes) {
        return;
    }

    loadingLikes = true;

    fetchJson("/api/users/likes?page=" + (likePage + 1))
        .then(function (data) {

            // 처음 조회는 서버 정렬 순서대로 뒤에 붙인다
            data.items.forEach(function (user) {
                addReceivedLike(user, true);
            });

            likePage = data.page;
            hasMoreLikes = data.hasNext;

            toggleEmptyMessage("likeList", ".like-item", "like-list-empty", "아직 좋아요가 없습니다.");
        })
        .catch(function (error) {

            console.error("받은 좋아요 조회 오류:", error);
        })
        .finally(function () {

            loadingLikes = false;
        });
}


/* 가로 목록이라 오른쪽 끝에 가까워지면 다음 페이지 */
function initLikeListScroll() {

    const likeList = document.getElementById("likeList");

    if (!likeList) {
        return;
    }

    likeList.addEventListener("scroll", function () {

        const end =
            likeList.scrollLeft + likeList.clientWidth >= likeList.scrollWidth - 50;

        if (end) {
            loadReceivedLikes();
        }
    });
}


/* 목록이 비었으면 안내 문구 표시, 항목이 생기면 제거 */
function toggleEmptyMessage(listId, itemSelector, emptyClass, emptyText) {

    const list =
        document.getElementById(listId);

    if (!list) {
        return;
    }

    const empty =
        list.querySelector("." + emptyClass);

    const hasItem =
        list.querySelector(itemSelector) !== null;

    if (hasItem && empty) {

        empty.remove();

    } else if (!hasItem && !empty) {

        const emptyDiv =
            document.createElement("div");

        emptyDiv.classList.add(emptyClass);
        emptyDiv.textContent = emptyText;

        list.appendChild(emptyDiv);
    }
}


/* =========================================================
   채팅리스트 갱신
   ========================================================= */

function findChatItem(matchId) {

    return document.querySelector(
        '.chat-item[data-match-id="' + matchId + '"]'
    );
}


function renderChatItem(data) {

    const chatList =
        document.getElementById("chatList");

    if (!chatList) {
        return;
    }

    let chatItem =
        findChatItem(data.matchId);

    if (chatItem) {
        updateChatItem(chatItem, data);
    } else {
        chatItem = createChatItem(data);
    }

    // 최근 메시지 받은 채팅방을 맨 위로 이동
    chatList.prepend(chatItem);

    toggleEmptyMessage("chatList", ".chat-item", "chat-list-empty", "아직 채팅이 없습니다.");
}


function updateChatItem(chatItem, data) {

    chatItem.querySelector(".last-message").textContent =
        getLastMessagePreview(data);

    const lastMessageTime =
        chatItem.querySelector(".last-message-time");

    if (lastMessageTime && data.lastMessageAt) {
        lastMessageTime.textContent = formatTime(data.lastMessageAt);
    }

    updateUnreadBadge(chatItem, data.unreadCount);

    updateReadOnlyBadge(chatItem, data.readOnly);
}


/* 마지막 메시지 미리보기 (사진/동영상은 서버가 "사진"/"동영상"으로 저장해 둠) */
function getLastMessagePreview(data) {

    return data.lastMessage || "대화를 시작해보세요.";
}


/* =========================================================
   안 읽은 숫자 갱신
   ========================================================= */

function updateUnreadBadge(chatItem, unreadCount) {

    let unreadBadge =
        chatItem.querySelector(".unread-badge");

    const count =
        Number(unreadCount);

    if (count > 0) {

        if (!unreadBadge) {

            unreadBadge =
                document.createElement("span");

            unreadBadge.classList.add("unread-badge");

            chatItem.querySelector(".chat-bottom")
                .appendChild(unreadBadge);
        }

        unreadBadge.textContent = count;

    } else if (unreadBadge) {

        unreadBadge.remove();
    }
}


/* =========================================================
   새 채팅 아이템 생성 (MatchChatResponse)
   ========================================================= */

function createChatItem(data) {

    const chatItem =
        document.createElement("div");

    chatItem.classList.add("chat-item");

    chatItem.dataset.matchId =
        data.matchId;


    /* -------------------------------------------------
       프로필
       ------------------------------------------------- */

    const chatProfile =
        document.createElement("div");

    chatProfile.classList.add("chat-profile");

    if (data.photoUrl) {

        const image =
            document.createElement("img");

        image.src = resolveUrl(data.photoUrl);
        image.alt = "프로필";
        image.classList.add("chat-profile-image");

        chatProfile.appendChild(image);

    } else {

        const placeholder =
            document.createElement("div");

        placeholder.classList.add("chat-profile-placeholder");
        placeholder.textContent = "?";

        chatProfile.appendChild(placeholder);
    }


    /* -------------------------------------------------
       정보
       ------------------------------------------------- */

    const chatInfo =
        document.createElement("div");

    chatInfo.classList.add("chat-info");


    /* 상단 */

    const chatTop =
        document.createElement("div");

    chatTop.classList.add("chat-top");

    const nickname =
        document.createElement("span");

    nickname.classList.add("chat-nickname");
    nickname.textContent = data.nickname || "";

    const time =
        document.createElement("span");

    time.classList.add("last-message-time");

    if (data.lastMessageAt) {
        time.textContent = formatTime(data.lastMessageAt);
    }

    chatTop.appendChild(nickname);
    chatTop.appendChild(time);


    /* 하단 */

    const chatBottom =
        document.createElement("div");

    chatBottom.classList.add("chat-bottom");

    const lastMessage =
        document.createElement("span");

    lastMessage.classList.add("last-message");
    lastMessage.textContent = getLastMessagePreview(data);

    chatBottom.appendChild(lastMessage);


    chatInfo.appendChild(chatTop);
    chatInfo.appendChild(chatBottom);

    chatItem.appendChild(chatProfile);
    chatItem.appendChild(chatInfo);

    updateUnreadBadge(chatItem, data.unreadCount);

    updateReadOnlyBadge(chatItem, data.readOnly);

    return chatItem;
}


/* =========================================================
   상대가 매칭을 취소한 대화 (대화 내용만 볼 수 있음)
   ========================================================= */

function updateReadOnlyBadge(chatItem, readOnly) {

    chatItem.classList.toggle("read-only", !!readOnly);

    let badge =
        chatItem.querySelector(".read-only-badge");

    if (readOnly && !badge) {

        badge = document.createElement("span");
        badge.classList.add("read-only-badge");
        badge.textContent = "대화 종료";

        chatItem.querySelector(".chat-nickname").after(badge);

    } else if (!readOnly && badge) {

        badge.remove();
    }
}


/* =========================================================
   받은 좋아요 추가 (UserResponse)
   atEnd: 처음 목록 조회는 순서대로 뒤에, 실시간 좋아요는 맨 앞에
   ========================================================= */

function addReceivedLike(user, atEnd) {

    const likeList =
        document.getElementById("likeList");

    if (!likeList || !user || !user.userId) {
        return;
    }

    // 이미 있으면 추가하지 않음
    if (likeList.querySelector('.like-item[data-user-id="' + user.userId + '"]')) {
        return;
    }

    const likeItem =
        document.createElement("div");

    likeItem.classList.add("like-item");
    likeItem.dataset.userId = user.userId;

    // 무료 회원에게는 서버가 닉네임을 가려서 보낸다 (사진은 흐리게)
    if (user.locked) {
        likeItem.classList.add("locked");
    }

    if (user.superLike) {
        likeItem.classList.add("super-liked-me");
    }


    /* 프로필 */

    const profile =
        document.createElement("div");

    profile.classList.add("like-profile");

    if (user.photoUrl) {

        const image =
            document.createElement("img");

        image.src = resolveUrl(user.photoUrl);
        image.alt = "프로필";
        image.classList.add("like-image");

        profile.appendChild(image);

    } else {

        const placeholder =
            document.createElement("div");

        placeholder.classList.add("like-image", "like-placeholder");
        placeholder.textContent = "?";

        profile.appendChild(placeholder);
    }


    /* 이름 */

    const name =
        document.createElement("div");

    name.classList.add("like-name");
    name.textContent = user.nickname || "";

    if (user.superLike) {

        const badge =
            document.createElement("span");

        badge.classList.add("super-like-badge");
        badge.textContent = "★ ";

        name.prepend(badge);
    }


    /* 좋아요 버튼 */

    const likeButton =
        document.createElement("button");

    likeButton.type = "button";
    likeButton.classList.add("like-back-btn");
    likeButton.dataset.userId = user.userId;
    likeButton.textContent = "♡";


    likeItem.appendChild(profile);
    likeItem.appendChild(name);

    if (user.locked) {

        // 무료 회원은 목록에서 좋아요를 보낼 수 없다 (추천 카드에서 만나면 그때 보낼 수 있음)
        likeItem.addEventListener("click", function () {

            if (confirm("누가 보냈는지 보려면 골드 이상 구독이 필요해요. 구독 화면으로 갈까요?")) {
                location.href = contextPath + "/subscription";
            }
        });

    } else {
        likeItem.appendChild(likeButton);
    }

    if (atEnd) {

        // 처음 조회: 서버가 슈퍼 좋아요 → 최신순으로 정렬해서 줌
        likeList.appendChild(likeItem);

    } else if (user.superLike) {

        // 실시간 슈퍼 좋아요는 맨 앞
        likeList.prepend(likeItem);

    } else {

        // 실시간 일반 좋아요는 슈퍼 좋아요들 바로 뒤
        const superLikes =
            likeList.querySelectorAll(".like-item.super-liked-me");

        const lastSuperLike =
            superLikes[superLikes.length - 1];

        if (lastSuperLike) {
            lastSuperLike.after(likeItem);
        } else {
            likeList.prepend(likeItem);
        }
    }

    toggleEmptyMessage("likeList", ".like-item", "like-list-empty", "아직 좋아요가 없습니다.");
}


/* 좋아요 받은 사람 제거 */
function removeReceivedLike(targetUserId) {

    const likeItem =
        document.querySelector('.like-item[data-user-id="' + targetUserId + '"]');

    if (likeItem) {

        likeItem.remove();

        toggleEmptyMessage("likeList", ".like-item", "like-list-empty", "아직 좋아요가 없습니다.");
    }
}


/* =========================================================
   전체 클릭 이벤트
   ========================================================= */

document.addEventListener(
    "click",
    function (event) {

        /* -------------------------------------------------
           좋아요 버튼 → 서로 좋아요라 매칭됨
           (매칭되면 서버가 /user/queue/chat-list로 채팅방을 보내 줌)
           ------------------------------------------------- */

        const likeButton =
            event.target.closest(".like-back-btn");

        if (likeButton) {

            const targetId =
                likeButton.dataset.userId;

            likeButton.disabled = true;

            sendSwipe(targetId, "LIKE")
                .then(function (result) {

                    if (result === "MATCHED") {
                        removeReceivedLike(targetId);
                    }
                })
                .catch(function (error) {

                    console.error(error);

                    likeButton.disabled = false;
                    alert("좋아요 처리 중 오류가 발생했습니다.");
                });

            return;
        }


        /* -------------------------------------------------
           채팅 아이템 클릭
           ------------------------------------------------- */

        const chatItem =
            event.target.closest(".chat-item");

        if (chatItem && chatItem.dataset.matchId) {

            location.href =
                contextPath +
                "/chat/room?matchId=" +
                chatItem.dataset.matchId;
        }
    }
);


document.addEventListener("DOMContentLoaded", function () {

    // 로그인하지 않았으면 common.js가 로그인 화면으로 보냄
    if (!userId) {
        return;
    }

    loadChatList();
    initChatListScroll();

    loadReceivedLikes();
    initLikeListScroll();
});
