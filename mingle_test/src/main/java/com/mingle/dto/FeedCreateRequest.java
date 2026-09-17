package com.mingle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 피드 작성 요청 (POST /api/feeds)
 *
 * 사진은 먼저 /api/uploads/temp로 올려 두고 임시 파일명만 보낸다.
 * 작성이 끝나면 feed 폴더로 옮겨진다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedCreateRequest {

    private String title;
    private String category;      // NORMAL / PLACE / REVIEW
    private String content;
    private String tempFileName;  // 사진을 올리지 않았으면 null
}
