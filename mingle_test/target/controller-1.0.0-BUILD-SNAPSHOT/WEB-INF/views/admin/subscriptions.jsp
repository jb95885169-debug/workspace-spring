<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>구독 상품 관리</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/admin-subscriptions.css">
</head>
<body>
<main class="admin-page">
    <header class="admin-header">
        <div>
            <p class="eyebrow">ADMIN / BILLING</p>
            <h1>구독 상품 관리</h1>
            <p>상품 가격과 판매 상태를 변경하면 다음 결제부터 서버 정책에 반영됩니다.</p>
        </div>
        <a href="${pageContext.request.contextPath}/users/list">서비스로 돌아가기</a>
    </header>
    <section id="productList" class="product-list" aria-live="polite">
        <p class="loading">상품을 불러오는 중입니다.</p>
    </section>
    <section class="admin-section">
        <h2>회원 상태</h2>
        <div id="userList">불러오는 중입니다.</div>
    </section>
    <section class="admin-section">
        <h2>문의 답변</h2>
        <div id="ticketList">불러오는 중입니다.</div>
    </section>
    <section class="admin-section">
        <h2>신고 처리</h2>
        <div id="reportList">불러오는 중입니다.</div>
    </section>
</main>
<script>
    const contextPath = '${pageContext.request.contextPath}';
</script>
<script src="${pageContext.request.contextPath}/resources/js/admin/subscriptions.js?v=20260921-2"></script>
</body>
</html>
