/* =========================================================
   추천 회원 카드 (한 장씩 보여줌, .user-card.active만 보임)
   ========================================================= */

let userCards = [];

let currentCardIndex = 0;

// 오늘 남은 슈퍼 좋아요 수 (null이면 아직 조회 전, -1이면 무제한)
let remainingSuperLikes = null;

// 서버의 SubscriptionTier.UNLIMITED와 같은 값
const UNLIMITED_SUPER_LIKES = -1;


/* 추천 회원 조회 GET /api/users */
function loadRecommendedUsers() {

    fetchJson("/api/users")
        .then(function (users) {

            const container =
                document.getElementById("userCardList");

            const emptyMessage =
                container.querySelector(".user-card-empty");

            users.forEach(function (user) {
                container.insertBefore(createUserCard(user), emptyMessage);
            });

            userCards =
                Array.prototype.slice.call(container.querySelectorAll(".user-card"));

            currentCardIndex = 0;
            showCurrentCard();
            updateSuperLikeButtons();
        })
        .catch(function (error) {

            console.error("유저 목록 조회 오류:", error);
            alert("추천 회원을 불러오지 못했습니다.");
        });
}


/* 카드 생성 (UserResponse) */
function createUserCard(user) {

    const card =
        document.createElement("div");

    card.classList.add("user-card");
    card.dataset.userId = user.userId;

    // 나에게 슈퍼 좋아요를 보낸 회원 (서버가 맨 앞으로 정렬해 줌)
    if (user.superLike) {
        card.classList.add("super-liked-me");
    }


    /* 프로필 이미지 */

    const imageBox =
        document.createElement("div");

    imageBox.classList.add("user-image");

    const image =
        document.createElement("img");

    image.src = user.photoUrl
        ? resolveUrl(user.photoUrl)
        : contextPath + "/resources/images/default-profile.png";

    image.alt = user.nickname || "프로필";

    imageBox.appendChild(image);

    if (user.superLike) {

        const banner =
            document.createElement("div");

        banner.classList.add("super-like-banner");
        banner.textContent = "★ 회원님에게 슈퍼 좋아요를 보냈어요";

        imageBox.appendChild(banner);
    }


    /* 유저 정보 */

    const info =
        document.createElement("div");

    info.classList.add("user-info");

    const nickname =
        document.createElement("h2");

    nickname.textContent = user.nickname || "";

    const basicInfo =
        document.createElement("div");

    basicInfo.classList.add("user-basic-info");
    basicInfo.textContent =
        [user.age ? user.age + "세" : null, user.region]
            .filter(Boolean)
            .join(" · ");

    const job =
        document.createElement("div");

    job.classList.add("user-job");
    job.textContent = user.job || "";

    const introduction =
        document.createElement("p");

    introduction.classList.add("user-introduction");
    introduction.textContent = user.introduction || "";

    info.appendChild(nickname);
    info.appendChild(basicInfo);
    info.appendChild(job);
    info.appendChild(introduction);


    /* 버튼 (swipe.js가 처리) */

    const actions =
        document.createElement("div");

    actions.classList.add("user-actions");

    actions.appendChild(createActionButton("swipe-pass-btn", "✕", user.userId));
    actions.appendChild(createActionButton("swipe-super-like-btn", "★", user.userId));
    actions.appendChild(createActionButton("swipe-like-btn", "♥", user.userId));


    card.appendChild(imageBox);
    card.appendChild(info);
    card.appendChild(actions);

    return card;
}


function createActionButton(className, text, targetUserId) {

    const button =
        document.createElement("button");

    button.type = "button";
    button.classList.add(className);
    button.dataset.userId = targetUserId;
    button.textContent = text;

    return button;
}


/* 현재 카드만 보이고, 다 봤으면 안내 문구 */
function showCurrentCard() {

    userCards.forEach(function (card, index) {
        card.classList.toggle("active", index === currentCardIndex);
    });

    document.querySelector(".user-card-empty").style.display =
        currentCardIndex >= userCards.length ? "block" : "none";
}


/* 스와이프 후 다음 카드 (swipe.js에서 호출) */
function showNextCard() {

    currentCardIndex++;
    showCurrentCard();
}


/* =========================================================
   슈퍼 좋아요 남은 개수 (★ 버튼 오른쪽 위 숫자)
   GET /api/swipes/super-likes/remaining → { remaining, limit }
   ========================================================= */

function loadRemainingSuperLikes() {

    fetchJson("/api/swipes/super-likes/remaining")
        .then(function (data) {

            setRemainingSuperLikes(data.remaining);
        })
        .catch(function (error) {

            console.error("슈퍼 좋아요 개수 조회 오류:", error);
        });
}


function setRemainingSuperLikes(count) {

    remainingSuperLikes = count;
    updateSuperLikeButtons();
}


/* 슈퍼 좋아요를 보낸 뒤 1 감소 (swipe.js에서 호출) */
function decreaseRemainingSuperLikes() {

    // 아직 조회 전(null)이거나 무제한 등급(-1)이면 줄이지 않는다
    if (remainingSuperLikes !== null && remainingSuperLikes > 0) {
        setRemainingSuperLikes(remainingSuperLikes - 1);
    }
}


function updateSuperLikeButtons() {

    if (remainingSuperLikes === null) {
        return;
    }

    // 플래티넘은 제한이 없어서 서버가 -1을 준다
    const unlimited = remainingSuperLikes === UNLIMITED_SUPER_LIKES;

    document
        .querySelectorAll(".swipe-super-like-btn")
        .forEach(function (button) {

            button.dataset.remaining = unlimited ? "∞" : remainingSuperLikes;
            button.classList.toggle("empty", remainingSuperLikes === 0);

            button.title = unlimited
                ? "슈퍼 좋아요 (무제한)"
                : "슈퍼 좋아요 (오늘 " + remainingSuperLikes + "개 남음)";
        });
}


/* =========================================================
   유저 상세 모달
   ========================================================= */

function loadUserDetail(targetUserId) {

    fetchJson("/api/users/" + targetUserId)
        .then(function (user) {

            showUserDetail(user);
        })
        .catch(function (error) {

            console.error(error);
            alert("유저 정보를 불러오는 중 오류가 발생했습니다.");
        });
}


function showUserDetail(user) {

    document.getElementById("userDetailNickname").textContent =
        user.nickname || "";

    document.getElementById("userDetailBasicInfo").textContent =
        [
            user.age ? user.age + "세" : null,
            user.gender,
            user.height ? user.height + "cm" : null
        ]
        .filter(Boolean)
        .join(" · ");

    document.getElementById("userDetailJob").textContent =
        user.job || "";

    document.getElementById("userDetailRegion").textContent =
        user.region || "";

    document.getElementById("userDetailIntroduction").textContent =
        user.introduction || "";

    document.getElementById("userDetailInterests").textContent =
        user.interests || "";

    document.getElementById("userDetailPhoto").src =
        user.photoUrl
            ? resolveUrl(user.photoUrl)
            : contextPath + "/resources/images/default-profile.png";

    document.getElementById("userDetailModal").classList.add("show");
}


/* =========================================================
   페이지 시작
   ========================================================= */

document.addEventListener("DOMContentLoaded", function () {

    // 로그인하지 않았으면 common.js가 로그인 화면으로 보냄
    if (!userId) {
        return;
    }

    const modal =
        document.getElementById("userDetailModal");

    // 카드 클릭 → 상세 (좋아요 / 슈퍼 좋아요 / 패스 버튼 영역은 제외)
    document.getElementById("userCardList")
        .addEventListener("click", function (event) {

            if (event.target.closest(".user-actions")) {
                return;
            }

            const card =
                event.target.closest(".user-card");

            if (card) {
                loadUserDetail(card.dataset.userId);
            }
        });

    // 닫기 버튼
    document.getElementById("userDetailCloseBtn")
        .addEventListener("click", function () {
            modal.classList.remove("show");
        });

    // 모달 바깥쪽 클릭하면 닫기
    modal.addEventListener("click", function (event) {

        if (event.target === modal) {
            modal.classList.remove("show");
        }
    });

    loadRecommendedUsers();
    loadRemainingSuperLikes();
});
