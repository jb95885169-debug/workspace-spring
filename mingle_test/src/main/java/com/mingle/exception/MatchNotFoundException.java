package com.mingle.exception;

/**
 * 매칭이 없거나 요청한 회원이 참여자가 아님 (→ 404)
 */
public class MatchNotFoundException extends RuntimeException {

    public MatchNotFoundException() {
        super("매칭을 찾을 수 없습니다.");
    }
}
