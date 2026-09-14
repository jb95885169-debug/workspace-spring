// ==========================================
// 💳 자체 가짜 결제 시스템 & 구독 관리 로직 (모달 기반 업그레이드)
// ==========================================

window.mockPaymentHistory = window.mockPaymentHistory || [];

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
        executeMockPayProcess(tierName, price);
    });
}

// 업그레이드 안내 모달 띄우기 (confirm 대용)
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

// 3. 실제 가짜 결제/승인 창 띄우기
function executeMockPayProcess(tierName, price) {
    const existingModal = document.getElementById('mockPaymentModal');
    if (existingModal) existingModal.remove();

    const merchantUid = 'ORD_' + new Date().getTime();

    const modalHTML = `
        <div id="mockPaymentModal" class="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm">
            <div class="bg-[#18181b] border border-white/10 rounded-2xl w-full max-w-md p-6 text-white shadow-2xl space-y-5">
                <div class="flex justify-between items-center border-b border-white/10 pb-3">
                    <h3 class="text-base font-bold text-[#d4af37]">🛡️ MIRA Mock Secure Pay</h3>
                    <button onclick="closeMockModal()" class="text-gray-400 hover:text-white text-lg">&times;</button>
                </div>
                
                <div class="space-y-3 text-sm text-gray-300">
                    <div class="flex justify-between bg-black/30 p-3 rounded-xl">
                        <span>선택 플랜</span>
                        <span class="font-bold text-white">MIRA ${tierName} (30일)</span>
                    </div>
                    <div class="flex justify-between bg-black/30 p-3 rounded-xl">
                        <span>결제 금액</span>
                        <span class="font-bold text-[#d4af37]">₩ ${price.toLocaleString()}</span>
                    </div>
                </div>

                <div class="flex gap-3 pt-2">
                    <button onclick="handleMockResult('${tierName}', ${price}, '${merchantUid}', true)" 
                        class="flex-1 bg-[#d4af37] text-black py-2.5 rounded-xl font-bold hover:bg-amber-400 transition text-xs">
                        결제 승인 (성공)
                    </button>
                    <button onclick="handleMockResult('${tierName}', ${price}, '${merchantUid}', false)" 
                        class="flex-1 bg-red-500/10 border border-red-500/30 text-red-400 py-2.5 rounded-xl font-bold hover:bg-red-500/20 transition text-xs">
                        결제 취소 (실패)
                    </button>
                </div>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', modalHTML);
}

function closeMockModal() {
    const modal = document.getElementById('mockPaymentModal');
    if (modal) modal.remove();
}

// 4. 결제 승인 결과 처리 및 DB 동기화
function handleMockResult(tierName, price, merchantUid, isSuccess) {
    closeMockModal();

    if (isSuccess) {
        fetch('/subscription/pay/process', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ 
                tier: tierName,
                amount: price,
                merchantUid: merchantUid
            })
        })
        .then(response => {
            if (response.ok) {
                const newPayment = {
                    merchant_uid: merchantUid,
                    tier: tierName,
                    amount: price,
                    pay_method: '카드 결제 (테스트)',
                    paid_at: new Date().toISOString().replace('T', ' ').substring(0, 19),
                    status: '결제 완료'
                };
                window.mockPaymentHistory.unshift(newPayment);

                // 핵심: 사진 속 빨간 네모 영역들을 새 등급으로 즉시 갱신
                applyTierUpdate(tierName, 30);

                showSuccessScreen(tierName, price, merchantUid);
            } else {
                alert('서버 DB 저장 중 오류가 발생했습니다.');
            }
        })
        .catch(error => {
            console.error('통신 에러:', error);
            alert('결제 처리 중 통신 오류가 발생했습니다.');
        });

    } else {
        alert('결제가 취소되었거나 실패하였습니다.');
    }
}

// 5. 성공 화면 팝업
function showSuccessScreen(tierName, price, merchantUid) {
    // 기존 모달들 정리
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

                <button onclick="document.getElementById('successScreenModal').remove();" 
                    class="w-full bg-[#d4af37] text-black py-2.5 rounded-xl font-bold hover:bg-amber-400 transition text-sm">
                    		확인
                </button>
            </div>
        </div>
    `;
    document.body.insertAdjacentHTML('beforeend', successHTML);
}

// 6. 👑 사진 속 빨간 네모 영역들을 한 번에 싹 바꿔주는 핵심 함수
function applyTierUpdate(tier, days) {
    // 1) 상단 우측 헤더의 "현재 등급: 👑 Gold 멤버" 영역
    const currentTierDisplay = document.getElementById('currentTierDisplay');
    if (currentTierDisplay) currentTierDisplay.innerText = `👑 ${tier} 멤버`;

    // 2) 구독 관리 상단 배너의 "👑 Gold 멤버십" 영역
    const bannerTierName = document.getElementById('bannerTierName');
    if (bannerTierName) bannerTierName.innerText = tier;

    // 3) 우측 상자 "남은 구독 기간 D-30일" 영역
    const remainingDaysDisplay = document.getElementById('remainingDaysDisplay');
    if (remainingDaysDisplay) remainingDaysDisplay.innerText = `D-${days}일`;
}

// 7. 결제 내역 모달창 렌더링
function renderPaymentHistory() {
    const container = document.getElementById('paymentHistoryContainer');
    if (!container) return;
    
    container.innerHTML = '';

    if (window.mockPaymentHistory.length === 0) {
        container.innerHTML = `<p class="text-center text-xs text-gray-500 py-8">결제 내역이 존재하지 않습니다.</p>`;
        return;
    }

    window.mockPaymentHistory.forEach(item => {
        const el = document.createElement('div');
        el.className = "bg-black/30 border border-white/10 rounded-xl p-4 text-xs space-y-2 mb-3";
        el.innerHTML = `
            <div class="flex justify-between items-center border-b border-white/10 pb-2">
                <span class="font-bold text-[#d4af37]">${item.tier} 멤버십 구독</span>
                <span class="text-emerald-400 bg-emerald-500/10 px-2 py-0.5 rounded text-[10px]">${item.status}</span>
            </div>
            <div class="grid grid-cols-2 gap-2 text-[11px] text-gray-300 pt-1">
                <div>주문번호: <span class="text-gray-400">${item.merchant_uid}</span></div>
                <div>결제금액: <span class="text-white font-bold">₩ ${item.amount.toLocaleString()}</span></div>
                <div>결제수단: <span class="text-gray-400">${item.pay_method}</span></div>
                <div>결제일시: <span class="text-gray-400">${item.paid_at}</span></div>
            </div>
        `;
        container.appendChild(el);
    });
}
//서버에서 실제 DB 결제 내역을 비동기로 가져와서 모달에 렌더링
function openPaymentHistoryModal() {
    fetch('/subscription/history-list') // 결제 내역을 JSON으로 반환하는 컨트롤러 엔드포인트
    .then(response => response.json())
    .then(data => {
        const container = document.getElementById('paymentHistoryContainer');
        if (!container) return;
        
        container.innerHTML = '';

        if (!data || data.length === 0) {
            container.innerHTML = `<p class="text-center text-xs text-gray-500 py-8">결제 내역이 존재하지 않습니다.</p>`;
            return;
        }

        data.forEach(item => {
            const el = document.createElement('div');
            el.className = "bg-black/30 border border-white/10 rounded-xl p-4 text-xs space-y-2 mb-3";
            el.innerHTML = `
                <div class="flex justify-between items-center border-b border-white/10 pb-2">
                    <span class="font-bold text-[#d4af37]">${item.productName}</span>
                    <span class="text-emerald-400 bg-emerald-500/10 px-2 py-0.5 rounded text-[10px]">${item.status}</span>
                </div>
                <div class="grid grid-cols-2 gap-2 text-[11px] text-gray-300 pt-1">
                    <div>결제금액: <span class="text-white font-bold">₩ ${Number(item.amount).toLocaleString()}</span></div>
                    <div>결제일시: <span class="text-gray-400">${item.paidAt}</span></div>
                    <div class="col-span-2">이용기간: <span class="text-gray-400">${item.startDate} ~ ${item.endDate}</span></div>
                </div>
            `;
            container.appendChild(el);
        });

        // 모달 열기
        const modal = document.getElementById('paymentHistoryModal');
        if (modal) modal.classList.remove('hidden');
    })
    .catch(err => {
        console.error('결제 내역 조회 실패:', err);
        alert('결제 내역을 불러오지 못했습니다.');
    });
}