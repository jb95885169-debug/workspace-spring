package com.mingle.service;

import com.mingle.dto.ProfileResponse;
import com.mingle.dto.ProfileUpdateRequest;

/**
 * 내 프로필 (프로필 수정 화면)
 * 사진 등록 / 삭제 / 대표 지정은 ProfilePhotoService가 담당한다.
 */
public interface ProfileService {

    /* DDL의 컬럼 길이와 같아야 함 (넘기면 ORA-12899) */
    int MAX_NICKNAME_LENGTH = 20;   // mingle_user_profiles.nickname VARCHAR2(20 CHAR)
    int MAX_PHONE_LENGTH = 20;      // mingle_users.phone           VARCHAR2(20)
    int MAX_REGION_LENGTH = 50;     // mingle_user_profiles.region  VARCHAR2(50 CHAR)
    int MAX_JOB_LENGTH = 50;        // mingle_user_profiles.job     VARCHAR2(50 CHAR)


    /** 내 프로필 (계정 정보 + 관심사 + 사진), 프로필이 없으면 IllegalStateException */
    ProfileResponse getMyProfile(int userId);

    /**
     * 프로필 수정 (닉네임 / 휴대폰 / 지역 / 직업 / 키 / 관심사 + 새 사진)
     * 잘못된 입력이나 닉네임 중복이면 IllegalArgumentException
     */
    void updateProfile(int userId, ProfileUpdateRequest request);
}
