package com.mingle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 채팅 파일 업로드 응답 (받은 url, messageType으로 /app/chat/send 전송)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatFileUploadResponse {

    private String url;          // /uploads/chat/{파일명}
    private String messageType;  // IMAGE / VIDEO
}
