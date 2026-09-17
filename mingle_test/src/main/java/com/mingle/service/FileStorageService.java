package com.mingle.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mingle.type.UploadDir;
import com.mingle.type.UploadFileType;
import com.mingle.util.StoredFile;

/**
 * 업로드 파일 저장 (프로필 사진 / 채팅 첨부 / 피드 첨부 공용)
 *
 * 두 가지 방식이 있다.
 * - 바로 저장 : 올리는 즉시 확정되는 경우 (채팅 첨부)
 * - temp 저장 : 폼을 제출해야 확정되는 경우 (가입, 프로필 수정, 피드 작성)
 *               → saveTemp로 두었다가 moveFromTemp로 옮긴다
 */
public interface FileStorageService {

    /** 올리는 즉시 확정되는 파일 저장 */
    StoredFile save(MultipartFile file, UploadDir dir, List<UploadFileType> allowed);

    /** 확정 전 임시 저장 (TEMP 폴더) */
    StoredFile saveTemp(MultipartFile file, List<UploadFileType> allowed);

    /**
     * temp에 둔 파일을 실제 폴더로 옮긴다
     * 저장된 이름 형식이 아니거나 파일이 없으면 IllegalArgumentException
     *
     * @return 화면에서 쓰는 주소 (/uploads/...)
     */
    String moveFromTemp(String tempFileName, UploadDir dir);

    /** 파일 삭제 (실패해도 예외를 던지지 않고 로그만 남긴다) */
    void delete(String url);

    /** 이 서비스가 만든 이름 형식인지 (경로 조작 차단) */
    boolean isStoredFileName(String fileName);

    /** 폴더 안에서 지정한 시간보다 오래된 파일 이름 (정리 작업용) */
    List<String> findOlderThan(UploadDir dir, long hours);
}
