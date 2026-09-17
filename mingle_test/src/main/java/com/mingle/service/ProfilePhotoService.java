package com.mingle.service;

import java.util.List;

/**
 * 프로필 사진
 *
 * 올리는 즉시 저장하지 않고 temp 폴더에 먼저 둔다 (FileStorageService.saveTemp).
 * 가입 / 수정이 끝난 뒤에야 profile 폴더로 옮기고 DB에 등록한다.
 * 임시 업로드 자체는 restcontroller.UploadRestController가 받는다.
 */
public interface ProfilePhotoService {

    /** 회원당 등록할 수 있는 사진 수 */
    int MAX_PHOTOS = 5;

    /**
     * 임시 파일을 profile 폴더로 옮기고 DB에 등록
     * 사진이 하나도 없던 회원이면 첫 장이 대표 사진이 된다.
     */
    void addPhotos(int userId, List<String> tempFileNames);

    /** 사진 삭제 (본인 것만, 대표 사진을 지우면 남은 사진 중 하나가 대표가 된다) */
    void deletePhoto(int userId, int photoId);

    /** 대표 사진 지정 (본인 것만) */
    void setPrimaryPhoto(int userId, int photoId);
}
