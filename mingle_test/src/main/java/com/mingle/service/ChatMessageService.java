package com.mingle.service;

import java.util.List;

import com.mingle.dto.ChatMessageResponse;
import com.mingle.dto.ChatMessageSendRequest;

public interface ChatMessageService {

    /** 메시지 조회 한 번에 가져오는 개수 (위로 스크롤할 때마다 이만큼씩) */
    int MESSAGE_PAGE_SIZE = 20;

    /**
     * 메시지 MESSAGE_PAGE_SIZE개씩 조회 (lastMessageId보다 이전 것, 0이면 가장 최근부터)
     * 고른 뒤에는 화면에 붙이는 순서대로 오래된 것부터 돌려준다
     * 대화 내용을 볼 수 있는 채팅방만 (ACTIVE 또는 상대가 먼저 나간 매칭, 아니면 404 / 409)
     */
    List<ChatMessageResponse> getMessages(int userId, int matchId, int lastMessageId);

    /**
     * 메시지 전송 (STOMP /app/chat/send)
     * 참여자 + ACTIVE 확인 → 저장 → 커밋 후 채팅방 / 양쪽 채팅목록 브로드캐스트
     * senderId는 로그인 회원 ID
     */
    ChatMessageResponse sendMessage(int senderId, ChatMessageSendRequest request);

    /**
     * 읽음 처리 (STOMP /app/chat/read)
     * 참여자 확인 → 상대 메시지 읽음 처리 → 읽은 게 있으면 커밋 후 읽음 이벤트 브로드캐스트
     */
    int readMessages(int userId, int matchId);
}
