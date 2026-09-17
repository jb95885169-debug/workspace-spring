package com.mingle.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * mingle_feeds 테이블
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedVO {

	private int id;
	private int userId;       // 작성자
	private String title;
	private String category;  // NORMAL / PLACE / REVIEW
	private String content;
	private String imageUrl;  // /uploads/feed/... (없으면 null)
	private Date createdAt;
	private Date updatedAt;
}
