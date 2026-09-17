<%@ page contentType="text/html; charset=UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>구독</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/subscription.css">
</head>

<body>

<div class="subscription-container">

    <header class="subscription-header">
        <button type="button" onclick="goUserList()">←</button>
        <h2>구독</h2>
        <a class="logout-link" href="${pageContext.request.contextPath}/profile">내 프로필</a>
    </header>


    <!-- 현재 등급 (subscription.js가 /api/subscriptions/me로 채움) -->
    <section class="tier-box" id="tierBox">
        <div class="tier-name" id="tierName">불러오는 중...</div>
        <div class="tier-period" id="tierPeriod"></div>
    </section>


    <section>
        <h3>구독 상품</h3>
        <div class="product-list" id="productList"></div>
    </section>


    <section>
        <h3>결제 내역</h3>
        <div class="history-list" id="historyList"></div>
    </section>

</div>


<script>
    const contextPath = '${pageContext.request.contextPath}';

    // 로그인 회원 (세션의 userId, 없으면 null → common.js가 로그인 화면으로 보냄)
    const userId = Number('${sessionScope.userId}') || null;
</script>

<script src="${pageContext.request.contextPath}/resources/js/common.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/subscription/subscription.js"></script>

</body>
</html>
