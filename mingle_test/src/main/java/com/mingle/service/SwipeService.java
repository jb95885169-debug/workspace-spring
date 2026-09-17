package com.mingle.service;

import com.mingle.dto.SwipeRequest;
import com.mingle.dto.SwipeResult;
import com.mingle.type.SubscriptionTier;

public interface SwipeService {

    /**
     * 스와이프 (LIKE / PASS / SUPER_LIKE)
     * 결과에 맞는 실시간 알림(매칭 / 좋아요)도 커밋 후 보낸다.
     */
    SwipeResult swipe(int swiperId, SwipeRequest request);

    /**
     * 하루에 보낼 수 있는 슈퍼 좋아요 수 (구독 등급에 따라 다름)
     * 플래티넘은 SubscriptionTier.UNLIMITED
     */
    int getDailySuperLikeLimit(int userId);

    /**
     * 오늘 남은 슈퍼 좋아요 수 (0시에 초기화)
     * 제한이 없는 등급이면 SubscriptionTier.UNLIMITED
     */
    int getRemainingSuperLikes(int userId);

    /** 지금 내 등급 (화면 표시용) */
    SubscriptionTier getTier(int userId);
}
