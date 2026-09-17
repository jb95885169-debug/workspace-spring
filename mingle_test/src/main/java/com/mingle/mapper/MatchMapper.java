package com.mingle.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mingle.dto.MatchChatResponse;
import com.mingle.vo.MatchAccessVO;
import com.mingle.vo.MatchVO;

@Mapper
public interface MatchMapper {

    int insertMatch(MatchVO match);

    boolean existsMatch(
            @Param("user1Id") int user1Id,
            @Param("user2Id") int user2Id);

    // 내 매칭(채팅) 목록 (최근 대화 순, offset부터 size개)
    List<MatchChatResponse> selectMatches(
            @Param("userId") int userId,
            @Param("offset") int offset,
            @Param("size") int size);

    // 내 채팅방 수 (더 불러올 게 있는지 확인용)
    int countMatches(int userId);

    // 채팅 목록 항목 한 건 (userId 기준, 실시간 목록 갱신용)
    MatchChatResponse selectMatchChat(
            @Param("matchId") int matchId,
            @Param("userId") int userId);

    // 채팅방 접근 권한 확인용 (없는 매칭이면 null)
    MatchAccessVO selectMatchAccess(
            @Param("matchId") int matchId,
            @Param("userId") int userId);

    // 매칭 상대 ID (참여자 확인 후에만 호출, 없는 매칭이면 예외)
    int selectPartnerId(
            @Param("matchId") int matchId,
            @Param("userId") int userId);

    // 채팅 목록용 마지막 메시지 갱신 (텍스트는 내용, 사진/동영상은 "사진"/"동영상")
    int updateLastMessage(
            @Param("matchId") int matchId,
            @Param("lastMessage") String lastMessage);

    // 매칭 나가기 1: ACTIVE → CANCELLED (먼저 나간 사람 기록)
    int updateMatchCancelled(
            @Param("matchId") int matchId,
            @Param("cancelledBy") int cancelledBy,
            @Param("cancelReason") String cancelReason);

    // 매칭 나가기 2: CANCELLED → DESTROYED (나머지 한 명도 나감)
    int updateMatchDestroyed(@Param("matchId") int matchId);
}
