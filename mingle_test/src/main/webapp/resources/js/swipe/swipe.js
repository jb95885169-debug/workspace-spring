/* =========================================================
   스와이프 (추천 회원 카드의 패스 / 슈퍼 좋아요 / 좋아요 버튼)
   ========================================================= */

const SWIPE_ACTIONS = {
    "swipe-pass-btn": "PASS",
    "swipe-super-like-btn": "SUPER_LIKE",
    "swipe-like-btn": "LIKE"
};

// 카드가 날아가는 방향 + 도장 (userList.css)
const SWIPE_ANIMATION_CLASS = {
    PASS: "swiped-pass",
    SUPER_LIKE: "swiped-super-like",
    LIKE: "swiped-like"
};

const SWIPE_ANIMATION_MS = 350;

let swiping = false;


document.addEventListener("click", function (event) {

    const button =
        event.target.closest(".user-card .user-actions button");

    if (!button || swiping) {
        return;
    }

    let action = null;

    Object.keys(SWIPE_ACTIONS).forEach(function (className) {

        if (button.classList.contains(className)) {
            action = SWIPE_ACTIONS[className];
        }
    });

    if (!action) {
        return;
    }

    // 오늘 슈퍼 좋아요를 다 썼으면 요청하지 않음 (remainingSuperLikes: userList.js)
    if (action === "SUPER_LIKE" && remainingSuperLikes === 0) {
        alert("오늘 보낼 수 있는 슈퍼 좋아요를 모두 사용했어요. 내일 다시 보낼 수 있어요.");
        return;
    }

    swiping = true;

    const card =
        button.closest(".user-card");

    sendSwipe(button.dataset.userId, action)
        .then(function (result) {

            if (action === "SUPER_LIKE") {
                decreaseRemainingSuperLikes();
            }

            playSwipeAnimation(card, action, function () {

                // 다음 카드 (userList.js)
                showNextCard();

                if (result === "MATCHED") {

                    showNotification({
                        type: "MATCH",
                        message: "매칭되었습니다! 채팅 목록에서 대화를 시작해 보세요."
                    });
                }

                swiping = false;
            });
        })
        .catch(function (error) {

            console.error("스와이프 오류:", error);

            swiping = false;

            // 다른 탭에서 이미 다 쓴 경우 등
            if (error.status === 429) {
                setRemainingSuperLikes(0);
            }

            alert(error.message || "처리 중 오류가 발생했습니다.");
        });
});


function playSwipeAnimation(card, action, done) {

    if (!card) {
        done();
        return;
    }

    card.classList.add(SWIPE_ANIMATION_CLASS[action]);

    setTimeout(done, SWIPE_ANIMATION_MS);
}
