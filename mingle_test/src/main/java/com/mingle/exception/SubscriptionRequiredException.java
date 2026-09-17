package com.mingle.exception;

/**
 * 무료 회원이 유료 기능을 쓰려고 함 (→ 403)
 *
 * 화면은 이 응답을 받으면 구독 화면으로 안내한다.
 */
public class SubscriptionRequiredException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SubscriptionRequiredException(String message) {
        super(message);
    }
}
