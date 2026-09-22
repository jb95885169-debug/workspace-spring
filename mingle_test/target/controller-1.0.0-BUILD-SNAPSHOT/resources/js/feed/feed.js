/* =========================================================
   피드 (글 / 좋아요 / 댓글)

   목록은 서버가 정한 개수씩 받고, 아래로 스크롤하면 이어서 받는다.
   사진은 photoUpload.js가 temp에 올려 두고, 글을 올릴 때 임시 파일명만 보낸다.
   ========================================================= */

const CATEGORY_NAMES = {
    NORMAL: "일상",
    PLACE: "장소",
    REVIEW: "후기"
};

// 지금까지 받은 페이지 (0이면 아직 못 받음)
let feedPage = 0;

let loadingFeeds = false;

let hasMoreFeeds = true;

// 수정 중인 글 (null이면 새 글쓰기)
let editingFeedId = null;

let reportTargetUserId = null;


document.addEventListener("DOMContentLoaded", function () {

    // 로그인하지 않았으면 common.js가 로그인 화면으로 보냄
    if (!userId) {
        return;
    }

    loadFeeds();
    initFeedScroll();

    document.getElementById("feedSubmitBtn")
        .addEventListener("click", function () {

            // 같은 폼을 글쓰기와 수정에 함께 쓴다
            if (editingFeedId === null) {
                createFeed();
            } else {
                updateFeed();
            }
        });

    document.getElementById("feedCancelBtn")
        .addEventListener("click", resetFeedForm);

    document.querySelectorAll("[data-report-close]")
        .forEach(function (element) {
            element.addEventListener("click", closeReportCenter);
        });

    document.querySelectorAll("[data-report-tab]")
        .forEach(function (button) {
            button.addEventListener("click", function () {
                switchReportTab(button.dataset.reportTab);
            });
        });

    document.getElementById("reportForm")
        .addEventListener("submit", submitReport);
});


/* =========================================================
   목록
   ========================================================= */

function loadFeeds() {

    if (loadingFeeds || !hasMoreFeeds) {
        return;
    }

    loadingFeeds = true;

    fetchJson("/api/feeds?page=" + (feedPage + 1))
        .then(function (data) {

            const list = document.getElementById("feedList");

            data.items.forEach(function (feed) {

                if (!findFeedCard(feed.id)) {
                    list.appendChild(createFeedCard(feed));
                }
            });

            feedPage = data.page;
            hasMoreFeeds = data.hasNext;

            if (data.totalCount === 0) {
                list.textContent = "아직 올라온 글이 없습니다.";
            }
        })
        .catch(function (error) {

            console.error("피드 조회 오류:", error);
        })
        .finally(function () {

            loadingFeeds = false;
        });
}


/* 화면 아래에 가까워지면 다음 페이지 */
function initFeedScroll() {

    window.addEventListener("scroll", function () {

        const bottom =
            window.innerHeight + window.scrollY >= document.body.offsetHeight - 100;

        if (bottom) {
            loadFeeds();
        }
    });
}


function findFeedCard(feedId) {

    return document.querySelector('.feed-card[data-feed-id="' + feedId + '"]');
}


/* =========================================================
   글쓰기
   ========================================================= */

function createFeed() {

    const title = document.getElementById("feedTitle");
    const category = document.getElementById("feedCategory");
    const content = document.getElementById("feedContent");

    // photoUpload.js가 넣어 둔 임시 파일명 (사진을 안 올렸으면 없음)
    const tempInput =
        document.querySelector('#tempPhotoList input[name="tempFileNames"]');

    const button = document.getElementById("feedSubmitBtn");
    button.disabled = true;

    fetch(contextPath + "/api/feeds", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            title: title.value,
            category: category.value,
            content: content.value,
            tempFileName: tempInput ? tempInput.value : null
        })
    })
    .then(function (response) {

        return response.json()
            .catch(function () {
                return {};
            })
            .then(function (data) {

                if (!response.ok) {
                    throw new Error(data.message || "글을 올리지 못했습니다.");
                }
                return data;
            });
    })
    .then(function (feed) {

        const list = document.getElementById("feedList");

        // 처음 글이면 안내 문구가 들어 있으므로 비운다
        if (!list.querySelector(".feed-card")) {
            list.textContent = "";
        }

        list.prepend(createFeedCard(feed));

        resetFeedForm();
    })
    .catch(function (error) {

        console.error("피드 작성 오류:", error);
        alert(error.message);
    })
    .finally(function () {

        button.disabled = false;
    });
}


/* =========================================================
   수정 (글쓰기 폼을 수정 모드로 바꿔 쓴다)
   ========================================================= */

function startEditFeed(feed) {

    editingFeedId = feed.id;

    document.getElementById("feedTitle").value = feed.title;
    document.getElementById("feedCategory").value = feed.category;
    document.getElementById("feedContent").value = feed.content;

    // 새로 올린 임시 사진이 남아 있으면 지운다
    document.getElementById("tempPhotoList").textContent = "";
    document.getElementById("removeImage").checked = false;

    // 사진이 있는 글에서만 "사진 빼기"를 고를 수 있다
    document.getElementById("removeImageRow").hidden = !feed.imageUrl;

    document.getElementById("feedSubmitBtn").textContent = "수정 저장";
    document.getElementById("feedCancelBtn").hidden = false;

    window.scrollTo({ top: 0, behavior: "smooth" });
}


function updateFeed() {

    const tempInput =
        document.querySelector('#tempPhotoList input[name="tempFileNames"]');

    const feedId = editingFeedId;
    const button = document.getElementById("feedSubmitBtn");
    button.disabled = true;

    fetch(contextPath + "/api/feeds/" + feedId, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            title: document.getElementById("feedTitle").value,
            category: document.getElementById("feedCategory").value,
            content: document.getElementById("feedContent").value,
            tempFileName: tempInput ? tempInput.value : null,
            removeImage: document.getElementById("removeImage").checked
        })
    })
    .then(function (response) {

        return response.json()
            .catch(function () {
                return {};
            })
            .then(function (data) {

                if (!response.ok) {
                    throw new Error(data.message || "글을 고치지 못했습니다.");
                }
                return data;
            });
    })
    .then(function (feed) {

        // 바뀐 내용으로 카드를 다시 그린다
        const card = findFeedCard(feedId);

        if (card) {
            card.replaceWith(createFeedCard(feed));
        }

        resetFeedForm();
    })
    .catch(function (error) {

        console.error("피드 수정 오류:", error);
        alert(error.message);
    })
    .finally(function () {

        button.disabled = false;
    });
}


/* 글쓰기 폼을 처음 상태로 */
function resetFeedForm() {

    editingFeedId = null;

    document.getElementById("feedTitle").value = "";
    document.getElementById("feedContent").value = "";
    document.getElementById("feedCategory").value = "NORMAL";

    document.getElementById("tempPhotoList").textContent = "";
    document.getElementById("removeImage").checked = false;
    document.getElementById("removeImageRow").hidden = true;

    document.getElementById("feedSubmitBtn").textContent = "올리기";
    document.getElementById("feedCancelBtn").hidden = true;
}


/* =========================================================
   피드 카드
   ========================================================= */

function createFeedCard(feed) {

    const card = document.createElement("div");
    card.classList.add("feed-card");
    card.dataset.feedId = feed.id;


    /* 윗줄: 작성자 / 카테고리 / 삭제 */

    const top = document.createElement("div");
    top.classList.add("feed-top");

    const writer = document.createElement("span");
    writer.classList.add("feed-writer");
    writer.textContent = feed.nickname;

    const category = document.createElement("span");
    category.classList.add("feed-category");
    category.textContent = CATEGORY_NAMES[feed.category] || feed.category;

    top.appendChild(writer);
    top.appendChild(category);

    const mine = Number(feed.userId) === Number(userId);

    // 내 글이면 수정 / 삭제, 관리자는 남의 글도 삭제만 (수정은 작성자만)
    if (mine || isAdmin) {

        const actions = document.createElement("span");
        actions.classList.add("feed-actions");

        if (mine) {

            const editButton = document.createElement("button");
            editButton.type = "button";
            editButton.classList.add("feed-edit-btn");
            editButton.textContent = "수정";

            editButton.addEventListener("click", function () {
                startEditFeed(feed);
            });

            actions.appendChild(editButton);
        }

        const deleteButton = document.createElement("button");
        deleteButton.type = "button";
        deleteButton.classList.add("feed-delete-btn");
        deleteButton.textContent = mine ? "삭제" : "삭제(관리자)";

        deleteButton.addEventListener("click", function () {
            deleteFeed(feed.id, card, !mine);
        });

        actions.appendChild(deleteButton);

        top.appendChild(actions);
    }


    /* 제목 / 내용 / 사진 */

    const title = document.createElement("div");
    title.classList.add("feed-title");
    title.textContent = feed.title;

    const content = document.createElement("div");
    content.classList.add("feed-content");
    content.textContent = feed.content;

    card.appendChild(top);
    card.appendChild(title);
    card.appendChild(content);

    if (feed.imageUrl) {

        const image = document.createElement("img");
        image.classList.add("feed-image");
        image.src = resolveUrl(feed.imageUrl);
        image.alt = "첨부 사진";

        card.appendChild(image);
    }


    /* 아랫줄: 좋아요 / 댓글 / 시각 */

    const bottom = document.createElement("div");
    bottom.classList.add("feed-bottom");

    const likeButton = document.createElement("button");
    likeButton.type = "button";
    likeButton.classList.add("feed-like-btn");
    setLikeButton(likeButton, feed);

    likeButton.addEventListener("click", function () {
        toggleLike(feed.id, likeButton);
    });

    const commentButton = document.createElement("button");
    commentButton.type = "button";
    commentButton.classList.add("feed-comment-btn");
    commentButton.textContent = "댓글 " + feed.commentCount;

    commentButton.addEventListener("click", function () {
        toggleComments(feed.id, card);
    });

    const reportButton = document.createElement("button");
    reportButton.type = "button";
    reportButton.classList.add("feed-report-btn");
    reportButton.textContent = "신고";
    reportButton.addEventListener("click", function () {
        openReportCenter(feed.userId);
    });

    const time = document.createElement("span");
    time.classList.add("feed-time");
    time.textContent = formatDate(feed.createdAt);

    bottom.appendChild(likeButton);
    bottom.appendChild(commentButton);
    bottom.appendChild(reportButton);
    bottom.appendChild(time);

    card.appendChild(bottom);

    return card;
}


function openReportCenter(targetUserId) {

    reportTargetUserId = targetUserId;
    document.getElementById("reportModal").hidden = false;
    switchReportTab("form");
    document.getElementById("reportReason").focus();
}


function closeReportCenter() {

    document.getElementById("reportModal").hidden = true;
}


function switchReportTab(tabName) {

    const form = document.getElementById("reportForm");
    const history = document.getElementById("reportHistory");

    document.querySelectorAll("[data-report-tab]")
        .forEach(function (button) {
            button.classList.toggle("active", button.dataset.reportTab === tabName);
        });

    form.hidden = tabName !== "form";
    history.hidden = tabName !== "history";

    if (tabName === "history") {
        loadMyReports();
    }
}


function submitReport(event) {

    event.preventDefault();

    fetch(contextPath + "/api/reports", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            targetUserId: reportTargetUserId,
            reason: document.getElementById("reportReason").value,
            title: document.getElementById("reportSubject").value,
            content: document.getElementById("reportContent").value
        })
    })
        .then(function (response) {
            if (response.ok) {
                return;
            }
            return response.json().catch(function () { return {}; })
                .then(function (data) {
                    throw new Error(data.message || "신고를 접수하지 못했습니다.");
                });
        })
        .then(function () {
            alert("신고가 접수되었습니다.");
            document.getElementById("reportForm").reset();
            closeReportCenter();
        })
        .catch(function (error) {
            alert(error.message);
        });
}


function loadMyReports() {

    const history = document.getElementById("reportHistory");
    history.textContent = "신고 내역을 불러오는 중입니다.";

    fetchJson("/api/reports/mine")
        .then(function (reports) {
            history.textContent = "";

            if (!reports.length) {
                history.textContent = "접수한 신고가 없습니다.";
                return;
            }

            reports.forEach(function (report) {
                const item = document.createElement("article");
                item.classList.add("report-history-item");

                const heading = document.createElement("div");
                heading.classList.add("report-history-heading");
                heading.textContent = report.title;

                const status = document.createElement("span");
                status.classList.add("report-status", "report-status-" + report.status.toLowerCase());
                status.textContent = report.status === "PENDING" ? "접수" : report.status === "RESOLVED" ? "처리 완료" : "반려";

                const reason = document.createElement("p");
                reason.textContent = "사유: " + reportReasonLabel(report.reason);

                const content = document.createElement("p");
                content.textContent = "내용: " + (report.content || "-");

                const target = document.createElement("p");
                target.textContent = "신고 대상: " + (report.targetNickname || "회원 " + report.targetUserId);

                const processedAt = document.createElement("p");
                processedAt.classList.add("report-processed-at");
                processedAt.textContent = report.processedAt
                    ? "처리일시: " + formatReportDate(report.processedAt)
                    : "아직 처리되지 않았습니다.";

                item.appendChild(heading);
                item.appendChild(status);
                item.appendChild(reason);
                item.appendChild(content);
                item.appendChild(target);
                item.appendChild(processedAt);
                history.appendChild(item);
            });
        })
        .catch(function () {
            history.textContent = "신고 내역을 불러오지 못했습니다.";
        });
}


function formatReportDate(value) {

    if (!value) {
        return "-";
    }

    const date = new Date(value);
    return isNaN(date.getTime()) ? value : date.toLocaleString("ko-KR");
}


function reportReasonLabel(reason) {

    const labels = {
        SPAM: "스팸 및 홍보",
        ABUSE: "욕설 및 비방",
        INAPPROPRIATE: "부적절한 콘텐츠",
        FAKE_PROFILE: "사칭 및 도용",
        ETC: "기타"
    };

    return labels[reason] || reason || "-";
}


function setLikeButton(button, feed) {

    button.textContent = (feed.liked ? "♥ " : "♡ ") + feed.likeCount;
    button.classList.toggle("liked", feed.liked);
}


/* =========================================================
   좋아요 / 삭제
   ========================================================= */

function toggleLike(feedId, button) {

    fetch(contextPath + "/api/feeds/" + feedId + "/like", { method: "POST" })
        .then(function (response) {

            if (!response.ok) {
                throw new Error("좋아요 처리에 실패했습니다.");
            }
            return response.json();
        })
        .then(function (feed) {

            setLikeButton(button, feed);
        })
        .catch(function (error) {

            console.error("좋아요 오류:", error);
            alert(error.message);
        });
}


function deleteFeed(feedId, card, byAdmin) {

    const message = byAdmin
        ? "관리자 권한으로 다른 회원의 글을 삭제할까요? 댓글도 함께 지워집니다."
        : "이 글을 삭제할까요? 댓글도 함께 지워집니다.";

    if (!confirm(message)) {
        return;
    }

    fetch(contextPath + "/api/feeds/" + feedId, { method: "DELETE" })
        .then(function (response) {

            if (response.status === 204) {
                card.remove();
                return;
            }
            throw new Error("삭제하지 못했습니다.");
        })
        .catch(function (error) {

            console.error("피드 삭제 오류:", error);
            alert(error.message);
        });
}


/* =========================================================
   댓글 (버튼을 누르면 열고 닫는다)
   ========================================================= */

function toggleComments(feedId, card) {

    const opened = card.querySelector(".comment-box");

    if (opened) {
        opened.remove();
        return;
    }

    const box = document.createElement("div");
    box.classList.add("comment-box");

    const list = document.createElement("div");
    list.classList.add("comment-list");

    const input = document.createElement("input");
    input.type = "text";
    input.classList.add("comment-input");
    input.placeholder = "댓글을 입력하세요";
    input.maxLength = 500;

    const addButton = document.createElement("button");
    addButton.type = "button";
    addButton.classList.add("comment-add-btn");
    addButton.textContent = "등록";

    addButton.addEventListener("click", function () {
        addComment(feedId, input, list, card);
    });

    input.addEventListener("keydown", function (event) {

        // 한글 조합 중 Enter는 무시
        if (event.key === "Enter" && !event.isComposing) {
            event.preventDefault();
            addComment(feedId, input, list, card);
        }
    });

    const form = document.createElement("div");
    form.classList.add("comment-form");
    form.appendChild(input);
    form.appendChild(addButton);

    box.appendChild(list);
    box.appendChild(form);

    card.appendChild(box);

    loadComments(feedId, list, card);
}


function loadComments(feedId, list, card) {

    fetchJson("/api/feeds/" + feedId + "/comments")
        .then(function (comments) {

            renderComments(comments, feedId, list, card);
        })
        .catch(function (error) {

            console.error("댓글 조회 오류:", error);
        });
}


function renderComments(comments, feedId, list, card) {

    list.textContent = "";

    if (comments.length === 0) {
        list.textContent = "첫 댓글을 남겨보세요.";
    }

    comments.forEach(function (comment) {
        list.appendChild(createCommentItem(comment, feedId, list, card));
    });

    // 댓글 버튼의 숫자도 맞춰 준다
    const commentButton = card.querySelector(".feed-comment-btn");

    if (commentButton) {
        commentButton.textContent = "댓글 " + comments.length;
    }
}


function createCommentItem(comment, feedId, list, card) {

    const item = document.createElement("div");
    item.classList.add("comment-item");

    const nickname = document.createElement("span");
    nickname.classList.add("comment-nickname");
    nickname.textContent = comment.nickname;

    const content = document.createElement("span");
    content.classList.add("comment-content");
    content.textContent = comment.content;

    item.appendChild(nickname);
    item.appendChild(content);

    const mine = Number(comment.userId) === Number(userId);

    // 내 댓글이면 수정
    if (mine) {

        const editButton = document.createElement("button");
        editButton.type = "button";
        editButton.classList.add("comment-edit-btn");
        editButton.textContent = "수정";

        editButton.addEventListener("click", function () {
            startEditComment(comment, item, feedId, list, card);
        });

        item.appendChild(editButton);
    }

    // 내 댓글이거나 관리자면 삭제
    if (mine || isAdmin) {

        const deleteButton = document.createElement("button");
        deleteButton.type = "button";
        deleteButton.classList.add("comment-delete-btn");
        deleteButton.textContent = "×";
        deleteButton.title = mine ? "삭제" : "관리자 삭제";

        deleteButton.addEventListener("click", function () {

            if (!mine && !confirm("관리자 권한으로 이 댓글을 삭제할까요?")) {
                return;
            }

            deleteComment(comment.id, feedId, list, card);
        });

        item.appendChild(deleteButton);
    }

    return item;
}


/* 댓글 한 줄을 입력창으로 바꾼다 */
function startEditComment(comment, item, feedId, list, card) {

    // 이미 수정 중이면 다시 만들지 않는다
    if (item.querySelector(".comment-edit-input")) {
        return;
    }

    const input = document.createElement("input");
    input.type = "text";
    input.classList.add("comment-edit-input");
    input.value = comment.content;
    input.maxLength = 500;

    const saveButton = document.createElement("button");
    saveButton.type = "button";
    saveButton.classList.add("comment-save-btn");
    saveButton.textContent = "저장";

    const cancelButton = document.createElement("button");
    cancelButton.type = "button";
    cancelButton.classList.add("comment-cancel-btn");
    cancelButton.textContent = "취소";

    // 수정 중에는 원래 줄 대신 입력창만 보여 준다
    item.textContent = "";
    item.appendChild(input);
    item.appendChild(saveButton);
    item.appendChild(cancelButton);

    input.focus();

    saveButton.addEventListener("click", function () {
        updateComment(comment.id, input.value, feedId, list, card);
    });

    cancelButton.addEventListener("click", function () {
        // 고치지 않고 원래 줄로 되돌린다
        item.replaceWith(createCommentItem(comment, feedId, list, card));
    });

    input.addEventListener("keydown", function (event) {

        // 한글 조합 중 Enter는 무시
        if (event.key === "Enter" && !event.isComposing) {
            event.preventDefault();
            updateComment(comment.id, input.value, feedId, list, card);
        }
    });
}


function updateComment(commentId, content, feedId, list, card) {

    if (content.trim() === "") {
        return;
    }

    fetch(contextPath + "/api/feeds/comments/" + commentId, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ content: content })
    })
    .then(function (response) {

        return response.json()
            .catch(function () {
                return {};
            })
            .then(function (data) {

                if (!response.ok) {
                    throw new Error(data.message || "댓글을 고치지 못했습니다.");
                }
                return data;
            });
    })
    .then(function (comments) {

        renderComments(comments, feedId, list, card);
    })
    .catch(function (error) {

        console.error("댓글 수정 오류:", error);
        alert(error.message);
    });
}


function addComment(feedId, input, list, card) {

    const content = input.value.trim();

    if (content === "") {
        return;
    }

    fetch(contextPath + "/api/feeds/" + feedId + "/comments", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ content: content })
    })
    .then(function (response) {

        return response.json()
            .catch(function () {
                return {};
            })
            .then(function (data) {

                if (!response.ok) {
                    throw new Error(data.message || "댓글을 쓰지 못했습니다.");
                }
                return data;
            });
    })
    .then(function (comments) {

        input.value = "";
        renderComments(comments, feedId, list, card);
    })
    .catch(function (error) {

        console.error("댓글 작성 오류:", error);
        alert(error.message);
    });
}


function deleteComment(commentId, feedId, list, card) {

    fetch(contextPath + "/api/feeds/comments/" + commentId, { method: "DELETE" })
        .then(function (response) {

            if (response.status === 204) {
                loadComments(feedId, list, card);
                return;
            }
            throw new Error("댓글을 지우지 못했습니다.");
        })
        .catch(function (error) {

            console.error("댓글 삭제 오류:", error);
            alert(error.message);
        });
}
