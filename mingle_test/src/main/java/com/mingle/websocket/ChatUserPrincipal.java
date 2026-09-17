package com.mingle.websocket;

import java.security.Principal;

/**
 * WebSocket 연결의 로그인 회원
 * getName()이 convertAndSendToUser()의 대상 이름이 된다.
 */
public class ChatUserPrincipal implements Principal {

    private final int userId;

    public ChatUserPrincipal(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public String getName() {
        return String.valueOf(userId);
    }

    @Override
    public String toString() {
        return "ChatUserPrincipal[userId=" + userId + "]";
    }
}
