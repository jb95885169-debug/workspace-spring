<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="shortcut icon" href="#">
<link rel="stylesheet" href="/resources/css/main.css">
<title>Insert title here</title>
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
            <span class="text-xs font-bold text-[#d4af37]" id="currentTierDisplay">👑 ${member.subscriptionTier} 멤버</span>	<!-- 구독권등급 -->
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
