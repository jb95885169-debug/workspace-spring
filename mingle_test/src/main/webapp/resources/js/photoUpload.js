/* =========================================================
   사진 임시 업로드 (가입 / 프로필 수정 / 피드 작성 공용)

   고른 사진을 바로 저장하지 않고 temp 폴더에 먼저 올린다.
   폼을 제출해야 각 폴더(profile, feed)로 옮겨지고 DB에 등록된다.

   화면에 필요한 것
   - contextPath, photoLimit(더 올릴 수 있는 장수) 전역 변수
   - <input type="file" id="photoInput">
   - <div id="tempPhotoList"> (미리보기와 hidden tempFileNames가 들어감)
   - photoUpload.css (미리보기 스타일)
   ========================================================= */

const TEMP_UPLOAD_URL = "/api/uploads/temp";


document.addEventListener("DOMContentLoaded", function () {

    const fileInput = document.getElementById("photoInput");

    if (!fileInput) {
        return;
    }

    fileInput.addEventListener("change", function () {

        const files = Array.prototype.slice.call(fileInput.files);

        // 같은 사진을 다시 고를 수 있도록 값을 비운다
        fileInput.value = "";

        files.forEach(uploadTemp);
    });
});


/* 한 장씩 올리고, 성공하면 미리보기와 hidden 값을 추가 */
function uploadTemp(file) {

    if (countTempPhotos() >= photoLimit) {
        alert("사진은 " + photoLimit + "장까지 올릴 수 있습니다.");
        return;
    }

    const formData = new FormData();
    formData.append("file", file);

    fetch(contextPath + TEMP_UPLOAD_URL, {
        method: "POST",
        body: formData
    })
    .then(function (response) {

        return response.json()
            .catch(function () {
                return {};
            })
            .then(function (data) {

                if (!response.ok) {
                    throw new Error(data.message || "사진을 올리지 못했습니다.");
                }
                return data;
            });
    })
    .then(function (data) {

        addTempPhoto(data.tempFileName, data.tempUrl);
    })
    .catch(function (error) {

        console.error("사진 업로드 오류:", error);
        alert(error.message);
    });
}


function countTempPhotos() {

    return document.querySelectorAll("#tempPhotoList .temp-photo").length;
}


/* 미리보기 한 장 (제출하면 tempFileNames로 함께 전송된다) */
function addTempPhoto(tempFileName, tempUrl) {

    const list = document.getElementById("tempPhotoList");

    if (!list) {
        return;
    }

    const item = document.createElement("div");
    item.classList.add("temp-photo");

    const image = document.createElement("img");
    image.src = contextPath + tempUrl;
    image.alt = "올린 사진";

    // 폼 제출 시 서버로 보내는 값
    const hidden = document.createElement("input");
    hidden.type = "hidden";
    hidden.name = "tempFileNames";
    hidden.value = tempFileName;

    const removeButton = document.createElement("button");
    removeButton.type = "button";
    removeButton.classList.add("temp-photo-remove");
    removeButton.textContent = "×";

    // 저장 전이므로 화면에서만 빼면 된다 (temp 파일은 UploadCleanupTask가 정리)
    removeButton.addEventListener("click", function () {
        item.remove();
    });

    item.appendChild(image);
    item.appendChild(hidden);
    item.appendChild(removeButton);

    list.appendChild(item);
}
