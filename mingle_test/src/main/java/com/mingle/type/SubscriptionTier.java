package com.mingle.type;

/**
 * 구독 등급과 혜택
 *
 * 등급은 따로 저장하지 않고 기간이 남은 구독의 상품 등급으로 계산한다.
 * (SubscriptionService.getCurrentTier, 구독이 없으면 BASIC)
 *
 * level : 등급 비교용 (높을수록 상위). 업그레이드는 허용하고 다운그레이드는 막는다.
 */
public enum SubscriptionTier {

    /** 무료 회원 */
    BASIC(1, 0, "무료 회원"),

    /** 골드 */
    GOLD(3, 1, "골드"),

    /**
     * 플래티넘 (슈퍼 좋아요 무제한 + 우선 좋아요)
     * 우선 좋아요 : 내가 보낸 좋아요가 상대 목록에서 앞쪽에 노출된다.
     *              (UserMapper.xml의 priorityLike, 받는 사람에게는 티가 나지 않는다)
     */
    PLATINUM(-1, 2, "플래티넘");

    /** 하루 제한이 없다는 뜻 */
    public static final int UNLIMITED = -1;

    private final int dailySuperLikeLimit;
    private final int level;
    private final String displayName;

    SubscriptionTier(int dailySuperLikeLimit, int level, String displayName) {
        this.dailySuperLikeLimit = dailySuperLikeLimit;
        this.level = level;
        this.displayName = displayName;
    }

    /** 하루에 보낼 수 있는 슈퍼 좋아요 수 (UNLIMITED면 제한 없음) */
    public int getDailySuperLikeLimit() {
        return dailySuperLikeLimit;
    }

    /** 등급 순서 (BASIC 0 < GOLD 1 < PLATINUM 2) */
    public int getLevel() {
        return level;
    }

    /** 화면에 보여 줄 이름 */
    public String getDisplayName() {
        return displayName;
    }

    public boolean isUnlimitedSuperLike() {
        return dailySuperLikeLimit == UNLIMITED;
    }

    /** this가 other보다 낮은 등급인지 */
    public boolean isLowerThan(SubscriptionTier other) {
        return this.level < other.level;
    }

    /** 문자열 등급 → enum (모르는 값이면 무료 회원으로 본다) */
    public static SubscriptionTier of(String tier) {

        if (tier == null) {
            return BASIC;
        }

        try {
            return valueOf(tier.trim().toUpperCase());

        } catch (IllegalArgumentException e) {
            return BASIC;
        }
    }
}
