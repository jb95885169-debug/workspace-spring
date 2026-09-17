package com.mingle.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchChatResponse {

    private int matchId;
    private int userId;

    private String nickname;
    private String gender;

    private int age;
    private int height;

    private String job;
    private String region;

    private String interests;

    private String photoUrl;

    // 마지막 메시지 미리보기 (텍스트는 내용, 사진/동영상은 "사진"/"동영상") - mingle_matches.last_message
    private String lastMessage;
    private Date lastMessageAt;

    private int unreadCount;

    // 상대가 매칭을 취소한 대화 (대화 내용만 볼 수 있음, 목록에 "대화 종료" 표시)
    private boolean readOnly;
}