package com.mingle.websocket;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import com.mingle.dto.ChatMessageSendRequest;
import com.mingle.dto.ChatReadRequest;
import com.mingle.exception.MatchAlreadyCancelledException;
import com.mingle.exception.MatchNotFoundException;
import com.mingle.exception.SubscriptionRequiredException;
import com.mingle.service.ChatMessageService;

import lombok.extern.log4j.Log4j;

/**
 * STOMP 채팅 메시지 처리 (/app/chat/send, /app/chat/read)
 *
 * - 보낸 사람 / 읽은 사람은 payload가 아니라 연결의 Principal(로그인 회원)로 판단한다.
 * - 저장, 권한 확인, 전송은 ChatMessageService가 담당한다.
 * - @MessageMapping은 WebSocketConfig와 같은 root 컨텍스트에 있어야 동작하므로
 *   controller 패키지(servlet-context)와 분리해서 root-context가 스캔한다.
 */
@Controller
@Log4j
public class ChatMessageController {

    @Autowired
    private ChatMessageService chatMessageService;

    @MessageMapping("/chat/send")
    public void sendMessage(@Payload ChatMessageSendRequest message,
                            SimpMessageHeaderAccessor headerAccessor) {

        chatMessageService.sendMessage(getLoginUserId(headerAccessor), message);
    }

    @MessageMapping("/chat/read")
    public void readMessages(@Payload ChatReadRequest read,
                            SimpMessageHeaderAccessor headerAccessor) {

        chatMessageService.readMessages(getLoginUserId(headerAccessor), read.getMatchId());
    }

    /**
     * 처리 실패를 요청한 연결(탭)에만 알림 → /user/queue/errors, 본문 { "message": "..." }
     */
    @MessageExceptionHandler
    @SendToUser(destinations = StompDestinations.ERRORS, broadcast = false)
    public Map<String, String> handleException(RuntimeException e) {

        // 서비스가 사용자에게 보여줄 문구로 던지는 예외만 그대로 전달하고, 나머지는 감춘다
        boolean expected = e instanceof IllegalArgumentException
                || e instanceof IllegalStateException
                || e instanceof MatchNotFoundException
                || e instanceof MatchAlreadyCancelledException
                || e instanceof SubscriptionRequiredException;

        if (expected) {
            log.warn("채팅 처리 거부: " + e.getMessage());
            return Collections.singletonMap("message", e.getMessage());
        }

        log.error("채팅 처리 오류", e);
        return Collections.singletonMap("message", "메시지 처리 중 오류가 발생했습니다.");
    }

    private int getLoginUserId(SimpMessageHeaderAccessor headerAccessor) {

        Principal user = headerAccessor.getUser();

        if (!(user instanceof ChatUserPrincipal)) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return ((ChatUserPrincipal) user).getUserId();
    }
}
