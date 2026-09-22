package com.mingle.security;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.mingle.mapper.UserMapper;
import com.mingle.vo.UserVO;

/**
 * 로그인 시 Spring Security가 회원을 찾는 곳 (security-context.xml에 빈 등록)
 *
 * 비밀번호 대조는 Security가 BCryptPasswordEncoder로 한다.
 * mingle_auth의 role은 USER / ADMIN이라 hasRole('USER')와 맞추려면 ROLE_ 접두사가 필요하다.
 */
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String email) {

        UserVO user = userMapper.selectUserByEmail(normalizeEmail(email));

        if (user == null) {
            // 없는 이메일과 틀린 비밀번호를 구분해서 알려주면 가입 여부를 확인하는 데 악용될 수 있다.
            // 화면에는 두 경우 모두 같은 문구가 나간다 (authentication-failure-url).
            throw new UsernameNotFoundException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        boolean active = "ACTIVE".equals(user.getStatus())
            || ("BANNED".equals(user.getStatus())
                && user.getSuspendedUntil() != null
                && user.getSuspendedUntil().before(new Date()));

        if (active && "BANNED".equals(user.getStatus())) {
            userMapper.updateUserStatus(user.getId(), "ACTIVE");
        }

        return new LoginUser(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
            active,
                getAuthorities(user.getId()));
    }

    /** mingle_auth의 권한 목록 (없으면 일반 회원으로 처리) */
    private List<GrantedAuthority> getAuthorities(int userId) {

        List<GrantedAuthority> authorities = new ArrayList<>();

        for (String role : userMapper.selectRoles(userId)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }

        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return authorities;
    }

    /** 가입할 때와 같은 형태로 맞춰서 조회 (대소문자 / 공백 차이) */
    private String normalizeEmail(String email) {
        return (email == null) ? null : email.trim().toLowerCase();
    }
}
