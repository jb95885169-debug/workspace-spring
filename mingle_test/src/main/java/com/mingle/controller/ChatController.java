package com.mingle.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/chat")
public class ChatController {

    // 채팅 목록 JSP
    @GetMapping("/list")
    public String chatListPage() {
        return "chat/chatList";
    }

    // 채팅방 JSP
    @GetMapping("/room")
    public String chatRoomPage(
            @RequestParam int matchId) {

        return "chat/chatRoom";
    }
}