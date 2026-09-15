<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MIRA - Premium Dating & Service Dashboard</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

    <script>
        const contextPath = "${pageContext.request.contextPath}";
    </script>
    
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://cdn.iamport.kr/v1/iamport.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/common.css">

</head>
<body class="h-screen w-screen overflow-hidden flex flex-col">

<!-- 상단 메뉴창 -->
<header class="h-16 bg-[#121218] border-b border-[#1f1f2e] flex items-center justify-between px-6 z-30 shadow-lg shrink-0">
    <div class="flex items-center space-x-3">
        <span class="text-2xl font-serif font-bold text-[#d4af37] tracking-wider">MIRA</span>
        <span class="text-xs px-2 py-0.5 rounded bg-[#d4af37]/20 text-[#d4af37] font-semibold">PREMIUM</span>
    </div>
    
    <div class="flex items-center space-x-8 text-sm font-medium text-gray-300">
        <button onclick="switchTab('discovery')" id="navDiscovery" class="text-[#d4af37] font-semibold transition pb-1 border-b-2 border-[#d4af37]">디스커버리</button>
        <button onclick="switchTab('feed')" id="navFeed" class="hover:text-[#d4af37] transition pb-1 border-b-2 border-transparent">피드</button>
        <button onclick="switchTab('likes')" id="navLikes" class="hover:text-[#d4af37] transition pb-1 border-b-2 border-transparent">좋아요 내역</button>
        <button onclick="switchTab('subscription')" id="navSubscription" class="hover:text-[#d4af37] transition pb-1 border-b-2 border-transparent">구독 & 결제</button>
    </div>

    <div class="flex items-center space-x-4">
        <!-- 구독 등급 노출 -->
        <div class="bg-[#1b1b26] border border-[#d4af37]/40 px-3.5 py-1.5 rounded-full flex items-center space-x-2 shadow-md cursor-pointer" onclick="switchTab('subscription')">
            <span class="text-[11px] text-gray-400">현재 등급:</span>
            <span class="text-xs font-bold text-[#d4af37]" id="currentTierDisplay">👑 Gold 멤버</span>
        </div>
        
        <div class="relative">
            <button onclick="toggleProfilePopup(event)" class="flex items-center space-x-2 bg-[#1b1b26] hover:bg-[#252536] border border-[#d4af37]/40 px-3.5 py-1.5 rounded-full transition shadow-md">
                <div class="w-5 h-5 rounded-full bg-gradient-to-tr from-[#d4af37] to-amber-200 flex items-center justify-center text-[9px] font-bold text-black">내</div>
                <span class="text-xs font-semibold text-white">프로필</span>
            </button>

            <div id="profilePopup" class="absolute right-0 top-12 w-48 bg-[#16161f] border border-[#262638] rounded-2xl shadow-2xl py-2 hidden z-40">
                <div onclick="openModal('myInfoModal'); document.getElementById('profilePopup').classList.add('hidden');" class="px-4 py-2.5 text-xs text-gray-300 hover:bg-[#20202e] hover:text-[#d4af37] cursor-pointer transition">👤 내 정보 관리</div>
                <div onclick="switchTab('subscription'); document.getElementById('profilePopup').classList.add('hidden');" class="px-4 py-2.5 text-xs text-gray-300 hover:bg-[#20202e] hover:text-[#d4af37] cursor-pointer transition">💳 구독 관리</div>
				<div onclick="openModal('paymentHistoryModal'); renderPaymentHistory(); document.getElementById('profilePopup').classList.add('hidden');" class="px-4 py-2.5 text-xs text-gray-300 hover:bg-[#20202e] hover:text-[#d4af37] cursor-pointer transition">📜 결제 내역</div>
                <div class="border-t border-[#262638] my-1"></div>
                <div onclick="alert('로그아웃 되었습니다.'); document.getElementById('profilePopup').classList.add('hidden');" class="px-4 py-2.5 text-xs text-rose-400 hover:bg-rose-500/10 cursor-pointer transition">🚪 로그아웃</div>
            </div>
        </div>
    </div>
</header>

<!-- 메인 컨테이너 영역 -->
<div class="flex-1 flex overflow-hidden relative">

    <!-- 좌측 1: 메뉴 및 고객문의/알림 이모지 사이드바 -->
    <aside class="w-20 bg-[#0e0e13] border-r border-[#1f1f2e] flex flex-col justify-between items-center py-6 z-20 shrink-0">
        <div class="flex flex-col items-center space-y-4"></div>
        <div class="flex flex-col items-center space-y-4 relative">
            <!-- 🎧 고객 문의 아이콘 -->
            <button onclick="openSupportModal()" class="w-10 h-10 rounded-xl bg-[#16161f] hover:bg-[#20202e] flex items-center justify-center text-lg shadow-md transition relative" title="1:1 고객 문의">
                🎧
                <span id="supportBadge" class="absolute top-2 right-2 w-2 h-2 bg-amber-500 rounded-full hidden"></span>
            </button>
            
            <!-- 🔔 알림 팝업 오픈 버튼 -->
            <button onclick="openModal('notificationModal')" class="w-10 h-10 rounded-xl bg-[#16161f] hover:bg-[#20202e] flex items-center justify-center text-lg shadow-md transition relative" title="공지사항 및 최근 알림">
                🔔
                <span class="absolute top-2 right-2 w-2 h-2 bg-rose-500 rounded-full"></span>
            </button>
        </div>
    </aside>

    <!-- 좌측 2: 최근 채팅 내역 사이드바 -->
    <aside class="w-80 bg-[#0e0e13] border-r border-[#1f1f2e] flex flex-col p-4 z-20 shrink-0">
        <div class="flex items-center justify-between mb-4">
            <h2 class="text-xs font-semibold text-gray-400 tracking-wider uppercase">최근 채팅 및 매칭내역</h2>
            <span class="text-xs bg-[#d4af37]/20 text-[#d4af37] px-2 py-0.5 rounded-full font-bold">2</span>
        </div>
        
        <div class="flex-1 overflow-y-auto space-y-3 pr-1">
            <div class="flex items-center space-x-3 p-3 rounded-xl bg-[#16161f] hover:bg-[#1f1f2e] cursor-pointer transition border border-transparent hover:border-[#d4af37]/30">
                <div class="relative">
                    <div class="w-12 h-12 rounded-full bg-cover bg-center border border-[#d4af37]/40" style="background-image: url('https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=300&q=80')"></div>
                    <span class="absolute bottom-0 right-0 w-3 h-3 bg-emerald-500 border-2 border-[#16161f] rounded-full"></span>
                </div>
                <div class="flex-1 min-w-0">
                    <div class="flex justify-between items-baseline mb-1">
                        <h3 class="text-sm font-semibold truncate text-white">Sophia</h3>
                        <span class="text-[10px] text-gray-500">방금 전</span>
                    </div>
                    <p class="text-xs text-gray-400 truncate">안녕하세요! 반가워요 ✨</p>
                </div>
            </div>
        </div>
    </aside>

    <!-- 중앙 메인 컨텐츠 영역 -->
    <main class="flex-1 bg-[#0b0b10] flex flex-col relative overflow-hidden">
        
        <!-- 탭 1: 디스커버리 -->
        <div id="viewDiscovery" class="flex-1 flex flex-col items-center justify-center p-6">
            <div class="w-full max-w-md h-[440px] bg-[#16161f] rounded-3xl shadow-2xl relative overflow-hidden border border-[#262638] card-transition flex flex-col justify-end p-6 cursor-pointer" id="profileCard">
                <div id="cardImage" class="absolute inset-0 bg-cover bg-center" style="background-image: url('https://images.unsplash.com/photo-1524504388940-b1c1722653e1?auto=format&fit=crop&w=600&q=80');"></div>
                <div class="absolute inset-0 bg-gradient-to-t from-black/95 via-black/30 to-transparent flex flex-col justify-end p-6">
                    <h2 id="cardNameAge" class="text-2xl font-bold font-serif text-white mb-1">Elena, 25</h2>
                    <p id="cardJobRegion" class="text-sm text-gray-300 mb-3">Software Engineer · 서울 마포구</p>
                </div>
            </div>
        </div>

        <!-- 탭 2: 구독 & 결제 관리 (담당 화면 메인) -->
        <div id="viewSubscription" class="flex-1 overflow-y-auto p-8 hidden">
            <div class="max-w-5xl mx-auto space-y-10 pb-12">
                <!-- 현재 구독 상단 배너 -->
                <div class="bg-gradient-to-r from-[#16161f] to-[#1f1f2e] border border-[#d4af37]/40 rounded-3xl p-6 shadow-xl flex justify-between items-center">
                    <div>
                        <span class="text-xs text-[#d4af37] font-bold tracking-wider uppercase">현재 이용 중인 멤버십</span>
                        <h2 class="text-2xl font-serif font-bold text-white mt-1">👑 <span id="bannerTierName">Gold</span> 멤버십</h2>
                        <p class="text-xs text-gray-400 mt-1">자동 결제 예정일: <span class="text-gray-200">2026-10-10</span></p>
                    </div>
                    <div class="flex items-center space-x-3">
                        <button onclick="openModal('paymentHistoryModal'); renderPaymentHistory();" class="px-4 py-2.5 bg-[#121218] hover:bg-[#20202e] border border-[#262638] text-xs text-gray-300 font-semibold rounded-xl transition">📜 결제 이력 보기</button>
                        <div class="bg-[#0b0b10] border border-[#d4af37]/30 px-6 py-3 rounded-2xl text-center shadow-inner">
                            <span class="text-xs text-gray-400 block">남은 구독 기간</span>
                            <span class="text-xl font-bold text-[#d4af37]" id="remainingDaysDisplay">D-30일</span>
                        </div>
                    </div>
                </div>

                <div class="text-center space-y-2 mb-6">
                    <h1 class="text-3xl font-serif font-bold text-[#d4af37]">MIRA Membership 플랜</h1>
                    <p class="text-sm text-gray-400">원하시는 플랜을 선택하고 PG 결제를 테스트해보세요.</p>
                </div>

                <!-- 멤버십 플랜 카드 3종 -->
                <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                    <!-- BASIC -->
                    <div class="bg-[#16161f] border border-[#262638] rounded-3xl p-8 flex flex-col relative">
                        <h3 class="text-xl font-bold text-white mb-2">Basic</h3>
                        <p class="text-xs text-gray-400 mb-6 border-b border-[#262638] pb-4">기본 무료 탐색 서비스</p>
                        <ul class="space-y-3 text-xs text-gray-300 flex-1 mb-8">
                            <li class="flex items-center space-x-2"><span>✔️</span> <span>매일 10명 프로필 탐색</span></li>
                            <li class="flex items-center space-x-2"><span>✔️</span> <span>기본 좋아요 발송</span></li>
                        </ul>
                        <button onclick="changeToBasic()" class="w-full py-3 rounded-xl bg-[#1f1f2e] hover:bg-[#2a2a3c] text-white font-bold text-sm transition">Basic 무료 전환</button>
                    </div>

                    <!-- GOLD -->
                    <div class="bg-[#1b1b26] border border-[#d4af37] rounded-3xl p-8 flex flex-col relative transform scale-105 shadow-[0_0_20px_rgba(212,175,55,0.15)]">
                        <div class="absolute -top-4 left-1/2 transform -translate-x-1/2 bg-[#d4af37] text-black text-[10px] font-bold px-4 py-1 rounded-full">인기 추천</div>
                        <h3 class="text-2xl font-bold text-[#d4af37] mb-2">Gold</h3>
                        <p class="text-xs text-gray-400 mb-6 border-b border-[#262638] pb-4">적극적인 매칭을 위한 필수 멤버십</p>
                        <ul class="space-y-3 text-xs text-gray-300 flex-1 mb-8">
                            <li class="flex items-center space-x-2"><span>✔️</span> <span>매일 30명 프로필 탐색</span></li>
                            <li class="flex items-center space-x-2"><span>✔️</span> <span class="text-[#d4af37] font-bold">매일 슈퍼 좋아요 5회</span></li>
                            <li class="flex items-center space-x-2"><span>✔️</span> <span class="text-[#d4af37] font-bold">주 1회 프로필 상단 노출</span></li>
                        </ul>
                        <button onclick="requestPayment('Gold', 19900)" class="w-full py-3 rounded-xl bg-[#d4af37] hover:bg-amber-400 text-black font-bold text-sm transition shadow-lg">₩ 19,900 / 30일 결제</button>
                    </div>

                    <!-- PLATINUM -->
                    <div class="bg-[#16161f] border border-[#262638] rounded-3xl p-8 flex flex-col relative">
                        <h3 class="text-xl font-bold text-white mb-2">Platinum</h3>
                        <p class="text-xs text-gray-400 mb-6 border-b border-[#262638] pb-4">모든 프리미엄 혜택 및 선조회</p>
                        <ul class="space-y-3 text-xs text-gray-300 flex-1 mb-8">
                            <li class="flex items-center space-x-2"><span>✔️</span> <span>Gold 혜택 전체 포함</span></li>
                            <li class="flex items-center space-x-2"><span>✔️</span> <span class="text-purple-400 font-bold">나를 좋아요한 사람 즉시 공개</span></li>
                            <li class="flex items-center space-x-2"><span>✔️</span> <span class="text-purple-400 font-bold">무제한 매칭 리와인드</span></li>
                        </ul>
                        <button onclick="requestPayment('Platinum', 39900)" class="w-full py-3 rounded-xl bg-[#1f1f2e] text-white hover:bg-[#2a2a3c] font-bold text-sm transition border border-[#262638]">₩ 39,900 / 30일 결제</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- 기타 피드/좋아요 탭 -->
        <div id="viewFeed" class="flex-1 p-8 hidden"><h1 class="text-xl text-white">피드 영역</h1></div>
        <div id="viewLikes" class="flex-1 p-8 hidden"><h1 class="text-xl text-white">좋아요 내역 영역</h1></div>
    </main>
</div>

	<!-- ================================================================= -->
	<!-- 🎧 1:1 고객 문의 모달 (담당 기능 1) -->
	<!-- ================================================================= -->
	<div id="supportModal" class="fixed inset-0 bg-black/75 backdrop-blur-sm flex items-center justify-center hidden z-50">
	    <div class="bg-[#16161f] border border-[#262638] w-full max-w-xl rounded-3xl p-6 shadow-2xl relative space-y-4">
	        <div class="flex justify-between items-center border-b border-[#262638] pb-3">
	            <h3 class="text-lg font-serif font-bold text-white flex items-center space-x-2">
	                <span>🎧</span> <span>1:1 고객 문의 센터</span>
	            </h3>
	            <button onclick="closeModal('supportModal')" class="text-gray-400 hover:text-white font-bold">✕</button>
	        </div>
	
	        <div class="flex space-x-2 bg-[#0b0b10] p-1 rounded-2xl border border-[#262638]">
	            <button onclick="switchSupportSubTab('write')" id="btnSupportWrite" class="flex-1 py-2 rounded-xl bg-[#d4af37] text-black font-bold text-xs transition">✍️ 새 문의 등록</button>
	            <button onclick="switchSupportSubTab('list')" id="btnSupportList" class="flex-1 py-2 rounded-xl bg-transparent text-gray-400 hover:text-white font-bold text-xs transition">📋 나의 문의 내역</button>
	        </div>
	
	        <div id="supportTabWrite" class="space-y-4 pt-1">
	    <div>
	        <label class="block text-xs text-gray-400 mb-1">문의 유형 카테고리</label>
	        <select id="supportCategory" class="w-full bg-[#0b0b10] border border-[#262638] rounded-xl px-3 py-2.5 text-xs text-white focus:outline-none focus:border-[#d4af37]">
	            <option value="이용문의">이용 문의</option>
	            <option value="결제/구독">결제 및 구독 문의</option>
	            <option value="신고/버그">버그 제보 및 사용자 신고</option>
	            <option value="기타">기타 문의</option>
	        </select>
	    </div>
	    
	    <!-- 추가된 부분 문의 제목 입력란 -->
	    <div>
	        <label class="block text-xs text-gray-400 mb-1">문의 제목</label>
	        <input type="text" id="supportTitle" placeholder="제목을 입력해주세요." class="w-full bg-[#0b0b10] border border-[#262638] rounded-xl px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-[#d4af37]">
	    </div>
	
	    <div>
	        <label class="block text-xs text-gray-400 mb-1">문의 내용</label>
	        <textarea id="supportContent" rows="4" placeholder="불편하신 점이나 문의 내용을 상세히 적어주시면 빠르게 답변해 드리겠습니다." class="w-full bg-[#0b0b10] border border-[#262638] rounded-xl p-3.5 text-xs text-white focus:outline-none focus:border-[#d4af37] resize-none"></textarea>
	    </div>
	    <button onclick="submitSupportTicket()" class="w-full py-3 bg-[#d4af37] hover:bg-amber-400 transition text-black font-bold rounded-xl text-xs shadow-lg">문의 접수하기</button>
	</div>

        <div id="supportTabList" class="space-y-3 max-h-80 overflow-y-auto pr-1 hidden">
            <div id="supportListContainer" class="space-y-3"></div>
        </div>
    </div>
</div>

<!-- ================================================================= -->
<!-- 📜 결제 내역 확인 모달 (담당 기능 2) -->
<!-- ================================================================= -->
<div id="paymentHistoryModal" class="fixed inset-0 bg-black/75 backdrop-blur-sm flex items-center justify-center hidden z-50">
    <div class="bg-[#16161f] border border-[#262638] w-full max-w-xl rounded-3xl p-6 shadow-2xl relative space-y-4">
        <div class="flex justify-between items-center border-b border-[#262638] pb-3">
            <h3 class="text-lg font-serif font-bold text-white flex items-center space-x-2">
                <span>💳</span> <span>결제 및 구독 내역</span>
            </h3>
            <button onclick="closeModal('paymentHistoryModal')" class="text-gray-400 hover:text-white font-bold">✕</button>
        </div>
        <div class="max-h-80 overflow-y-auto pr-1 space-y-3" id="paymentHistoryContainer"></div>
    </div>
</div>

<!-- 🔔 알림 모달 & 프로필 모달 -->
<div id="notificationModal" class="fixed inset-0 bg-black/70 flex items-center justify-center hidden z-50">
    <div class="bg-[#16161f] border border-[#262638] w-full max-w-md rounded-3xl p-6">
        <div class="flex justify-between border-b border-[#262638] pb-2 mb-3">
            <h3 class="text-white font-bold">🔔 알림 센터</h3>
            <button onclick="closeModal('notificationModal')" class="text-gray-400">✕</button>
        </div>
        <p class="text-xs text-gray-300">새로운 결제 성공 알림 및 시스템 공지사항이 여기에 표시됩니다.</p>
    </div>
</div>

<div id="myInfoModal" class="fixed inset-0 bg-black/70 flex items-center justify-center hidden z-50">
    <div class="bg-[#16161f] border border-[#262638] w-full max-w-md rounded-3xl p-6">
        <div class="flex justify-between border-b border-[#262638] pb-2 mb-3">
            <h3 class="text-white font-bold">👤 내 정보</h3>
            <button onclick="closeModal('myInfoModal')" class="text-gray-400">✕</button>
        </div>
        <p class="text-xs text-gray-300">내 정보 관리 모달 내용</p>
    </div>
</div>

<!-- 분리된 JS 파일 연결 (경로는 프로젝트에 맞게 수정) -->
<script src="/resources/js/payments.js"></script>
<script src="/resources/js/subscription.js"></script>

</body>
</html>