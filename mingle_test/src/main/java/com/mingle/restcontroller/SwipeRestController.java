package com.mingle.restcontroller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mingle.dto.SwipeRequest;
import com.mingle.dto.SwipeResult;
import com.mingle.exception.SuperLikeLimitExceededException;
import com.mingle.security.LoginUserId;
import com.mingle.service.SwipeService;
import com.mingle.type.SubscriptionTier;

import lombok.extern.log4j.Log4j;

@RestController
@RequestMapping("/api/swipes")
@Log4j
public class SwipeRestController {

    @Autowired
    private SwipeService swipeService;

    // 스와이프 (LIKE / PASS / SUPER_LIKE)
    // 200 "MATCHED"(매칭 성사) 또는 "OK" / 400 잘못된 요청 / 429 오늘 슈퍼 좋아요 소진
    // 알림(매칭 / 좋아요)은 SwipeService가 처리
    @PostMapping
    public ResponseEntity<String> swipe(
            @RequestBody SwipeRequest request,
            @LoginUserId int userId) {

        SwipeResult result = swipeService.swipe(userId, request);

        log.info("SWIPE " + request.getAction() + " : " + userId + " -> " + request.getTargetId() + " = " + result);

        return ResponseEntity.ok(result == SwipeResult.MATCHED ? "MATCHED" : "OK");
    }

    // 오늘 남은 슈퍼 좋아요 { remaining, limit, tier, unlimited }
    // 개수는 구독 등급에 따라 다르다 (무료 1개 / 골드 3개 / 플래티넘 무제한)
    // 무제한이면 remaining과 limit이 -1로 내려간다
    @GetMapping("/super-likes/remaining")
    public ResponseEntity<Map<String, Object>> getRemainingSuperLikes(
            @LoginUserId int userId) {

        SubscriptionTier tier = swipeService.getTier(userId);

        Map<String, Object> body = new HashMap<>();
        body.put("remaining", swipeService.getRemainingSuperLikes(userId));
        body.put("limit", tier.getDailySuperLikeLimit());
        body.put("tier", tier.name());
        body.put("unlimited", tier.isUnlimitedSuperLike());

        return ResponseEntity.ok(body);
    }


    /** 잘못된 스와이프 요청 (없는 대상, 자기 자신, 이미 스와이프함 등) */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleInvalidSwipe(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(errorBody(e.getMessage()));
    }

    /** 오늘 슈퍼 좋아요 소진 */
    @ExceptionHandler(SuperLikeLimitExceededException.class)
    public ResponseEntity<Map<String, String>> handleSuperLikeLimitExceeded(SuperLikeLimitExceededException e) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorBody(e.getMessage()));
    }

    private Map<String, String> errorBody(String message) {
        return Collections.singletonMap("message", message);
    }
}
