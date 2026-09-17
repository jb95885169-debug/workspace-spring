package com.mingle.service;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mingle.dto.ChatMessageResponse;
import com.mingle.dto.ChatMessageSendRequest;
import com.mingle.dto.ChatReadEvent;
import com.mingle.dto.MatchChatResponse;
import com.mingle.dto.NotificationMessage;
import com.mingle.mapper.ChatMessageMapper;
import com.mingle.mapper.MatchMapper;
import com.mingle.type.NotificationType;
import com.mingle.util.TransactionUtils;
import com.mingle.vo.ChatMessageVO;
import com.mingle.websocket.StompDestinations;

@Service
public class ChatMessageServiceImpl implements ChatMessageService{

	/** 텍스트 메시지 최대 길이 */
	private static final int MAX_CONTENT_LENGTH = 1000;

	/** ChatFileServiceImpl이 발급하는 파일 URL 형식 (/uploads/chat/{UUID 32자리}.{확장자}) */
	private static final Pattern CHAT_FILE_URL =
			Pattern.compile("^/uploads/chat/[0-9a-f]{32}\\.[a-z0-9]{2,5}$");

	@Autowired
	private ChatMessageMapper chatMessageMapper;

	@Autowired
	private MatchMapper matchMapper;

	@Autowired
	private MatchService matchService;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessages(int userId, int matchId, int lastMessageId) {

		// ACTIVE 또는 상대가 먼저 나간 채팅방 (아니면 MatchNotFoundException / MatchAlreadyCancelledException)
		matchService.checkReadableParticipant(matchId, userId);

        return chatMessageMapper.selectMessages(matchId, lastMessageId, MESSAGE_PAGE_SIZE);
    }

	@Override
	@Transactional
	public ChatMessageResponse sendMessage(int senderId, ChatMessageSendRequest request) {

		if (request == null) {
			throw new IllegalArgumentException("메시지가 비어 있습니다.");
		}

		int matchId = request.getMatchId();

		// 참여 중인 ACTIVE 채팅방만 (아니면 MatchNotFoundException / MatchAlreadyCancelledException)
		matchService.checkActiveParticipant(matchId, senderId);

		// 저장할 메시지 (보낸 사람, 읽음 여부, 시각은 서버가 채운다)
		ChatMessageVO message = new ChatMessageVO();
		message.setMatchId(matchId);
		message.setSenderId(senderId);
		message.setIsRead(0);
		message.setCreatedAt(new Date());
		fillContent(message, request);

		chatMessageMapper.insertMessage(message);

		// 채팅 목록용 마지막 메시지 갱신 (INSERT와 같은 트랜잭션이라 둘 중 하나만 반영되는 일이 없음)
		matchMapper.updateLastMessage(matchId, toPreview(message));

		// 참여자 확인을 통과했으므로 매칭이 존재해 null이 나오지 않는다
		int receiverId = matchMapper.selectPartnerId(matchId, senderId);

		// 브로드캐스트할 데이터는 트랜잭션 안에서 미리 조회
		// (커밋 후 콜백에서 매퍼를 호출하면 MyBatis 세션이 스레드에 남을 수 있음)
		ChatMessageResponse response = ChatMessageResponse.from(message);
		MatchChatResponse senderChat = matchMapper.selectMatchChat(matchId, senderId);
		MatchChatResponse receiverChat = matchMapper.selectMatchChat(matchId, receiverId);

		// 받는 사람에게 보여 줄 토스트 (receiverChat의 nickname이 보낸 사람 = 상대)
		NotificationMessage notification = (receiverChat == null) ? null
				: new NotificationMessage(
						NotificationType.CHAT,
						senderId,
						receiverChat.getNickname(),
						toPreview(message),
						matchId);

		TransactionUtils.afterCommit(() -> {
			// 채팅 메시지는 두 참여자에게 각각 보낸다 (화면에서 matchId로 현재 채팅방인지 확인)
			sendToUser(senderId, StompDestinations.CHAT, response);
			sendToUser(receiverId, StompDestinations.CHAT, response);
			sendToUser(senderId, StompDestinations.CHAT_LIST, senderChat);
			sendToUser(receiverId, StompDestinations.CHAT_LIST, receiverChat);

			// 받는 사람이 그 채팅방을 보고 있으면 화면에서 알아서 무시한다
			sendToUser(receiverId, StompDestinations.NOTIFICATION, notification);
		});

		return response;
	}

	@Override
	@Transactional
	public int readMessages(int userId, int matchId) {

		// 참여자만 (취소된 매칭이어도 읽음 처리는 허용)
		matchService.checkParticipant(matchId, userId);

		int updated = chatMessageMapper.updateMessagesRead(matchId, userId);

		if (updated > 0) {
			// 읽힌 메시지를 보낸 상대에게만 알림 ("1" 표시 제거용)
			int partnerId = matchMapper.selectPartnerId(matchId, userId);
			ChatReadEvent readEvent = new ChatReadEvent(matchId, userId);
			TransactionUtils.afterCommit(() -> sendToUser(partnerId, StompDestinations.CHAT_READ, readEvent));
		}
		return updated;
	}

	/**
	 * 메시지 유형별 내용 검증 후 채우기
	 */
	private void fillContent(ChatMessageVO message, ChatMessageSendRequest request) {

		String messageType = (request.getMessageType() == null) ? "TEXT" : request.getMessageType();

		switch (messageType) {
		case "TEXT":
			String content = (request.getContent() == null) ? "" : request.getContent().trim();
			if (content.isEmpty()) {
				throw new IllegalArgumentException("메시지 내용을 입력해 주세요.");
			}
			if (content.length() > MAX_CONTENT_LENGTH) {
				throw new IllegalArgumentException("메시지는 " + MAX_CONTENT_LENGTH + "자까지 보낼 수 있습니다.");
			}
			message.setContent(content);
			break;

		case "IMAGE":
		case "VIDEO":
			// 업로드 API가 발급한 경로만 허용 (외부 URL, 임의 경로 차단)
			String fileUrl = request.getFileUrl();
			if (fileUrl == null || !CHAT_FILE_URL.matcher(fileUrl).matches()) {
				throw new IllegalArgumentException("잘못된 파일 주소입니다.");
			}
			message.setFileUrl(fileUrl);
			break;

		default:
			throw new IllegalArgumentException("지원하지 않는 메시지 형식입니다.");
		}

		message.setMessageType(messageType);
	}

	/** 채팅 목록 미리보기 문구 (텍스트는 내용 그대로, 사진/동영상은 종류) */
	private String toPreview(ChatMessageVO message) {

		switch (message.getMessageType()) {
		case "IMAGE":
			return "사진";
		case "VIDEO":
			return "동영상";
		default:
			return message.getContent();
		}
	}

	/** 해당 회원의 모든 WebSocket 연결로 전송 (구독 주소: /user + destination) */
	private void sendToUser(int userId, String destination, Object payload) {
		if (payload != null) {
			messagingTemplate.convertAndSendToUser(String.valueOf(userId), destination, payload);
		}
	}
}
