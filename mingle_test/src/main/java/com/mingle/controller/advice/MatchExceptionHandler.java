package com.mingle.controller.advice;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mingle.exception.MatchAlreadyCancelledException;
import com.mingle.exception.MatchNotFoundException;

/**
 * 매칭(채팅방) 권한 예외 → HTTP 응답 (본문: { "message": "..." })
 * MatchService.checkParticipant / checkActiveParticipant를 거치는 모든 API에 공통 적용
 * (STOMP 메시지는 websocket.ChatMessageController가 따로 처리)
 */
@RestControllerAdvice
public class MatchExceptionHandler {

    /** 없는 매칭 / 참여자 아님 */
    @ExceptionHandler(MatchNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleMatchNotFound(MatchNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody(e.getMessage()));
    }

    /** 이미 취소(종료)된 매칭 */
    @ExceptionHandler(MatchAlreadyCancelledException.class)
    public ResponseEntity<Map<String, String>> handleMatchAlreadyCancelled(MatchAlreadyCancelledException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody(e.getMessage()));
    }

    private Map<String, String> errorBody(String message) {
        return Collections.singletonMap("message", message);
    }
}
