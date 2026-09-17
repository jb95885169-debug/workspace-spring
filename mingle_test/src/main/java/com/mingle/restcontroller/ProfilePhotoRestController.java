package com.mingle.restcontroller;

import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mingle.security.LoginUserId;
import com.mingle.service.ProfilePhotoService;

import lombok.extern.log4j.Log4j;

/**
 * 프로필 사진 (등록된 사진 관리)
 *
 * 사진을 올리는 것은 공용 임시 업로드(UploadRestController, /api/uploads/temp)를 쓰고,
 * 가입 / 프로필 수정 폼을 제출해야 profile 폴더로 옮겨진다.
 */
@RestController
@RequestMapping("/api/profile-photos")
@Log4j
public class ProfilePhotoRestController {

    @Autowired
    private ProfilePhotoService profilePhotoService;


    // 사진 삭제 (본인 것만)
    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> deletePhoto(
            @PathVariable int photoId,
            @LoginUserId int userId) {

        profilePhotoService.deletePhoto(userId, photoId);

        return ResponseEntity.noContent().build();
    }


    // 대표 사진 지정 (본인 것만)
    @PutMapping("/{photoId}/primary")
    public ResponseEntity<Void> setPrimaryPhoto(
            @PathVariable int photoId,
            @LoginUserId int userId) {

        profilePhotoService.setPrimaryPhoto(userId, photoId);

        return ResponseEntity.noContent().build();
    }


    /** 잘못된 파일 (형식, 빈 파일, 장수 초과, 없는 사진) */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleInvalidPhoto(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(errorBody(e.getMessage()));
    }

    /** 저장 실패 (내부 경로는 로그에만 남김) */
    @ExceptionHandler(UncheckedIOException.class)
    public ResponseEntity<Map<String, String>> handleSaveFailure(UncheckedIOException e) {
        log.error("프로필 사진 저장 실패", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody("사진 저장에 실패했습니다."));
    }

    private Map<String, String> errorBody(String message) {
        return Collections.singletonMap("message", message);
    }
}
