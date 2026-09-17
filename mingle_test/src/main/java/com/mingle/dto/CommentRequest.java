package com.mingle.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 댓글 작성 / 수정 요청
 * POST /api/feeds/{feedId}/comments, PUT /api/feeds/comments/{commentId}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    private String content;
}
