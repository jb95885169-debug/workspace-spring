package com.mingle.util;

import com.mingle.type.UploadFileType;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 저장된 파일 한 개 (FileStorageService의 결과)
 *
 * fileName : 저장된 이름 (temp에 둔 파일을 나중에 옮길 때 쓴다)
 * url      : 화면에서 쓰는 주소 (/uploads/...)
 * type     : 사진인지 동영상인지 (채팅 메시지 종류를 정할 때 쓴다)
 */
@Data
@AllArgsConstructor
public class StoredFile {

    private final String fileName;
    private final String url;
    private final UploadFileType type;
}
