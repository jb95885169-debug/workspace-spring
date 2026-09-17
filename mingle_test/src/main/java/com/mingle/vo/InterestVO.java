package com.mingle.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 관심사 선택지 (mingle_interests)
 * 가입 화면의 체크박스 목록으로 쓴다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterestVO {

    private int id;
    private String name;
}
