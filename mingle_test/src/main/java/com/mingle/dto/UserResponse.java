package com.mingle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 회원 카드 (추천 목록 / 회원 상세 / 받은 좋아요)
 * 필드 이름은 MatchChatResponse와 맞춘다 (userId, photoUrl)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private int userId;
    private String nickname;
    private String gender;
    private Integer age;
    private Integer height;
    private String job;
    private String region;
    private String introduction;
    private String interests;
    private String photoUrl;     // 대표 사진

    // 이 회원이 나에게 슈퍼 좋아요를 보냈는지 (추천 카드 / 받은 좋아요 목록에서 맨 앞 + 파란 표시)
    private boolean superLike;

    /**
     * 가려진 카드인지 (무료 회원이 보는 받은 좋아요 목록)
     * true면 닉네임이 가려져 있고, 화면은 사진을 흐리게 보여 준다.
     */
    private boolean locked;
}
