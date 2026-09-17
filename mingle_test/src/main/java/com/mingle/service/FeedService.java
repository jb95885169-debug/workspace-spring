package com.mingle.service;

import java.util.Arrays;
import java.util.List;

import com.mingle.dto.CommentRequest;
import com.mingle.dto.CommentResponse;
import com.mingle.dto.FeedCreateRequest;
import com.mingle.dto.FeedResponse;
import com.mingle.dto.FeedUpdateRequest;
import com.mingle.dto.PageResponse;

/**
 * 피드 (글 / 좋아요 / 댓글)
 *
 * 사진은 먼저 temp에 올려 두고(/api/uploads/temp), 글을 저장할 때 feed 폴더로 옮긴다.
 */
public interface FeedService {

    /** 목록을 한 번에 가져오는 개수 (아래로 스크롤하면 다음 페이지) */
    int FEED_PAGE_SIZE = 10;

    /** mingle_feeds의 CHECK 제약과 같아야 함 */
    List<String> CATEGORIES = Arrays.asList("NORMAL", "PLACE", "REVIEW");

    int MAX_TITLE_LENGTH = 100;
    int MAX_CONTENT_LENGTH = 1000;
    int MAX_COMMENT_LENGTH = 500;

    /** 피드 목록 (최신순, page는 1부터) */
    PageResponse<FeedResponse> getFeeds(int userId, int page);

    /** 피드 한 건 (없으면 IllegalArgumentException) */
    FeedResponse getFeed(int userId, int feedId);

    /** 피드 작성 (사진이 있으면 temp에서 feed 폴더로 옮긴다) */
    FeedResponse createFeed(int userId, FeedCreateRequest request);

    /** 피드 수정 (본인 글만, 사진 교체 / 제거 포함) */
    FeedResponse updateFeed(int userId, int feedId, FeedUpdateRequest request);

    /**
     * 피드 삭제 (댓글·좋아요는 DB가 함께 지우고 사진 파일도 지운다)
     * 일반 회원은 본인 글만, 관리자(admin)는 누구 글이든 지울 수 있다.
     */
    void deleteFeed(int userId, int feedId, boolean admin);

    /** 좋아요 켜고 끄기 (누른 뒤의 피드를 돌려준다) */
    FeedResponse toggleLike(int userId, int feedId);

    /** 댓글 목록 (오래된 순) */
    List<CommentResponse> getComments(int feedId);

    /** 댓글 작성 */
    void addComment(int userId, int feedId, CommentRequest request);

    /**
     * 댓글 수정 (본인 댓글만)
     *
     * @return 수정한 댓글이 달린 피드의 댓글 목록
     */
    List<CommentResponse> updateComment(int userId, int commentId, CommentRequest request);

    /** 댓글 삭제 (일반 회원은 본인 댓글만, 관리자는 누구 댓글이든) */
    void deleteComment(int userId, int commentId, boolean admin);
}
