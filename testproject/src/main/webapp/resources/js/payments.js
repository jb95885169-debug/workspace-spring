// ==========================================
// 🎧 고객 문의 및 구독/결제 통합 로직
// ==========================================

let userTier = 'Gold';
let currentMainTask = 'discovery';

let mockPaymentHistory = [
    {
        merchant_uid: 'ORD20260908-0012',
        tier: 'Gold',
        amount: 19900,
        pay_method: 'kakaopay',
        paid_at: '2026-09-08 14:18:22',
        status: 'PAID'
    }
];

//페이지 로드 시 포트원 SDK 초기화 및 서버 구독/결제 정보 자동 불러오기
window.addEventListener('DOMContentLoaded', () => {
    if (window.IMP) {
        IMP.init("imp00000000"); // 포트원 가맹점 식별코드 (테스트용)
    }
    
    if (typeof loadSubscriptionStatus === 'function') {
        loadSubscriptionStatus();
    }
});

// 화면 전환 관련
function switchTab(tab) {
    currentMainTask = tab;
    const views = { 'discovery': 'viewDiscovery', 'subscription': 'viewSubscription', 'feed': 'viewFeed', 'likes': 'viewLikes' };
    const navs = { 'discovery': 'navDiscovery', 'subscription': 'navSubscription', 'feed': 'navFeed', 'likes': 'navLikes' };

    Object.keys(views).forEach(key => {
        const el = document.getElementById(views[key]);
        const nav = document.getElementById(navs[key]);
        if (key === tab) {
            if(el) el.classList.remove('hidden');
            if(nav) nav.className = "text-[#d4af37] font-semibold transition pb-1 border-b-2 border-[#d4af37]";
            
            // 구독 탭으로 진입할 때 구독 정보 갱신
            if (tab === 'subscription') {
                loadSubscriptionStatus();
            }
        } else {
            if(el) el.classList.add('hidden');
            if(nav) nav.className = "hover:text-[#d4af37] transition pb-1 border-b-2 border-transparent";
        }
    });
}

function openModal(id) { document.getElementById(id).classList.remove('hidden'); }
function closeModal(id) { document.getElementById(id).classList.add('hidden'); }
function toggleProfilePopup(e) { e.stopPropagation(); document.getElementById('profilePopup').classList.toggle('hidden'); }

// 고객 문의 모달 열기
function openSupportModal() {
    openModal('supportModal');
    switchSupportSubTab('write');
}

function switchSupportSubTab(tab) {
    const tabWrite = document.getElementById('supportTabWrite');
    const tabList = document.getElementById('supportTabList');
    const btnWrite = document.getElementById('btnSupportWrite');
    const btnList = document.getElementById('btnSupportList');

    if (tab === 'write') {
        tabWrite.classList.remove('hidden');
        tabList.classList.add('hidden');
        btnWrite.className = "flex-1 py-2 rounded-xl bg-[#d4af37] text-black font-bold text-xs transition";
        btnList.className = "flex-1 py-2 rounded-xl bg-transparent text-gray-400 hover:text-white font-bold text-xs transition";
    } else {
        tabWrite.classList.add('hidden');
        tabList.classList.remove('hidden');
        btnList.className = "flex-1 py-2 rounded-xl bg-[#d4af37] text-black font-bold text-xs transition";
        btnWrite.className = "flex-1 py-2 rounded-xl bg-transparent text-gray-400 hover:text-white font-bold text-xs transition";
    
        // 목록 탭을 누를 때 DB 데이터를 가져옴 (contextPath 반영)
        loadTicketList();
    }
}

function submitSupportTicket() {
    const category = document.getElementById('supportCategory').value;
    const title = document.getElementById('supportTitle').value; 
    const content = document.getElementById('supportContent').value;

    if (!title.trim()) {
        alert('문의 제목을 입력해주세요.');
        return;
    }
    if (!content.trim()) {
        alert('문의 내용을 입력해주세요.');
        return;
    }

    fetch(contextPath + '/support/write', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            category: category,
            title: title,   
            content: content
        })
    })
    .then(response => {
        if (response.ok) {
            alert('문의가 성공적으로 접수되었습니다.');
            document.getElementById('supportTitle').value = '';
            document.getElementById('supportContent').value = '';
            switchSupportSubTab('list');
        } else {
            alert('문의 접수 실패');
        }
    })
    .catch(err => console.error('에러:', err));
}

// 밀리초 숫자를 'YYYY-MM-DD HH:mm' 형태의 문자열로 바꿔주는 함수
function formatDateTime(dateInput) {
    if (!dateInput) return '';
    if (typeof dateInput === 'string') return dateInput; 

    const date = new Date(dateInput);
    if (isNaN(date.getTime())) return '';

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');

    return `${year}-${month}-${day} ${hours}:${minutes}`;
}

// 1. 서버에서 목록을 조회해서 화면에 그려주는 함수 (contextPath 적용)
function loadTicketList() {
    fetch(contextPath + '/support/list')
    .then(response => response.json())
    .then(data => {
        console.log("서버에서 받아온 진짜 데이터:", data); 
        renderSupportList(data);
    })
    .catch(error => console.error('목록 불러오기 실패:', error));
}

// 2. 받아온 데이터를 기반으로 화면에 HTML 카드를 그려주는 함수
function renderSupportList(data) {
    const container = document.getElementById('supportListContainer'); 
    if (!container) {
        console.error("카드가 들어갈 컨테이너를 찾지 못함");
        return;
    }
    
    container.innerHTML = ''; 

    if (!data || data.length === 0) {
        container.innerHTML = '<p class="text-center text-xs text-gray-400 py-6">등록된 문의 내역이 없습니다.</p>';
        return;
    }

    data.forEach(ticket => {
        const isPending = ticket.status === 'PENDING';
        const statusClass = isPending ? 'bg-amber-500/10 text-amber-400' : 'bg-emerald-500/10 text-emerald-400';
        const statusText = isPending ? '처리 중' : '답변 완료';

        const adminButton = isPending ? `
            <button onclick="simulateAdminReply(${ticket.id})" class="mt-3 text-xs bg-[#d4af37] text-black px-3 py-1.5 rounded-lg font-semibold hover:bg-amber-400 transition">
                💬 [관리자] 답변 완료 처리하기
            </button>
        ` : '';

        const cardHTML = `
            <div class="bg-[#18181b] border border-white/10 rounded-2xl p-5 mb-4 text-white text-xs">
                <div class="flex justify-between items-center mb-2">
                    <span class="font-bold text-sm text-[#d4af37]">[${ticket.category}] ${ticket.title || ''}</span>
                    <div class="flex items-center gap-2">
                        <span class="text-[10px] px-2.5 py-1 rounded-full ${statusClass}">${statusText}</span>
                        <span class="text-[10px] text-gray-400">${formatDateTime(ticket.createdAt)}</span>
                    </div>
                </div>
                
                <p class="text-xs text-gray-200 mb-2 leading-relaxed">${ticket.content}</p>

                ${!isPending ? `
                    <div class="mt-3 p-3 bg-black/30 border border-white/5 rounded-xl text-xs text-gray-300">
                        <div class="flex justify-between items-center mb-1">
                            <span class="text-[#d4af37] font-bold">💬 관리자 답변</span>
                            <span class="text-[10px] text-gray-400">${formatDateTime(ticket.updatedAt)}</span>
                        </div>
                        안녕하세요 MIRA 지원팀입니다. 문의주신 내용이 정상적으로 처리되었습니다.
                    </div>
                ` : ''}
                
                ${adminButton}
            </div>
        `;
        container.innerHTML += cardHTML;
    });
}

// 관리자 답변 시뮬레이션 함수 (contextPath 적용)
function simulateAdminReply(ticketId) {
    fetch(contextPath + '/support/updateStatus', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ id: ticketId })
    })
    .then(response => {
        if (response.ok) {
            alert('관리자 답변이 등록되며 답변 완료로 변경되었습니다.');
            loadTicketList(); 
        } else {
            alert('처리 중 오류가 발생했습니다.');
        }
    })
    .catch(error => console.error('에러:', error));
}

// 구독 정보 및 결제 이력 로드 (contextPath 적용)
function loadSubscriptionStatus() {
    fetch(contextPath + '/subscription/info')
    .then(res => res.json())
    .then(data => {
        const sub = data.subscription;
        const payments = data.paymentList;

        const topBadge = document.getElementById('navUserTierBadge'); 
        if(topBadge && sub) {
            topBadge.innerText = `${sub.tier || 'Basic'} 멤버십`;
        }

        const currentTierEl = document.getElementById('currentTierName');
        const expireDateEl = document.getElementById('expireDateText');
        const dDayEl = document.getElementById('remainingDDay');

        if (sub) {
            let tierName = sub.productId == 2 ? 'Platinum 멤버십' : 'Gold 멤버십';
            if(currentTierEl) currentTierEl.innerText = tierName;
            
            if(sub.endDate) {
                let endDate = new Date(sub.endDate);
                let today = new Date();
                let diffTime = endDate - today;
                let diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
                
                if(expireDateEl) expireDateEl.innerText = `자동 결제 예정일: ${formatDate(sub.endDate)}`;
                if(dDayEl) dDayEl.innerText = diffDays > 0 ? `D-${diffDays}` : '만료됨';
            }
        } else {
            if(currentTierEl) currentTierEl.innerText = 'Basic 멤버십';
            if(expireDateEl) expireDateEl.innerText = '무료 이용 중';
            if(dDayEl) dDayEl.innerText = 'Free';
        }

        window.cachedPaymentHistory = payments;
    })
    .catch(err => console.error('구독 정보 로드 실패:', err));
}

//'결제 이력 보기' 또는 프로필의 '결제 내역' 클릭 시 호출
function openPaymentHistoryModal() {
    openModal('paymentHistoryModal');
    const container = document.getElementById('paymentHistoryContainer');
    if (!container) return;
    
    container.innerHTML = '';

    const payments = window.cachedPayDOMContentLoadedmentHistory || [];

    if (payments.length === 0) {
        container.innerHTML = `<p class="text-center text-xs text-gray-500 py-8">결제 내역이 존재하지 않습니다.</p>`;
        return;
    }

    payments.forEach(pay => {
        let payDate = pay.paidAt ? pay.paidAt : '';
        const el = document.createElement('div');
        el.className = "bg-[#0b0b10] border border-[#262638] rounded-xl p-4 text-xs space-y-1.5";
        el.innerHTML = `
            <div class="flex justify-between items-center">
                <span class="text-[#d4af37] font-bold">${pay.productName || '멤버십 구독'}</span>
                <span class="text-emerald-400 font-bold">₩ ${pay.amount ? pay.amount.toLocaleString() : 0}</span>
            </div>
            <div class="flex justify-between text-gray-400 text-[10px]">
                <span>결제일시: ${payDate}</span>
                <span class="bg-amber-500/10 text-amber-400 px-2 py-0.5 rounded">${pay.status || 'PAID'}</span>
            </div>
        `;
        container.appendChild(el);
    });
}

// 날짜 포맷 헬퍼
function formatDate(dateInput) {
    if (!dateInput) return '';
    const date = new Date(dateInput);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
}