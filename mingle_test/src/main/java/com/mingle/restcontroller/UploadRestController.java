package com.mingle.restcontroller;

import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mingle.dto.TempUploadResponse;
import com.mingle.service.FileStorageService;
import com.mingle.type.UploadFileType;
import com.mingle.util.StoredFile;

import lombok.extern.log4j.Log4j;

/**
 * 임시 업로드 (프로필 사진 / 피드 첨부 공용)
 *
 * 저장이 확정되기 전에 temp 폴더에 두고, 폼을 제출하면 각 서비스가 제 폴더로 옮긴다.
 * 가입 화면에서도 쓰므로 로그인 없이 호출할 수 있다. (security-context.xml)
 * 옮겨지지 않은 파일은 UploadCleanupTask가 지운다.
 */
@RestController
@RequestMapping("/api/uploads")
@Log4j
public class UploadRestController {

    @Autowired
    private FileStorageService fileStorageService;


    // 임시 업로드 → { tempFileName, tempUrl }
    @PostMapping("/temp")
    public ResponseEntity<TempUploadResponse> uploadTemp(
            @RequestParam("file") MultipartFile file) {
    	
    	//UploadFileType.VIDEO 추가하면 동영상도 보낼수있음
        StoredFile stored = fileStorageService.saveTemp(
                file,
                Arrays.asList(UploadFileType.IMAGE));

        return ResponseEntity.ok(new TempUploadResponse(stored.getFileName(), stored.getUrl()));
    }


    /** 잘못된 파일 (형식, 빈 파일) */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleInvalidFile(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(errorBody(e.getMessage()));
    }

    /** 저장 실패 (내부 경로는 로그에만 남김) */
    @ExceptionHandler(UncheckedIOException.class)
    public ResponseEntity<Map<String, String>> handleSaveFailure(UncheckedIOException e) {
        log.error("임시 파일 저장 실패", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody("파일 저장에 실패했습니다."));
    }

    private Map<String, String> errorBody(String message) {
        return Collections.singletonMap("message", message);
    }
}
