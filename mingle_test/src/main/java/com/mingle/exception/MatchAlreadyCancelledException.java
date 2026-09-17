package com.mingle.exception;

/**
 * 이미 취소(또는 종료)된 매칭을 다시 취소하려고 함 (→ 409)
 */
public class MatchAlreadyCancelledException extends RuntimeException {

    public MatchAlreadyCancelledException() {
        super("이미 취소된 매칭입니다.");
    }
}
