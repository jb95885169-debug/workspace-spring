package com.mingle.type;

/**
 * 업로드 폴더
 *
 * 실제 경로와 화면에서 쓰는 URL을 한곳에서 정한다.
 * servlet-context.xml의 /uploads/** 매핑과 이름이 같아야 한다.
 */
public enum UploadDir {

    /** 저장이 확정되기 전에 잠시 두는 곳 (가입 / 프로필 수정 / 피드 작성) */
    TEMP("temp"),

    /** 채팅 첨부 (보내는 즉시 확정되므로 temp를 거치지 않는다) */
    CHAT("chat"),

    /** 프로필 사진 */
    PROFILE("profile"),

    /** 피드 첨부 */
    FEED("feed"),

    /** 무료 회원에게 보여 줄 흐린 사진 (원본에서 만들어 둔 복사본) */
    BLUR("blur");

    /** 모든 업로드의 기준 경로 */
    public static final String BASE_DIR = "C:/upload/";

    /** 화면에서 쓰는 기준 URL (servlet-context.xml의 resources 매핑) */
    public static final String BASE_URL = "/uploads/";

    private final String folder;

    UploadDir(String folder) {
        this.folder = folder;
    }

    /** 저장 경로 (예: C:/upload/profile/) */
    public String getDir() {
        return BASE_DIR + folder + "/";
    }

    /** 화면 주소 (예: /uploads/profile/) */
    public String getUrl() {
        return BASE_URL + folder + "/";
    }

    /** 이 폴더의 파일 주소인지 (예: /uploads/profile/abc.jpg) */
    public boolean matches(String url) {
        return url != null && url.startsWith(getUrl());
    }
}
