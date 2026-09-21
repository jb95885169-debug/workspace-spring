/* =========================================================
   구독 (상품 목록 / 결제 / 내역)

    PortOne 결제창을 열고, 결제 결과는 서버가 다시 검증한 뒤
    결제 내역과 구독을 함께 만든다.
   ========================================================= */

const TIER_NAMES = {
    BASIC: "무료 회원",
    GOLD: "골드",
    PLATINUM: "플래티넘"
};

// 등급별 혜택 (서버의 type.SubscriptionTier와 같은 값)
const TIER_SUPER_LIKES = {
    BASIC: "슈퍼 좋아요 하루 1개",
    GOLD: "슈퍼 좋아요 하루 3개 · 받은 좋아요 확인 · 채팅",
    PLATINUM: "슈퍼 좋아요 무제한 · 내 좋아요 우선 노출"
};

// 살 수 있는지, 얼마를 낼지는 서버가 상품마다 계산해서 내려 준다
// (buyable / payAmount / note — SubscriptionService.getProducts)


document.addEventListener("DOMContentLoaded", function () {

    // 로그인하지 않았으면 common.js가 로그인 화면으로 보냄
    if (!userId) {
        return;
    }

    loadMySubscription();
    loadProducts();
    loadHistory();
});


/* =========================================================
   현재 등급
   ========================================================= */

function loadMySubscription() {

    fetchJson("/api/subscriptions/me")
        .then(function (subscription) {

            const tierName = document.getElementById("tierName");
            const tierPeriod = document.getElementById("tierPeriod");

            tierName.textContent = TIER_NAMES[subscription.tier] || subscription.tier;

            document.getElementById("tierBox")
                .classList.add("tier-" + subscription.tier.toLowerCase());

            const benefit = TIER_SUPER_LIKES[subscription.tier] || "";

            if (subscription.endDate) {

                tierPeriod.textContent =
                    benefit + " · " + formatDate(subscription.endDate) + "까지 (" + subscription.productName + ")";

            } else {
                tierPeriod.textContent = benefit + " · 구독하면 슈퍼 좋아요를 더 보낼 수 있어요.";
            }
        })
        .catch(function (error) {

            console.error("구독 조회 오류:", error);
        });
}


/* =========================================================
   상품 목록
   ========================================================= */

function loadProducts() {

    fetchJson("/api/subscriptions/products")
        .then(function (products) {

            const list = document.getElementById("productList");

            if (products.length === 0) {
                list.textContent = "판매 중인 상품이 없습니다.";
                return;
            }

            products.forEach(function (product) {
                list.appendChild(createProductCard(product));
            });
        })
        .catch(function (error) {

            console.error("상품 조회 오류:", error);
        });
}


function createProductCard(product) {

    const card = document.createElement("div");
    card.classList.add("product-card");

    const name = document.createElement("div");
    name.classList.add("product-name");
    name.textContent = product.name;

    const description = document.createElement("div");
    description.classList.add("product-description");
    description.textContent = TIER_SUPER_LIKES[product.tier] || product.description || "";

    const price = document.createElement("div");
    price.classList.add("product-price");

    // 업그레이드는 남은 기간의 차액만 내므로 정가와 다르다
    if (product.buyable && product.payAmount !== product.price) {

        price.textContent =
            formatPrice(product.payAmount) + "원 (정가 " + formatPrice(product.price) + "원)";

    } else {
        price.textContent = formatPrice(product.price) + "원 / " + product.durationDays + "일";
    }

    const button = document.createElement("button");
    button.type = "button";
    button.classList.add("product-buy-btn");

    if (product.buyable) {

        button.textContent = "결제하기";

        button.addEventListener("click", function () {
            pay(product, button);
        });

    } else {

        // 못 사는 이유를 버튼에 그대로 보여 준다 (서버도 같은 규칙으로 막는다)
        button.disabled = true;
        button.textContent = product.note;
    }

    card.appendChild(name);
    card.appendChild(description);
    card.appendChild(price);

    // "남은 20일을 플래티넘으로 바꿉니다" 같은 안내
    if (product.buyable && product.note) {

        const note = document.createElement("div");
        note.classList.add("product-note");
        note.textContent = product.note;

        card.appendChild(note);
    }

    card.appendChild(button);

    return card;
}




/* =========================================================
   결제 (금액과 기간은 서버가 상품에서 읽는다)
   ========================================================= */
function pay(product, button) {

    if (typeof PortOne === "undefined" || !window.portoneStoreId || !window.portoneChannelKey) {
        alert("결제 모듈이 설정되지 않았습니다.");
        return;
    }

    button.disabled = true;

    // 화면에 표시된 등급/금액을 믿지 않고 결제 직전에 서버 정책을 다시 조회한다.
    fetch(contextPath + "/api/subscriptions/policy?productId=" + encodeURIComponent(product.productId))
        .then(function (response) {
            return response.json().then(function (data) {
                if (!response.ok) {
                    throw new Error(data.message || "구독 정책을 확인하지 못했습니다.");
                }
                return data;
            });
        })
        .then(function (policy) {
            if (!policy.buyable) {
                throw new Error(policy.note || "현재 구독 상태에서는 구매할 수 없습니다.");
            }

            if (!confirm(product.name + "을(를) " + formatPrice(policy.payAmount) + "원에 결제할까요?")) {
                throw new Error("결제를 취소했습니다.");
            }

            var merchantUid = "mingle-" + userId + "-" + product.productId + "-" + Date.now();
            return { policy: policy, merchantUid: merchantUid };
        })
        .then(function (payment) {
            var policy = payment.policy;
            var merchantUid = payment.merchantUid;
    
            return PortOne.requestPayment({
        storeId: window.portoneStoreId,
        channelKey: window.portoneChannelKey,
        paymentId: merchantUid,
        orderName: product.name,
        totalAmount: policy.payAmount,
        currency: "KRW",
        payMethod: "EASY_PAY",
        customer: { fullName: "mingle 회원" }
            }).then(function (response) {

        if (!response || response.code || !response.paymentId) {
            button.disabled = false;
            alert((response && response.message) || "결제가 취소되었습니다.");
            return;
        }

        // 백엔드 검증 요청
        fetch(contextPath + "/api/subscriptions/pay", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                productId: product.productId,
                paymentId: response.paymentId,
                impUid: response.paymentId,
                merchantUid: merchantUid
            })
        })
        .then(function (result) {
            return result.json()
                .catch(function () { return {}; })
                .then(function (data) {
                    if (!result.ok) {
                        throw new Error(data.message || "결제 검증에 실패했습니다.");
                    }
                    return data;
                });
        })
        .then(function () {
            alert("결제가 완료되었습니다.");
            location.reload();
        })
        .catch(function (error) {
            console.error("결제 검증 오류:", error);
            button.disabled = false;
            alert(error.message);
        });
            }).catch(function (error) {
        console.error("결제창 오류:", error);
        button.disabled = false;
        if (error.message !== "결제를 취소했습니다.") {
            alert(error.message || "결제에 실패했습니다.");
        }
            });
        });
}

/* =========================================================
   결제 내역
   ========================================================= */

function loadHistory() {

    fetchJson("/api/subscriptions/history")
        .then(function (history) {

            const list = document.getElementById("historyList");

            if (history.length === 0) {
                list.textContent = "결제 내역이 없습니다.";
                return;
            }

            history.forEach(function (item) {
                list.appendChild(createHistoryItem(item));
            });
        })
        .catch(function (error) {

            console.error("결제 내역 조회 오류:", error);
        });
}


function createHistoryItem(item) {

    const row = document.createElement("div");
    row.classList.add("history-item");

    const name = document.createElement("div");
    name.classList.add("history-name");
    name.textContent = item.productName;

    const period = document.createElement("div");
    period.classList.add("history-period");
    period.textContent = formatDate(item.startDate) + " ~ " + formatDate(item.endDate);

    const amount = document.createElement("div");
    amount.classList.add("history-amount");
    amount.textContent = formatPrice(item.amount) + "원";

    row.appendChild(name);
    row.appendChild(period);
    row.appendChild(amount);

    return row;
}


/* 1000 단위 쉼표 */
function formatPrice(price) {

    return Number(price).toLocaleString("ko-KR");
}
