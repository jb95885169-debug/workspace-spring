package com.mingle.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mingle.dto.ChatRoomResponse;
import com.mingle.dto.MatchChatResponse;
import com.mingle.dto.PageResponse;
import com.mingle.exception.MatchAlreadyCancelledException;
import com.mingle.exception.MatchNotFoundException;
import com.mingle.exception.SubscriptionRequiredException;
import com.mingle.mapper.MatchMapper;
import com.mingle.type.SubscriptionTier;
import com.mingle.util.TransactionUtils;
import com.mingle.vo.MatchAccessVO;
import com.mingle.websocket.StompDestinations;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class MatchServiceImpl implements MatchService {

	@Autowired
    private MatchMapper matchMapper;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	/** 채팅은 유료 기능이라 등급을 확인한다 */
	@Autowired
	private SubscriptionService subscriptionService;

	@Override
	@Transactional(readOnly = true)
	public void checkParticipant(int matchId, int userId) {
		getParticipantAccess(matchId, userId);
	}

	@Override
	@Transactional(readOnly = true)
	public void checkReadableParticipant(int matchId, int userId) {
		getReadableAccess(matchId, userId);
	}

	@Override
	@Transactional(readOnly = true)
	public void checkActiveParticipant(int matchId, int userId) {
		getActiveAccess(matchId, userId);
	}

	@Override
	@Transactional(readOnly = true)
	public ChatRoomResponse getChatRoom(int matchId, int userId) {

		MatchAccessVO access = getReadableAccess(matchId, userId);

		return new ChatRoomResponse(matchId, !isActive(access));
	}

	@Override
    @Transactional
    public void leaveMatch(int matchId, int userId) {

		MatchAccessVO access = getParticipantAccess(matchId, userId);

		if (isActive(access)) {
			leaveActiveMatch(matchId, userId, access.getPartnerId());
			return;
		}

		if (isCancelledByPartner(access, userId)) {
			leaveCancelledMatch(matchId, userId);
			return;
		}

		// 이미 내가 나갔거나 둘 다 나간 매칭
		throw new MatchAlreadyCancelledException();
    }

	@Override
	@Transactional(readOnly = true)
	public PageResponse<MatchChatResponse> getMatches(int userId, int page) {

		int size = CHAT_PAGE_SIZE;
		int offset = PageResponse.toOffset(page, size);

		return PageResponse.of(
				matchMapper.selectMatches(userId, offset, size),
				page,
				size,
				matchMapper.countMatches(userId));
	}

	@Override
	@Transactional(readOnly = true)
	public MatchChatResponse getMatchChat(int matchId, int userId) {
		return matchMapper.selectMatchChat(matchId, userId);
	}


	/**
	 * ACTIVE 매칭에서 먼저 나감 → CANCELLED
	 * 커밋 후 상대에게: 채팅방 화면은 읽기 전용으로, 채팅 목록은 "대화 종료" 표시로
	 */
	private void leaveActiveMatch(int matchId, int userId, int partnerId) {

        if (matchMapper.updateMatchCancelled(matchId, userId, null) == 0) {
            // 확인 직후 상대가 먼저 나간 경우 (동시성 엣지케이스)
            throw new MatchAlreadyCancelledException();
        }

        log.info("매칭 나가기(CANCELLED) - matchId: " + matchId + ", userId: " + userId);

        // 상대 채팅 목록 항목은 트랜잭션 안에서 미리 조회 (readOnly = true로 바뀐 상태)
        MatchChatResponse partnerChat = matchMapper.selectMatchChat(matchId, partnerId);
        Map<String, Integer> cancelEvent = Collections.singletonMap("matchId", matchId);

        TransactionUtils.afterCommit(() -> {

        	messagingTemplate.convertAndSendToUser(
        			String.valueOf(partnerId), StompDestinations.MATCH_CANCELLED, cancelEvent);

        	if (partnerChat != null) {
        		messagingTemplate.convertAndSendToUser(
        				String.valueOf(partnerId), StompDestinations.CHAT_LIST, partnerChat);
        	}
        });
	}

	/**
	 * 상대가 먼저 나간 매칭에서 나도 나감 → DESTROYED
	 * 상대는 이미 이 매칭을 볼 수 없으므로 알림 없음
	 */
	private void leaveCancelledMatch(int matchId, int userId) {

		if (matchMapper.updateMatchDestroyed(matchId) == 0) {
			throw new MatchAlreadyCancelledException();
		}

		log.info("매칭 나가기(DESTROYED) - matchId: " + matchId + ", userId: " + userId);
	}

	/** 없는 매칭이거나 참여자가 아니면 404 */
	private MatchAccessVO getParticipantAccess(int matchId, int userId) {

		MatchAccessVO access = matchMapper.selectMatchAccess(matchId, userId);

		if (access == null || !access.isParticipant()) {
			throw new MatchNotFoundException();
		}
		return access;
	}

	/**
	 * 채팅은 유료 회원만 (무료 회원은 목록까지만 보고 방에는 못 들어간다)
	 *
	 * 채팅방 조회 / 메시지 조회 / 메시지 전송 / 파일 업로드가 모두
	 * getReadableAccess 또는 getActiveAccess를 거치므로 여기서 한 번에 막는다.
	 * 나중에 "읽기는 되고 전송만 막기"로 바꾸려면 getActiveAccess에서만 부르면 된다.
	 */
	private void checkChatAllowed(int userId) {

		if (SubscriptionTier.BASIC == SubscriptionTier.of(subscriptionService.getCurrentTier(userId))) {
			throw new SubscriptionRequiredException("채팅은 골드 이상 회원만 이용할 수 있어요.");
		}
	}

	/** 대화 내용을 볼 수 있는 매칭: ACTIVE 또는 상대가 먼저 나간 매칭 (아니면 409) */
	private MatchAccessVO getReadableAccess(int matchId, int userId) {

		checkChatAllowed(userId);

		MatchAccessVO access = getParticipantAccess(matchId, userId);

		if (!isActive(access) && !isCancelledByPartner(access, userId)) {
			throw new MatchAlreadyCancelledException();
		}
		return access;
	}

	/** 메시지를 보낼 수 있는 매칭: ACTIVE만 (막을 상태를 나열하지 않고 ACTIVE일 때만 허용) */
	private MatchAccessVO getActiveAccess(int matchId, int userId) {

		checkChatAllowed(userId);

		MatchAccessVO access = getParticipantAccess(matchId, userId);

		if (!isActive(access)) {
			throw new MatchAlreadyCancelledException();
		}
		return access;
	}

	private boolean isActive(MatchAccessVO access) {
		return "ACTIVE".equals(access.getStatus());
	}

	/** 상대가 먼저 나간 매칭 (나는 아직 대화 내용을 볼 수 있음) */
	private boolean isCancelledByPartner(MatchAccessVO access, int userId) {
		return "CANCELLED".equals(access.getStatus())
				&& access.getCancelledBy() != null
				&& access.getCancelledBy() != userId;
	}
}
