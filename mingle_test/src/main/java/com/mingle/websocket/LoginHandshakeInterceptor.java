package com.mingle.websocket;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

/**
 * WebSocket 접속 시 로그인 확인
 * HTTP 세션에 userId가 없으면 연결을 거부(401)하고,
 * 있으면 UserPrincipalHandshakeHandler가 쓸 수 있게 attributes에 넣어 둔다.
 */
public class LoginHandshakeInterceptor implements HandshakeInterceptor {

    /** security.CustomLoginSuccessHandler가 HTTP 세션에 넣는 로그인 회원 ID 키 */
    public static final String USER_ID = "userId";

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {

        Integer userId = null;

        if (request instanceof ServletServerHttpRequest) {
            HttpSession session = ((ServletServerHttpRequest) request).getServletRequest().getSession(false);
            Object value = (session != null) ? session.getAttribute(USER_ID) : null;

            if (value instanceof Integer) {
                userId = (Integer) value;
            }
        }

        if (userId == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        attributes.put(USER_ID, userId);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
    }
}
