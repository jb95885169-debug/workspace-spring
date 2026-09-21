<%@ page contentType="text/html; charset=UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>피드</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/feed.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/photoUpload.css">
</head>

<body>

<div class="feed-container">

    <header class="feed-header">
        <button type="button" onclick="goUserList()">←</button>
        <h2>피드</h2>
        <a class="logout-link" href="${pageContext.request.contextPath}/chat/list">채팅</a>
    </header>


    <!-- ================= 글쓰기 ================= -->
    <section class="feed-form">

        <input type="text"
               id="feedTitle"
               placeholder="제목"
               maxlength="100">

        <select id="feedCategory">
            <option value="NORMAL">일상</option>
            <option value="PLACE">장소</option>
            <option value="REVIEW">후기</option>
        </select>

        <textarea id="feedContent"
                  rows="3"
                  placeholder="무슨 일이 있었나요?"
                  maxlength="1000"></textarea>

        <!-- photoUpload.js가 temp에 올리고 미리보기를 붙인다 (사진 1장) -->
        <input type="file" id="photoInput" accept="image/*">

        <div id="tempPhotoList" class="temp-photo-list"></div>

        <!-- 수정할 때만 보인다 (새 사진을 올리면 교체, 이걸 켜면 사진을 뗀다) -->
        <label id="removeImageRow" class="remove-image-row" hidden=true>
            <input type="checkbox" id="removeImage"> 지금 사진 빼기
        </label>

        <div class="feed-form-buttons">
            <button type="button" id="feedSubmitBtn">올리기</button>
            <button type="button" id="feedCancelBtn" hidden>취소</button>
        </div>

    </section>


    <!-- ================= 목록 (feed.js가 /api/feeds로 채움) ================= -->
    <section class="feed-list" id="feedList"></section>

</div>

<div id="reportModal" class="report-modal" hidden>
    <div class="report-backdrop" data-report-close></div>
    <section class="report-panel" role="dialog" aria-modal="true" aria-labelledby="reportTitle">
        <div class="report-header">
            <h2 id="reportTitle">🚨 신고 센터</h2>
            <button type="button" class="report-close-btn" data-report-close aria-label="신고 창 닫기">×</button>
        </div>
        <div class="report-tabs">
            <button type="button" class="report-tab active" data-report-tab="form">🚨 새 신고 등록</button>
            <button type="button" class="report-tab" data-report-tab="history">📋 나의 신고 내역</button>
        </div>

        <form id="reportForm" class="report-form">
            <label for="reportReason">신고 사유</label>
            <select id="reportReason" required>
                <option value="">신고 사유를 선택해 주세요.</option>
                <option value="스팸 및 홍보">스팸 및 홍보</option>
                <option value="욕설 및 비방">욕설 및 비방</option>
                <option value="부적절한 콘텐츠">부적절한 콘텐츠</option>
                <option value="사칭 및 도용">사칭 및 도용</option>
                <option value="기타">기타</option>
            </select>
            <label for="reportSubject">신고 제목</label>
            <input id="reportSubject" type="text" maxlength="100" placeholder="제목을 입력해 주세요." required>
            <label for="reportContent">신고 내용</label>
            <textarea id="reportContent" rows="5" maxlength="1000" placeholder="신고 내용을 자세히 적어 주세요." required></textarea>
            <button type="submit" class="report-submit-btn">신고 접수하기</button>
        </form>

        <div id="reportHistory" class="report-history" hidden>
            <p class="report-loading">신고 내역을 불러오는 중입니다.</p>
        </div>
    </section>
</div>


<script>
    const contextPath = '${pageContext.request.contextPath}';

    // 로그인 회원 (세션의 userId, 없으면 null → common.js가 로그인 화면으로 보냄)
    const userId = Number('${sessionScope.userId}') || null;

    // 피드는 사진 1장만
    const photoLimit = 1;

    // 관리자면 남의 글·댓글에도 삭제 버튼을 보여 준다 (실제 권한 확인은 서버가 한다)
    const isAdmin = ${pageContext.request.isUserInRole('ADMIN')};
</script>

<script src="${pageContext.request.contextPath}/resources/js/common.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/photoUpload.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/feed/feed.js"></script>

</body>
</html>
