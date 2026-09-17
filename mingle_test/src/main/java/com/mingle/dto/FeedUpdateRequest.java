package com.mingle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 피드 수정 요청 (PUT /api/feeds/{feedId})
 *
 * 사진은 세 가지 경우가 있다.
 * - tempFileName이 있으면 : 새 사진으로 교체 (옛 파일은 지운다)
 * - removeImage가 true면  : 사진을 뗀다
 * - 둘 다 없으면          : 지금 사진 그대로
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedUpdateRequest {

    private String title;
    private String category;
    private String content;

    private String tempFileName;
    private boolean removeImage;
}
