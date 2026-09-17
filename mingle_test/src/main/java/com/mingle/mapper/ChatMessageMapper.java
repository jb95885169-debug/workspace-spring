package com.mingle.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mingle.dto.ChatMessageResponse;
import com.mingle.vo.ChatMessageVO;

@Mapper
public interface ChatMessageMapper {

    // 메시지 저장
    int insertMessage(ChatMessageVO message);

    // 메시지 limit개씩 최신순 조회 (lastMessageId보다 이전 것, 0이면 가장 최근부터)
    List<ChatMessageResponse> selectMessages(
            @Param("matchId") int matchId,
            @Param("lastMessageId") int lastMessageId,
            @Param("limit") int limit);

    // 사용 안 함 (메시지 조회는 selectMessages로 나눠서)
    List<ChatMessageResponse> selectAllMessages(int matchId);

    // 상대가 보낸 안 읽은 메시지를 읽음 처리 (userId = 읽는 회원)
    int updateMessagesRead(
            @Param("matchId") int matchId,
            @Param("userId") int userId);

    // 이 첨부 파일을 쓰는 메시지가 있는지 (schedule.UploadCleanupTask가 고아 파일을 지울 때)
    boolean existsFileUrl(String fileUrl);

    // 사용 안 함 (매칭 나가기는 상태만 바꾸고 메시지는 남겨 둠)
    int deleteMessages(@Param("matchId") int matchId);
}
