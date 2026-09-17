package com.mingle.controller;

import java.io.UncheckedIOException;
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

import com.mingle.dto.ChatFileUploadResponse;
import com.mingle.security.LoginUserId;
import com.mingle.service.ChatFileService;

import lombok.extern.log4j.Log4j;

/**
 * 채팅 사진 / 동영상 업로드
 * 업로드 후 받은 url, messageType으로 STOMP /app/chat/send 전송
 * (용량 초과는 advice.FileUploadExceptionHandler에서 처리)
 */
@RestController
@RequestMapping("/chat/upload")
@Log4j
public class ChatFileUploadController {

    @Autowired
    private ChatFileService chatFileService;

    /**
     * POST /chat/upload (multipart/form-data: file, matchId)
     */
    @PostMapping
    public ResponseEntity<ChatFileUploadResponse> uploadFile(@RequestParam("matchId") int matchId,
                                        @RequestParam("file") MultipartFile file,
                                        @LoginUserId int userId) {

        // 로그인 확인은 Spring Security가 한다 (여기까지 오면 로그인된 상태)
        return ResponseEntity.ok(chatFileService.upload(userId, matchId, file));
    }

    /** 잘못된 파일 (형식, 빈 파일) */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleInvalidFile(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(errorBody(e.getMessage()));
    }

    // 참여하지 않았거나(404) 종료된(409) 채팅방은 controller.advice.MatchExceptionHandler가 응답

    /** 저장 실패 (내부 경로는 로그에만 남김) */
    @ExceptionHandler(UncheckedIOException.class)
    public ResponseEntity<Map<String, String>> handleSaveFailure(UncheckedIOException e) {
        log.error("채팅 파일 저장 실패", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody("파일 저장에 실패했습니다."));
    }

    private Map<String, String> errorBody(String message) {
        return Collections.singletonMap("message", message);
    }
}
