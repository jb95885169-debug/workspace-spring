package com.mingle.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 채팅방(매칭) 접근 권한 확인용 조회 결과
 * MatchService 안에서만 사용
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchAccessVO {

    private boolean participant;   // 요청한 회원이 매칭 참여자인지
    private int partnerId;         // 요청한 회원 기준 상대 회원 ID
    private String status;         // ACTIVE / CANCELLED ...
    private Integer cancelledBy;   // 매칭을 취소한 회원 ID (취소 전이면 null)
}
