package com.mingle.schedule;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mingle.mapper.ChatMessageMapper;
import com.mingle.mapper.FeedMapper;
import com.mingle.service.FileStorageService;
import com.mingle.type.UploadDir;

import lombok.extern.log4j.Log4j;

/**
 * 쓰이지 않는 업로드 파일 정리
 *
 * - temp : 사진을 올려 두고 가입 / 수정을 끝내지 않으면 파일만 남는다
 * - chat : 업로드 직후 WebSocket 전송이 실패하면 메시지 없이 파일만 남는다
 *
 * 둘 다 올린 지 오래된 것만 지운다. 방금 올린 파일은 아직 저장 중일 수 있다.
 * (root-context.xml의 task:annotation-driven)
 */
@Component
@Log4j
public class UploadCleanupTask {

    /** 이 시간이 지난 파일만 정리 대상 */
    private static final long KEEP_HOURS = 24;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private FeedMapper feedMapper;


    /** 매일 새벽 3시 - temp 폴더 (옮겨지지 않은 임시 파일) */
    @Scheduled(cron = "0 0 3 * * *")
    public void cleanTempFiles() {

        List<String> oldFiles = fileStorageService.findOlderThan(UploadDir.TEMP, KEEP_HOURS);

        for (String fileName : oldFiles) {
            fileStorageService.delete(UploadDir.TEMP.getUrl() + fileName);
        }

        log.info("임시 파일 정리 - " + oldFiles.size() + "개");
    }


    /** 매일 새벽 3시 30분 - chat 폴더 (메시지로 남지 않은 첨부) */
    @Scheduled(cron = "0 30 3 * * *")
    public void cleanChatFiles() {

        List<String> oldFiles = fileStorageService.findOlderThan(UploadDir.CHAT, KEEP_HOURS);

        int deleted = 0;

        for (String fileName : oldFiles) {

            String url = UploadDir.CHAT.getUrl() + fileName;

            // 메시지로 남아 있으면 쓰이는 파일이므로 건드리지 않는다
            if (!chatMessageMapper.existsFileUrl(url)) {
                fileStorageService.delete(url);
                deleted++;
            }
        }

        log.info("채팅 첨부 정리 - " + deleted + "개 (검사 " + oldFiles.size() + "개)");
    }


    /** 매일 새벽 4시 - feed 폴더 (글로 남지 않은 사진) */
    @Scheduled(cron = "0 0 4 * * *")
    public void cleanFeedFiles() {

        List<String> oldFiles = fileStorageService.findOlderThan(UploadDir.FEED, KEEP_HOURS);

        int deleted = 0;

        for (String fileName : oldFiles) {

            String url = UploadDir.FEED.getUrl() + fileName;

            // 글에 남아 있으면 쓰이는 파일이므로 건드리지 않는다
            if (!feedMapper.existsImageUrl(url)) {
                fileStorageService.delete(url);
                deleted++;
            }
        }

        log.info("피드 사진 정리 - " + deleted + "개 (검사 " + oldFiles.size() + "개)");
    }
}
