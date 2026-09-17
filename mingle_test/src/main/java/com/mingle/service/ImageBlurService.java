package com.mingle.service;

/**
 * 흐린 사진 만들기
 *
 * 무료 회원에게 받은 좋아요를 보여 줄 때 쓴다.
 * 화면에서 CSS로만 흐리게 하면 개발자 도구로 원본을 볼 수 있으므로,
 * 아주 작게 줄인 복사본을 따로 만들어 그 주소만 내려보낸다.
 */
public interface ImageBlurService {

    /**
     * 원본 사진의 흐린 복사본 주소 (/uploads/blur/...)
     * 한 번 만든 뒤에는 그대로 다시 쓴다.
     *
     * @param photoUrl 원본 주소 (/uploads/profile/...), null이면 null을 돌려준다
     * @return 흐린 사진 주소, 만들지 못하면 null
     */
    String getBlurredUrl(String photoUrl);

    /** 원본을 지울 때 흐린 복사본도 함께 지운다 */
    void delete(String photoUrl);
}
