package com.mingle.type;

import java.util.Arrays;
import java.util.List;

/**
 * 받을 수 있는 파일 종류
 *
 * html, svg 등은 브라우저에서 스크립트가 실행될 수 있어 목록에 넣지 않는다.
 * 확장자와 Content-Type을 둘 다 확인한다 (Content-Type은 클라이언트가 바꿀 수 있음).
 */
public enum UploadFileType {

    IMAGE(Arrays.asList("jpg", "jpeg", "png", "gif", "webp"), "image/"),

    VIDEO(Arrays.asList("mp4", "webm", "mov"), "video/");

    private final List<String> extensions;
    private final String contentTypePrefix;

    UploadFileType(List<String> extensions, String contentTypePrefix) {
        this.extensions = extensions;
        this.contentTypePrefix = contentTypePrefix;
    }

    public boolean matches(String extension, String contentType) {

        return extensions.contains(extension)
                && contentType != null
                && contentType.startsWith(contentTypePrefix);
    }

    /** 오류 문구용 (예: "jpg, jpeg, png, gif, webp") */
    public String getExtensionNames() {
        return String.join(", ", extensions);
    }
}
