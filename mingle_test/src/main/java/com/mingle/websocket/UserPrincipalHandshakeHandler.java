package com.mingle.websocket;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

/**
 * WebSocket 연결의 Principal을 로그인 회원으로 지정
 * (LoginHandshakeInterceptor를 통과한 연결만 여기까지 온다)
 *
 * Spring Security를 도입하면 기본 DefaultHandshakeHandler가
 * request.getUserPrincipal()을 쓰므로 이 클래스는 지워도 된다.
 */
public class UserPrincipalHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {

        Integer userId = (Integer) attributes.get(LoginHandshakeInterceptor.USER_ID);

        return new ChatUserPrincipal(userId);
    }
}
