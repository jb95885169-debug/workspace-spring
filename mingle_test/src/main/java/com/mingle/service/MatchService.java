package com.mingle.service;

import com.mingle.dto.ChatRoomResponse;
import com.mingle.dto.MatchChatResponse;
import com.mingle.dto.PageResponse;

public interface MatchService {

    /**
     * 채팅방 참여자인지 확인
     * 없는 매칭이거나 참여자가 아니면 MatchNotFoundException (404)
     */
    void checkParticipant(int matchId, int userId);

    /**
     * 대화 내용을 볼 수 있는지 확인 (ACTIVE 또는 상대가 취소한 매칭)
     * 참여자가 아니면 404, 내가 취소한 매칭이면 MatchAlreadyCancelledException (409)
     */
    void checkReadableParticipant(int matchId, int userId);

    /**
     * 메시지를 보낼 수 있는지 확인 (참여자 + ACTIVE)
     * 참여자가 아니면 404, ACTIVE가 아니면 MatchAlreadyCancelledException (409)
     */
    void checkActiveParticipant(int matchId, int userId);

    /**
     * 채팅방 정보 (볼 수 없는 채팅방이면 checkReadableParticipant와 같은 예외)
     */
    ChatRoomResponse getChatRoom(int matchId, int userId);

    /**
     * 매칭 나가기
     * - ACTIVE 매칭: CANCELLED (내 목록에서 사라짐, 커밋 후 상대에게 알림 → 상대는 대화 내용만 볼 수 있음)
     * - 상대가 먼저 나간 매칭: DESTROYED (내 목록에서도 사라짐)
     * - 이미 내가 나갔거나 둘 다 나간 매칭: MatchAlreadyCancelledException (409)
     */
    void leaveMatch(int matchId, int userId);

    /** 채팅 목록을 한 번에 가져오는 개수 (아래로 스크롤하면 다음 페이지) */
    int CHAT_PAGE_SIZE = 20;

    // 내 채팅 목록 (ACTIVE + 상대가 먼저 나간 매칭, 최근 대화 순, page는 1부터)
    PageResponse<MatchChatResponse> getMatches(int userId, int page);

    // 채팅 목록 항목 한 건 (userId 기준, 실시간 목록 갱신용)
    MatchChatResponse getMatchChat(int matchId, int userId);
}
