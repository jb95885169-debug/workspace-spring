package com.mingle.dto;

/**
 * 스와이프 처리 결과
 */
public enum SwipeResult {

    MATCHED,    // 서로 좋아요 → 매칭 성사 (양쪽 채팅목록 갱신 + 상대에게 매칭 알림)
    LIKED,      // 좋아요 / 슈퍼 좋아요를 보냄 (상대에게 좋아요 알림)
    PASSED,     // 패스 (알림 없음)
    DUPLICATE   // 이미 스와이프한 회원 (아무것도 하지 않음)
}
