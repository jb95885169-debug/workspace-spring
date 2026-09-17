package com.mingle.controller.advice;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 업로드 용량 초과 (web.xml multipart-config 200MB)
 *
 * multipart 파싱은 DispatcherServlet이 컨트롤러를 정하기 전에 일어나므로
 * 컨트롤러 안의 @ExceptionHandler로는 잡히지 않고 전역 advice에서만 잡힌다.
 */
@RestControllerAdvice
public class FileUploadExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxUploadSize(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(Collections.singletonMap("message", "파일 용량이 너무 큽니다. (최대 200MB)"));
    }
}
