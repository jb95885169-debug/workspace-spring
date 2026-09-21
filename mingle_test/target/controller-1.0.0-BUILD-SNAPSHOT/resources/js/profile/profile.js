/* =========================================================
   프로필 수정 화면 (이미 등록된 사진 관리)

   추가는 photoUpload.js가 맡는다 (temp에 올린 뒤 폼 제출).
   여기서는 등록된 사진의 삭제와 대표 지정을 바로 반영한다.
   ========================================================= */

const PHOTO_API = "/api/profile-photos";


document.addEventListener("click", function (event) {

    const deleteButton = event.target.closest(".photo-delete-btn");

    if (deleteButton) {
        deletePhoto(deleteButton.dataset.photoId);
        return;
    }

    const primaryButton = event.target.closest(".photo-primary-btn");

    if (primaryButton) {
        setPrimaryPhoto(primaryButton.dataset.photoId);
    }
});


function deletePhoto(photoId) {

    if (!confirm("이 사진을 삭제할까요?")) {
        return;
    }

    sendPhotoRequest("/" + photoId, "DELETE");
}


function setPrimaryPhoto(photoId) {

    sendPhotoRequest("/" + photoId + "/primary", "PUT");
}


/* 성공하면 화면을 다시 불러온다 (대표 표시와 순서를 서버 기준으로 맞추기 위해) */
function sendPhotoRequest(path, method) {

    fetch(contextPath + PHOTO_API + path, { method: method })
        .then(function (response) {

            if (response.status === 204) {
                location.href = contextPath + "/profile";
                return;
            }

            // 로그인이 풀리면 로그인 화면(HTML)이 오므로 JSON 파싱이 실패한다
            return response.json()
                .catch(function () {
                    return {};
                })
                .then(function (data) {
                    throw new Error(
                        (data.message || "처리하지 못했습니다.") + " (" + response.status + ")");
                });
        })
        .catch(function (error) {

            console.error("사진 처리 오류:", error);
            alert(error.message);
        });
}
