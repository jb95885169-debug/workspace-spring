package com.mingle.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.mingle.mapper.UserMapper;

import lombok.extern.log4j.Log4j;

/**
 * 로그인 성공 처리 (security-context.xml에 빈 등록)
 *
 * 이 프로젝트는 REST 컨트롤러, JSP, WebSocket 핸드셰이크가 모두
 * 세션의 "userId"를 보고 동작하므로 로그인 직후 여기에 회원 ID를 넣어 준다.
 */
@Log4j
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    /** 세션에 담는 회원 ID (websocket.LoginHandshakeInterceptor와 같은 키) */
    public static final String USER_ID = "userId";

    @Autowired
    private UserMapper userMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        Object principal = authentication.getPrincipal();

        if (principal instanceof LoginUser) {

            int userId = ((LoginUser) principal).getUserId();

            request.getSession().setAttribute(USER_ID, userId);
            userMapper.updateLastLoginAt(userId);

            log.info("로그인 성공 - userId: " + userId);
        }

        response.sendRedirect(request.getContextPath() + "/users/list");
    }
}
