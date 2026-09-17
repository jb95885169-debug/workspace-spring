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
