package com.mingle.service;

import com.mingle.dto.PasswordFindRequest;
import com.mingle.dto.SignupRequest;

/**
 * 인증 (회원가입 / 비밀번호 찾기)
 *
 * 로그인과 로그아웃은 Spring Security가 처리한다.
 * (security-context.xml, security.CustomUserDetailService)
 */
public interface AuthService {

    /**
     * 회원가입 (회원 → 권한 → 프로필 → 관심사 → 사진을 한 트랜잭션)
     * 사진은 미리 temp에 올려 둔 임시 파일명으로 받는다 (SignupRequest.tempFileNames)
     * 잘못된 입력이나 중복이면 IllegalArgumentException
     *
     * @return 새로 만들어진 회원 ID
     */
    int signup(SignupRequest request);

    /**
     * 비밀번호 찾기 (이메일 + 닉네임으로 본인 확인 후 임시 비밀번호 발급)
     * 일치하는 회원이 없으면 IllegalArgumentException
     *
     * @return 발급된 임시 비밀번호 (화면에 한 번만 보여 준다)
     */
    String findPassword(PasswordFindRequest request);
}
