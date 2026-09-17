package com.mingle.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 회원 프로필 (mingle_user_profiles, 회원과 1:1)
 * 닉네임 / 생년월일 / 성별 / 지역은 가입할 때 받는 필수 항목이다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileVO {

    private int userId;
    private String nickname;
    private Date birthDate;
    private String gender;
    private String region;
    private String job;
    private Integer height;   // 입력하지 않으면 null
}
