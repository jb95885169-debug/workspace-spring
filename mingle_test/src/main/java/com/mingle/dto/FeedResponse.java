package com.mingle.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 피드 응답 (목록 / 상세 공통)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedResponse {

	private int id;
	private int userId;        // 작성자
	private String nickname;   // 작성자 닉네임
	private String title;
	private String category;
	private String content;
	private String imageUrl;
	private Date createdAt;
	private Date updatedAt;

	private int likeCount;     // 전체 좋아요 수
	private boolean liked;     // 조회한 회원이 좋아요했는지
	private int commentCount;  // 댓글 수
}
