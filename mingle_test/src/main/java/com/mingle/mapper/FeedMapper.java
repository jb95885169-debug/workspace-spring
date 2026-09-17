package com.mingle.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mingle.dto.CommentResponse;
import com.mingle.dto.FeedResponse;
import com.mingle.vo.FeedVO;

@Mapper
public interface FeedMapper {

	/* ================= 피드 ================= */

	// 피드 작성 (selectKey로 feed.id가 채워짐)
	int insertFeed(FeedVO feed);

	// 피드 목록 (최신순, offset부터 size개, liked는 userId 기준)
	List<FeedResponse> selectFeeds(
			@Param("userId") int userId,
			@Param("offset") int offset,
			@Param("size") int size);

	// 전체 피드 수 (더 불러올 게 있는지 확인용)
	int countFeeds();

	// 피드 한 건 (liked는 userId 기준, 없으면 null)
	FeedResponse selectFeed(
			@Param("userId") int userId,
			@Param("feedId") int feedId);

	// 피드 수정 (본인 글만, imageUrl이 null이면 사진을 뗀다)
	int updateFeed(
			@Param("feedId") int feedId,
			@Param("userId") int userId,
			@Param("title") String title,
			@Param("category") String category,
			@Param("content") String content,
			@Param("imageUrl") String imageUrl);

	// 피드 삭제 (본인 글만, 관리자는 누구 글이든, 댓글과 좋아요는 FK의 ON DELETE CASCADE로 함께 지워짐)
	int deleteFeed(
			@Param("feedId") int feedId,
			@Param("userId") int userId,
			@Param("admin") boolean admin);

	// 이 사진을 쓰는 피드가 있는지 (schedule.UploadCleanupTask가 고아 파일을 지울 때)
	boolean existsImageUrl(String imageUrl);


	/* ================= 좋아요 ================= */

	int insertFeedLike(
			@Param("feedId") int feedId,
			@Param("userId") int userId);

	int deleteFeedLike(
			@Param("feedId") int feedId,
			@Param("userId") int userId);

	boolean existsFeedLike(
			@Param("feedId") int feedId,
			@Param("userId") int userId);


	/* ================= 댓글 ================= */

	// 댓글 목록 (오래된 순)
	List<CommentResponse> selectComments(int feedId);

	// 댓글 작성
	int insertComment(
			@Param("feedId") int feedId,
			@Param("userId") int userId,
			@Param("content") String content);

	// 댓글 수정 (본인 댓글만)
	int updateComment(
			@Param("commentId") int commentId,
			@Param("userId") int userId,
			@Param("content") String content);

	// 댓글 삭제 (본인 댓글만, 관리자는 누구 댓글이든)
	int deleteComment(
			@Param("commentId") int commentId,
			@Param("userId") int userId,
			@Param("admin") boolean admin);

	// 댓글이 달린 피드 ID (수정 / 삭제 후 목록을 다시 줄 때, 없으면 null)
	Integer selectFeedIdByComment(int commentId);
}
