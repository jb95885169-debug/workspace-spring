// ==========================================
// 💳 MIRA 카카오페이(V1) 연동 및 구독 관리 로직
// ==========================================

// 포트원 V1 가맹점 식별코드 적용
const IMP_STORE_ID = "imp24123478";

// 결제 버튼 클릭 시 정책 검증 함수
function requestPayment(tierName, price) {
    fetch(contextPath + '/subscription/info')
        .then(response => {
            if (!response.ok) throw new Error('구독 정보를 조회하지 못했습니다.');
            return response.json();
        })
        .then(data => {
            const currentTier = data.tier; // 'Basic', 'Gold', 'Platinum' 등
            const remainingDays = data.remainingDays;
            
            // 시나리오 1: 현재 'Gold'인데 또 'Gold'를 구매하려고 할 때
            if (currentTier === 'Gold' && tierName === 'Gold') {
                showSubscriptionNoticeModal('이미 Gold 멤버십을 이용 중입니다.', '동일한 멤버십은 중복 구매할 수 없습니다.');
                return; 
            }
            
            // 시나리오 2: 현재 'Platinum'인데 'Platinum'을 구매하려고 할 때
            if (currentTier === 'Platinum' && tierName === 'Platinum') {
                showSubscriptionNoticeModal('이미 Platinum 멤버십을 이용 중입니다.', '동일한 멤버십은 중복 구매할 수 없습니다.');
                return; 
            }
            
            // 시나리오 3: 현재 'Platinum'인데 하위 등급인 'Gold'를 구매하려고 할 때
            if (currentTier === 'Platinum' && tierName === 'Gold') {
                showSubscriptionNoticeModal('하위 등급으로 변경할 수 없습니다.', '현재 Platinum 멤버십을 이용 중이므로 Gold 멤버십을 구매할 수 없습니다.');
                return; 
            }
            
            // 시나리오 4: 현재 'Gold'인데 상위 등급인 'Platinum'으로 갈 때 (업그레이드 & 차액 결제)
            if (currentTier === 'Gold' && tierName === 'Platinum') {
                const finalPrice = data.finalPrice;       
                const remainingValue = data.remainingValue; 
                
                // ★ 업그레이드 모달창 띄우기 (환산 가치 안내 텍스트 반영 가능)
                showUpgradeConfirmModal(tierName, finalPrice, remainingDays, remainingValue, function() {
                    executeKakaoPayment(tierName, finalPrice);
                });
                return;
            }
            
            // 시나리오 5: 최초 구매 (Basic 상태에서 Gold나 Platinum 살 때)
            showUpgradeConfirmModal(tierName, price, 0, 0, function() {
                executeKakaoPayment(tierName, price);
            });
        })
        .catch(error => {
            console.error('구독 정보 조회 실패:', error);
            alert("구독 정보를 불러오는 중 오류가 발생했습니다.");
        });
}

function showSubscriptionNoticeModal(title, message) {
    const existing = document.getElementById('subscriptionNoticeModal');
    if (existing) existing.remove();

    document.body.insertAdjacentHTML('beforeend', `
        <div id="subscriptionNoticeModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm p-4">
            <div class="bg-[#18181b] border border-white/10 rounded-2xl w-full max-w-md p-6 text-white shadow-2xl space-y-5">
                <div class="flex justify-between items-center border-b border-white/10 pb-3">
                    <h3 class="text-base font-bold text-[#d4af37]">멤버십 안내</h3>
                    <button type="button" data-action="close" class="text-gray-400 hover:text-white text-xl">&times;</button>
                </div>
                <div class="space-y-2">
                    <p class="text-sm font-bold">${title}</p>
                    <p class="text-xs text-gray-400 leading-relaxed">${message}</p>
                </div>
                <button type="button" data-action="close" class="w-full py-2.5 rounded-xl bg-[#d4af37] text-black text-sm font-bold hover:bg-amber-400">확인</button>
            </div>
        </div>`);

    const modal = document.getElementById('subscriptionNoticeModal');
    modal.querySelectorAll('[data-action="close"]').forEach(button => {
        button.addEventListener('click', () => modal.remove());
    });
}

// 3. 기존에 쓰시던 예쁜 커스텀 모달창을 띄우는 함수 연동
function showUpgradeConfirmModal(tierName, price, remainingDays, remainingValue, onConfirm) {
    const existing = document.getElementById('upgradeConfirmModal');
    if (existing) existing.remove();

    const creditMessage = remainingValue > 0
        ? `<p class="text-xs text-gray-400">Gold 잔여 ${remainingDays}일의 이용료 <span class="text-[#d4af37]">₩ ${remainingValue.toLocaleString()}</span>가 차감되었습니다.</p>`
        : '';
    const modalHtml = `
        <div id="upgradeConfirmModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm p-4">
            <div class="bg-[#18181b] border border-white/10 rounded-2xl w-full max-w-md p-6 text-white shadow-2xl space-y-5">
                <div class="flex justify-between items-center border-b border-white/10 pb-3">
                    <h3 class="text-base font-bold text-[#d4af37]">멤버십 결제 확인</h3>
                    <button type="button" data-action="cancel" class="text-gray-400 hover:text-white text-xl">&times;</button>
                </div>
                <div class="space-y-2">
                    <p class="text-sm"><span class="font-bold text-[#d4af37]">${tierName}</span> 멤버십을 결제할까요?</p>
                    ${creditMessage}
                    <p class="text-lg font-bold">결제 금액: ₩ ${price.toLocaleString()}</p>
                </div>
                <div class="flex gap-3">
                    <button type="button" data-action="cancel" class="flex-1 py-2.5 rounded-xl bg-[#2a2a3c] text-sm font-bold">취소</button>
                    <button type="button" data-action="confirm" class="flex-1 py-2.5 rounded-xl bg-[#d4af37] text-black text-sm font-bold hover:bg-amber-400">결제 진행</button>
                </div>
            </div>
        </div>`;

    document.body.insertAdjacentHTML('beforeend', modalHtml);
    const modal = document.getElementById('upgradeConfirmModal');
    const close = () => modal.remove();
    modal.querySelectorAll('[data-action="cancel"]').forEach(button => button.addEventListener('click', close));
    modal.querySelector('[data-action="confirm"]').addEventListener('click', () => {
        close();
        onConfirm();
    });
}

// 4. 포트원 V1 카카오페이 결제창 호출 함수
function executeKakaoPayment(tier, amount) {
    var IMP = window.IMP;
    if (!IMP) {
        alert("포트원 SDK가 로드되지 않았습니다.");
        return;
    }
    
    IMP.init(IMP_STORE_ID);

    var productName = "MIRA " + tier + " 멤버십 (30일)";
    var merchantUid = "m_seq_" + new Date().getTime();

    IMP.request_pay({
        pg: "kakaopay.TC0ONETIME",
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
            .then(response => {
                if (!response.ok) throw new Error('결제 처리 요청에 실패했습니다.');
                return response.text();
            })
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

// 5. 성공 화면 팝업
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

// 6. UI 실시간 등급 갱신 함수
function applyTierUpdate(tier, days) {
    const currentTierDisplay = document.getElementById('currentTierDisplay');
    if (currentTierDisplay) currentTierDisplay.innerText = `👑 ${tier} 멤버`;

    const bannerTierName = document.getElementById('bannerTierName');
    if (bannerTierName) bannerTierName.innerText = tier;

    const remainingDaysDisplay = document.getElementById('remainingDaysDisplay');
    if (remainingDaysDisplay) remainingDaysDisplay.innerText = `D-${days}일`;
}

// 7. 날짜/시간 포맷팅 유틸 함수
function formatDateTime(dateInput) {
 if (!dateInput) return '';
 if (typeof dateInput === 'string' && dateInput.includes('-')) return dateInput; 

 const date = new Date(Number(dateInput));
 if (isNaN(date.getTime())) return '';

 const year = date.getFullYear();
 const month = String(date.getMonth() + 1).padStart(2, '0');
 const day = String(date.getDate()).padStart(2, '0');
 const hours = String(date.getHours()).padStart(2, '0');
 const minutes = String(date.getMinutes()).padStart(2, '0');

 return `${year}-${month}-${day} ${hours}:${minutes}`;
}

// 8. 결제 및 구독 내역 조회 및 모달 렌더링 함수
function renderPaymentHistory() {
 fetch(contextPath + '/subscription/history', {
     method: "GET",
     headers: {
         "Content-Type": "application/json; charset=UTF-8"
     }
 })
 .then(response => response.json())
 .then(data => {
     const container = document.getElementById('paymentHistoryContainer');
     let html = '';

     if (!data || data.length === 0) {
         html = `<div class="text-center text-gray-400 py-8 text-xs">결제 내역이 없습니다.</div>`;
     } else {
         data.forEach(item => {
             const formattedDate = formatDateTime(item.paidAt);

             html += `
                 <div class="bg-[#1c1c28] p-4 rounded-2xl border border-white/5 space-y-2">
                     <div class="flex justify-between items-center">
                         <span class="font-bold text-[#d4af37] text-sm">${item.productName}</span>
                         <span class="text-xs px-2.5 py-1 bg-amber-500/20 text-amber-400 rounded-lg font-bold">${item.status}</span>
                     </div>
                     <div class="text-xs text-gray-400">결제일시: ${formattedDate}</div>
                     <div class="text-sm font-bold text-white">₩ ${item.amount.toLocaleString()}</div>
                 </div>
             `;
         });
     }

     if (container) container.innerHTML = html;

     const modal = document.getElementById('paymentHistoryModal');
     if (modal) {
         modal.classList.remove('hidden');
     }
 })
 .catch(error => {
     console.error("결제 내역 조회 실패:", error);
     alert("결제 내역을 불러오는 중 오류가 발생했습니다.");
 });
}
