package com.mingle.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * 로그인한 회원 (Spring Security의 User + 회원 ID)
 *
 * Security는 이메일로 회원을 찾지만 이 프로젝트는 회원 ID(int)로 동작하므로
 * ID를 함께 담아 두고, CustomLoginSuccessHandler가 꺼내서 세션에 넣는다.
 */
public class LoginUser extends User {

    private static final long serialVersionUID = 1L;

    private final int userId;

    public LoginUser(int userId,
                     String email,
                     String passwordHash,
                     boolean enabled,
                     Collection<? extends GrantedAuthority> authorities) {

        // 계정 만료 / 잠금 / 비밀번호 만료는 쓰지 않으므로 모두 정상(true)
        super(email, passwordHash, enabled, true, true, true, authorities);

        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }
}
