package com.mingle.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mingle.dto.ChatFileUploadResponse;
import com.mingle.type.UploadDir;
import com.mingle.type.UploadFileType;
import com.mingle.util.StoredFile;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class ChatFileServiceImpl implements ChatFileService {

    @Autowired
    private MatchService matchService;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public ChatFileUploadResponse upload(int userId, int matchId, MultipartFile file) {

        // 참여 중인 ACTIVE 채팅방만 (아니면 404 / 409)
        matchService.checkActiveParticipant(matchId, userId);

        // 채팅 첨부는 올리는 즉시 메시지로 전송되므로 temp를 거치지 않는다
        StoredFile stored = fileStorageService.save(
                file,
                UploadDir.CHAT,
                Arrays.asList(UploadFileType.IMAGE, UploadFileType.VIDEO));

        log.info("채팅 파일 업로드 - matchId: " + matchId
                + ", userId: " + userId
                + ", file: " + stored.getFileName());

        // 메시지 종류는 파일 종류를 그대로 쓴다 (IMAGE / VIDEO)
        return new ChatFileUploadResponse(stored.getUrl(), stored.getType().name());
    }
}
