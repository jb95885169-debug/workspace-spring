package com.mingle.dto;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 회원가입 요청 (POST /signup 폼)
 *
 * 비밀번호는 평문으로 들어오고 AuthService가 BCrypt로 바꿔 저장한다.
 * 닉네임 / 생년월일 / 성별은 mingle_user_profiles에서 NOT NULL이라 가입할 때 함께 받는다.
 * 사진은 multipart라 이 DTO가 아니라 컨트롤러에서 따로 받는다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    /* 계정 */
    private String email;
    private String password;
    private String passwordConfirm;
    private String phone;

    /* 프로필 (필수) */
    private String nickname;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    private String gender;   // MALE / FEMALE
    private String region;

    /* 관심사 (mingle_interests의 ID, 체크박스로 고름) */
    private List<Integer> interestIds;

    /* 새로 올린 사진의 임시 파일명 (temp 폴더에 올라와 있는 것) */
    private List<String> tempFileNames;

    /* 프로필 (선택, 비워두면 저장하지 않음) */
    private String job;
    private Integer height;
}
