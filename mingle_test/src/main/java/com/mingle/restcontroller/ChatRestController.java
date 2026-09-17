package com.mingle.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mingle.dto.ChatMessageResponse;
import com.mingle.dto.ChatRoomResponse;
import com.mingle.dto.MatchChatResponse;
import com.mingle.dto.PageResponse;
import com.mingle.security.LoginUserId;
import com.mingle.service.ChatMessageService;
import com.mingle.service.MatchService;

import lombok.extern.log4j.Log4j;

// 메시지 전송 / 읽음 처리는 STOMP (/app/chat/send, /app/chat/read → websocket.ChatMessageController)
// 404 / 409는 controller.advice.MatchExceptionHandler가 응답
@RestController
@RequestMapping("/api/chats")
@Log4j
public class ChatRestController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private ChatMessageService chatMessageService;


    // 채팅 목록 (MatchService.CHAT_PAGE_SIZE개씩, 아래로 스크롤하면 다음 page)
    @GetMapping
    public ResponseEntity<PageResponse<MatchChatResponse>> getChats(
            @RequestParam(defaultValue = "1") int page,
            @LoginUserId int userId) {

        return ResponseEntity.ok(matchService.getMatches(userId, page));
    }


    // 채팅방 정보 { matchId, readOnly } (상대가 취소한 매칭이면 readOnly = true)
    @GetMapping("/{matchId}")
    public ResponseEntity<ChatRoomResponse> getChatRoom(
            @PathVariable int matchId,
            @LoginUserId int userId) {

        return ResponseEntity.ok(matchService.getChatRoom(matchId, userId));
    }


    // 채팅 메시지 조회 (ChatMessageService.MESSAGE_PAGE_SIZE개씩, lastMessageId보다 이전 것)
    @GetMapping("/{matchId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable int matchId,
            @RequestParam(defaultValue = "0") int lastMessageId,
            @LoginUserId int userId) {

        return ResponseEntity.ok(
                chatMessageService.getMessages(userId, matchId, lastMessageId)
        );
    }
}
