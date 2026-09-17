package com.mingle.exception;

/**
 * 오늘 보낼 수 있는 슈퍼 좋아요를 모두 사용함 (→ 429)
 */
public class SuperLikeLimitExceededException extends RuntimeException {

    public SuperLikeLimitExceededException(int dailyLimit) {
        super("오늘 보낼 수 있는 슈퍼 좋아요(" + dailyLimit + "개)를 모두 사용했어요. 내일 다시 보낼 수 있어요.");
    }
}
