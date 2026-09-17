package com.mingle.service;

import java.util.List;

import com.mingle.vo.InterestVO;

/**
 * 관심사 선택지 (mingle_interests)
 * 가입 화면과 프로필 수정 화면이 함께 쓴다.
 */
public interface InterestService {

    /** 골라야 하는 최소 개수 */
    int MIN_INTERESTS = 3;

    List<InterestVO> getInterests();
}
