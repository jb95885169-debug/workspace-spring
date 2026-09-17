package com.mingle.dto;

import java.util.Date;
import java.util.List;

import com.mingle.vo.UserPhotoVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 내 프로필 (프로필 수정 화면)
 *
 * 이메일 / 생년월일 / 성별은 보여 주기만 하고 바꾸지 않는다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

    private int userId;
    private String email;
    private String phone;

    private String nickname;
    private Date birthDate;
    private String gender;
    private String region;
    private String job;
    private Integer height;

    /** 고른 관심사 ID (체크박스 표시용) */
    private List<Integer> interestIds;

    /** 등록한 사진 (대표 사진이 먼저) */
    private List<UserPhotoVO> photos;
}
