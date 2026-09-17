package com.mingle.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mingle.dto.MatchChatResponse;
import com.mingle.dto.NotificationMessage;
import com.mingle.dto.UserResponse;
import com.mingle.type.NotificationType;
import com.mingle.websocket.StompDestinations;

import lombok.extern.log4j.Log4j;

/**
 * 실시간 알림 (WebSocket)
 *
 * SwipeServiceImpl이 커밋 후(TransactionUtils.afterCommit) 호출하므로
 * 조회는 REQUIRES_NEW로 새 트랜잭션에서 한다.
 */
@Service
@Log4j
public class NotificationServiceImpl implements NotificationService {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private UserService userService;

	@Autowired
	private MatchService matchService;

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
	public void notifyMatch(int matchId, int userId, int targetId) {

		// 내 채팅리스트 데이터
		MatchChatResponse myChat =
				matchService.getMatchChat(matchId, userId);

		// 상대방 채팅리스트 데이터
		MatchChatResponse targetChat =
				matchService.getMatchChat(matchId, targetId);

		if (myChat == null || targetChat == null) {
			log.warn("Match notification skipped - 채팅목록 데이터 없음, matchId: " + matchId);
			return;
		}

		// 내 채팅리스트 갱신 (/user/queue/chat-list)
		messagingTemplate.convertAndSendToUser(
				String.valueOf(userId),
				StompDestinations.CHAT_LIST,
				myChat
		);

		// 상대방 채팅리스트 갱신
		messagingTemplate.convertAndSendToUser(
				String.valueOf(targetId),
				StompDestinations.CHAT_LIST,
				targetChat
		);

		// 상대방 알림 (/user/queue/notification)
		// targetChat은 상대(targetId) 기준 조회라 nickname이 매칭 상대(= 나)의 닉네임
		NotificationMessage notification = new NotificationMessage(
				NotificationType.MATCH,
				userId,
				targetChat.getNickname(),
				targetChat.getNickname() + "님과 매칭되었습니다.",
				matchId
		);

		messagingTemplate.convertAndSendToUser(
				String.valueOf(targetId),
				StompDestinations.NOTIFICATION,
				notification
		);

		log.info("Match notification sent - matchId: " + matchId + ", userId: " + userId + ", targetId: " + targetId);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
	public void notifyLike(int userId, int targetId, boolean superLike) {

		// 좋아요를 누른 사람 정보
		// 받는 사람이 무료 회원이고 일반 좋아요면 가려져서 온다 (슈퍼 좋아요는 그대로)
		UserResponse likeUser = userService.getReceivedLikeCard(targetId, userId, superLike);

		if (likeUser == null) {
			log.warn("Like notification skipped - 없는 회원: " + userId);
			return;
		}

		// 좋아요를 받은 사람에게 실시간 좋아요 이벤트 전송 (/user/queue/like)
		messagingTemplate.convertAndSendToUser(
				String.valueOf(targetId),
				StompDestinations.LIKE,
				likeUser
		);

		// 알림
		String message = superLike
				? likeUser.getNickname() + "님이 회원님에게 슈퍼 좋아요를 보냈습니다."
				: likeUser.getNickname() + "님이 회원님을 좋아합니다.";

		NotificationMessage notification = new NotificationMessage(
				NotificationType.LIKE,
				userId,
				likeUser.getNickname(),
				message,
				null   // 좋아요는 채팅방이 없으므로 목록으로 보낸다
		);

		messagingTemplate.convertAndSendToUser(
				String.valueOf(targetId),
				StompDestinations.NOTIFICATION,
				notification
		);

		log.info("Like notification sent - userId: " + userId + ", targetId: " + targetId + ", superLike: " + superLike);
	}
}
