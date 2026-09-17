package com.mingle.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mingle.dto.SwipeRequest;
import com.mingle.dto.SwipeResult;
import com.mingle.exception.SuperLikeLimitExceededException;
import com.mingle.mapper.MatchMapper;
import com.mingle.mapper.SwipeMapper;
import com.mingle.type.SubscriptionTier;
import com.mingle.util.TransactionUtils;
import com.mingle.vo.MatchVO;
import com.mingle.vo.SwipeVO;

import lombok.extern.log4j.Log4j;


@Service
@Log4j
public class SwipeServiceImpl implements SwipeService {

	private static final List<String> ACTIONS =
			Arrays.asList("LIKE", "PASS", "SUPER_LIKE");

	@Autowired
    private SwipeMapper swipeMapper;
	@Autowired
    private MatchMapper matchMapper;
	@Autowired
	private NotificationService notificationService;

	/** 슈퍼 좋아요 개수가 구독 등급에 따라 달라진다 */
	@Autowired
	private SubscriptionService subscriptionService;

	@Override
	@Transactional
	public SwipeResult swipe(int swiperId, SwipeRequest request) {

		String action = request.getAction();
		int targetId = request.getTargetId();

		if (!ACTIONS.contains(action)) {
			throw new IllegalArgumentException("지원하지 않는 스와이프입니다.");
		}
		if (targetId == swiperId) {
			throw new IllegalArgumentException("자기 자신에게는 스와이프할 수 없습니다.");
		}

	    // 이미 스와이프한 회원이면 아무것도 하지 않음 (알림도 보내지 않음)
	    if (swipeMapper.existsSwipe(swiperId, targetId)) {
	        return SwipeResult.DUPLICATE;
	    }

	    // 슈퍼 좋아요는 하루 개수 제한 (등급에 따라 다르고, 플래티넘은 제한 없음)
	    if ("SUPER_LIKE".equals(action)) {

	    	SubscriptionTier tier = getTier(swiperId);

	    	if (!tier.isUnlimitedSuperLike()
	    			&& swipeMapper.countTodaySuperLikes(swiperId) >= tier.getDailySuperLikeLimit()) {

	    		throw new SuperLikeLimitExceededException(tier.getDailySuperLikeLimit());
	    	}
	    }

	    SwipeVO swipe = new SwipeVO();
	    swipe.setSwiperId(swiperId);
	    swipe.setTargetId(targetId);
	    swipe.setAction(action);

	    swipeMapper.insertSwipe(swipe);

	    if ("PASS".equals(action)) {
	    	return SwipeResult.PASSED;
	    }

	    // 상대도 나에게 좋아요(슈퍼 포함)를 보냈으면 매칭
	    if (swipeMapper.existsLike(targetId, swiperId)) {

	        int user1Id = Math.min(swiperId, targetId);
	        int user2Id = Math.max(swiperId, targetId);

	        if (!matchMapper.existsMatch(user1Id, user2Id)) {

	            MatchVO match = new MatchVO();
	            match.setUser1Id(user1Id);
	            match.setUser2Id(user2Id);
	            match.setStatus("ACTIVE");

	            matchMapper.insertMatch(match);

	            int matchId = match.getId();

	            log.info("매칭 생성 - matchId: " + matchId + " (" + action + ")");

	            // 커밋 후 양쪽 채팅목록 갱신 + 상대에게 매칭 알림
	            TransactionUtils.afterCommit(() ->
	            		notificationService.notifyMatch(matchId, swiperId, targetId));

	            return SwipeResult.MATCHED;
	        }
	    }

	    // 커밋 후 상대에게 좋아요 / 슈퍼 좋아요 알림
	    boolean superLike = "SUPER_LIKE".equals(action);

	    TransactionUtils.afterCommit(() ->
	    		notificationService.notifyLike(swiperId, targetId, superLike));

	    return SwipeResult.LIKED;
	}

	@Override
	@Transactional(readOnly = true)
	public int getDailySuperLikeLimit(int userId) {
		return getTier(userId).getDailySuperLikeLimit();
	}

	@Override
	@Transactional(readOnly = true)
	public int getRemainingSuperLikes(int userId) {

		SubscriptionTier tier = getTier(userId);

		if (tier.isUnlimitedSuperLike()) {
			return SubscriptionTier.UNLIMITED;
		}

		int usedToday = swipeMapper.countTodaySuperLikes(userId);

		return Math.max(0, tier.getDailySuperLikeLimit() - usedToday);
	}

	@Override
	@Transactional(readOnly = true)
	public SubscriptionTier getTier(int userId) {
		return SubscriptionTier.of(subscriptionService.getCurrentTier(userId));
	}
}
