package com.mingle.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import lombok.extern.log4j.Log4j;

/**
 * 클라이언트가 쓸 수 있는 STOMP 주소 제한
 *
 * - SUBSCRIBE는 /user/queue/** 만 허용
 *   (/queue/chat-user0 같은 실제 브로커 주소를 직접 구독하면 남의 메시지를 받을 수 있음.
 *    톰캣 WebSocket 세션 ID는 순서대로 매겨져 추측이 쉽다)
 * - SEND는 /app/** 만 허용
 *   (브로커 주소로 직접 SEND하면 서버를 거치지 않고 남에게 메시지를 보낼 수 있음)
 *
 * 허용하지 않은 요청은 무시한다.
 */
@Log4j
public class StompDestinationInterceptor implements ChannelInterceptor {

    private static final String USER_QUEUE_PREFIX = "/user/queue/";
    private static final String APP_PREFIX = "/app/";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        String allowedPrefix;
        if (StompCommand.SUBSCRIBE.equals(command)) {
            allowedPrefix = USER_QUEUE_PREFIX;
        } else if (StompCommand.SEND.equals(command)) {
            allowedPrefix = APP_PREFIX;
        } else {
            return message;
        }

        String destination = accessor.getDestination();

        if (accessor.getUser() != null && destination != null && destination.startsWith(allowedPrefix)) {
            return message;
        }

        log.warn("STOMP 요청 거부 - " + command + " " + destination + ", user: " + accessor.getUser());
        return null;
    }
}
