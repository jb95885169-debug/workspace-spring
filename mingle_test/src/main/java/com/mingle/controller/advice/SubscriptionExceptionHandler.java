package com.mingle.controller.advice;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mingle.exception.SubscriptionRequiredException;

/**
 * 구독이 필요한 기능 → 403 (본문: { "message": "..." })
 * 화면은 이 메시지를 보여 주고 구독 화면으로 보낸다.
 */
@RestControllerAdvice
public class SubscriptionExceptionHandler {

    @ExceptionHandler(SubscriptionRequiredException.class)
    public ResponseEntity<Map<String, String>> handleSubscriptionRequired(SubscriptionRequiredException e) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Collections.singletonMap("message", e.getMessage()));
    }
}
