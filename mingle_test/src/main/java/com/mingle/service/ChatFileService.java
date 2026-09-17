package com.mingle.service;

import org.springframework.web.multipart.MultipartFile;

import com.mingle.dto.ChatFileUploadResponse;

public interface ChatFileService {

    /**
     * 채팅 사진 / 동영상 저장 (참여 중인 ACTIVE 매칭만)
     */
    ChatFileUploadResponse upload(int userId, int matchId, MultipartFile file);
}
