// ==========================================
// 💳 MIRA 카카오페이(V1) 연동 및 구독 관리 로직
// ==========================================

// 포트원 V1 가맹점 식별코드 적용
const IMP_STORE_ID = "imp24123478";

// 1. Basic 무료 등급으로 변경
function changeToBasic() {
    showUpgradeConfirmModal('Basic', 0, function() {
        applyTierUpdate('Basic', 0);
        alert('Basic 무료 등급으로 전환되었습니다.');
    });
}

// 2. 결제 버튼 클릭 시 안내 모달 먼저 띄우기
function requestPayment(tierName, price) {
    showUpgradeConfirmModal(tierName, price, function() {
        executeKakaoPayment(tierName, price);
    });
}

// 업그레이드 안내 모달 띄우기
function showUpgradeConfirmModal(tierName, price, onConfirm) {
    const existing = document.getElementById('upgradeConfirmModal');
    if (existing) existing.remove();

    const isBasic = tierName === 'Basic';
    const titleText = isBasic ? 'Basic 무료 등급으로 변경하시겠습니까?' : `MIRA ${tierName} 멤버십으로 업그레이드하시겠습니까?`;
    const descText = isBasic 
        ? '무료 플랜으로 전환되며 프리미엄 혜택이 해제됩니다.' 
        : `결제 즉시 상위 등급인 <span class="text-[#d4af37] font-bold">${tierName}</span>으로 전환되며, 이용 기간이 30일로 새롭게 갱신됩니다.`;

    const modalHTML = `
        <div id="upgradeConfirmModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm">
            <div class="bg-[#18181b] border border-white/10 rounded-2xl w-full max-w-md p-6 text-white shadow-2xl space-y-5">
                <div class="flex justify-between items-center border-b border-white/10 pb-3">
                    <h3 class="text-base font-bold text-[#d4af37]">✨ 멤버십 변경 안내</h3>
                    <button onclick="document.getElementById('upgradeConfirmModal').remove()" class="text-gray-400 hover:text-white text-lg">&times;</button>
                </div>
                
                <div class="space-y-2 text-sm text-gray-300">
                    <p class="font-bold text-white">${titleText}</p>
                    <p class="text-xs text-gray-400 leading-relaxed">${descText}</p>
                </div>

                <div class="flex gap-3 pt-2">
                    <button id="confirmYesBtn" class="flex-1 bg-[#d4af37] text-black py-2.5 rounded-xl font-bold hover:bg-amber-400 transition text-xs">
                        확인
                    </button>
                    <button onclick="document.getElementById('upgradeConfirmModal').remove()" class="flex-1 bg-white/10 text-white py-2.5 rounded-xl font-bold hover:bg-white/20 transition text-xs">
                        취소
                    </button>
                </div>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', modalHTML);
    document.getElementById('confirmYesBtn').onclick = function() {
        document.getElementById('upgradeConfirmModal').remove(); 
        onConfirm(); 
    };
}

// 3. 포트원 V1 카카오페이 결제창 호출 함수
function executeKakaoPayment(tier, amount) {
    var IMP = window.IMP;
    if (!IMP) {
        alert("포트원 SDK가 로드되지 않았습니다.");
        return;
    }
    
    // 가맹점 식별코드로 초기화
    IMP.init(IMP_STORE_ID);

    var productName = "MIRA " + tier + " 멤버십 (30일)";
    var merchantUid = "m_seq_" + new Date().getTime();

    IMP.request_pay({
        pg: "kakaopay.TC0ONETIME", // 카카오페이 테스트 PG 설정
        pay_method: "CARD",
        merchant_uid: merchantUid,
        name: productName,
        amount: amount,
        buyer_email: "user1@test.com",
        buyer_name: "테스트유저",
        buyer_tel: "010-1234-5678"
    }, function (rsp) {
    	if (rsp.success) {
            var paymentUniqueId = rsp.paymentId || rsp.imp_uid; 

            // 서버로 결제 성공 결과 전송 및 DB 반영
            fetch(contextPath + '/subscription/pay/process', {
                method: "POST",
                headers: {
                    "Content-Type": "application/json; charset=UTF-8"
                },
                body: JSON.stringify({
                    tier: tier,
                    amount: rsp.paid_amount,
                    merchantUid: rsp.merchant_uid,
                    impUid: paymentUniqueId,
                    payMethod: rsp.pay_method || 'CARD'
                })
            })
            .then(response => response.text()) // 서버가 보내주는 "success" 문자열을 텍스트로 읽습니다.
            .then(result => {
                if (result === "success") {
                    applyTierUpdate(tier, 30);
                    showSuccessScreen(tier, rsp.paid_amount, rsp.merchant_uid);
                } else {
                    alert("결제는 성공했으나 서버 처리 중 오류가 발생했습니다.");
                }
            })
            .catch(error => {
                console.error("통신 에러:", error);
                alert("서버 통신 중 오류가 발생했습니다.");
            });
        }
    });
}

// 4. 성공 화면 팝업
function showSuccessScreen(tierName, price, merchantUid) {
    const existingSuccess = document.getElementById('successScreenModal');
    if (existingSuccess) existingSuccess.remove();

    const successHTML = `
        <div id="successScreenModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm">
            <div class="bg-[#18181b] border border-white/10 rounded-2xl w-full max-w-sm p-6 text-white shadow-2xl text-center space-y-4">
                <div class="w-16 h-16 bg-emerald-500/20 text-emerald-400 rounded-full flex items-center justify-center mx-auto text-2xl font-bold">
                    ✓
                </div>
                <h3 class="text-lg font-bold text-white">결제 및 구독 완료!</h3>
                <p class="text-xs text-gray-400">성공적으로 결제되었으며, 30일 동안 <span class="text-[#d4af37] font-bold">${tierName}</span> 혜택이 적용됩니다.</p>
                
                <div class="bg-black/30 p-3 rounded-xl text-left text-xs space-y-1 text-gray-300">
                    <div>주문번호: <span class="text-gray-400">${merchantUid}</span></div>
                    <div>결제금액: <span class="text-white font-bold">₩ ${price.toLocaleString()}</span></div>
                </div>

                <button onclick="document.getElementById('successScreenModal').remove(); location.reload();" 
                    class="w-full bg-[#d4af37] text-black py-2.5 rounded-xl font-bold hover:bg-amber-400 transition text-sm">
                    확인
                </button>
            </div>
        </div>
    `;
    document.body.insertAdjacentHTML('beforeend', successHTML);
}

// 5. UI 실시간 등급 갱신 함수
function applyTierUpdate(tier, days) {
    const currentTierDisplay = document.getElementById('currentTierDisplay');
    if (currentTierDisplay) currentTierDisplay.innerText = `👑 ${tier} 멤버`;

    const bannerTierName = document.getElementById('bannerTierName');
    if (bannerTierName) bannerTierName.innerText = tier;

    const remainingDaysDisplay = document.getElementById('remainingDaysDisplay');
    if (remainingDaysDisplay) remainingDaysDisplay.innerText = `D-${days}일`;
}

// 6. 결제 내역 렌더링 함수
function renderPaymentHistory() {
    openPaymentHistoryModal();
}