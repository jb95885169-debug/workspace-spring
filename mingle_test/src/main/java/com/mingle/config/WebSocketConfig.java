package com.mingle.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.mingle.websocket.LoginHandshakeInterceptor;
import com.mingle.websocket.StompDestinationInterceptor;
import com.mingle.websocket.UserPrincipalHandshakeHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(
            MessageBrokerRegistry registry) {

        // 서버 → 클라이언트 메시지는 모두 회원별 큐로 보낸다 (StompDestinations)
        registry.enableSimpleBroker("/queue");

        registry.setApplicationDestinationPrefixes("/app");

        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(
            StompEndpointRegistry registry) {

        // 로그인한 회원만 접속, 연결의 Principal = 로그인 회원
        // setAllowedOrigins("*")는 다른 사이트가 로그인 쿠키로 접속할 수 있어 쓰지 않음 (같은 출처만 허용)
        registry.addEndpoint("/ws-chat")
                .setHandshakeHandler(new UserPrincipalHandshakeHandler())
                .addInterceptors(new LoginHandshakeInterceptor());
    }

    @Override
    public void configureClientInboundChannel(
            ChannelRegistration registration) {

        // 구독은 /user/queue/**, 전송은 /app/** 만 허용
        registration.interceptors(new StompDestinationInterceptor());
    }
}
