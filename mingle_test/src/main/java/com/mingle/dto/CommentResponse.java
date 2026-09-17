package com.mingle.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 피드 댓글 (GET /api/feeds/{feedId}/comments)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private int id;
    private int userId;        // 작성자 (화면에서 내 댓글인지 판단)
    private String nickname;
    private String content;
    private Date createdAt;
}
