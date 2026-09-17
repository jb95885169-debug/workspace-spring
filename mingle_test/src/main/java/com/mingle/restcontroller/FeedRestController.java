package com.mingle.restcontroller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mingle.dto.CommentRequest;
import com.mingle.dto.CommentResponse;
import com.mingle.dto.FeedCreateRequest;
import com.mingle.dto.FeedResponse;
import com.mingle.dto.FeedUpdateRequest;
import com.mingle.dto.PageResponse;
import com.mingle.security.LoginUserId;
import com.mingle.service.FeedService;

/**
 * 피드 (글 / 좋아요 / 댓글)
 * 사진은 /api/uploads/temp로 먼저 올리고 임시 파일명만 보낸다.
 */
@RestController
@RequestMapping("/api/feeds")
public class FeedRestController {

    @Autowired
    private FeedService feedService;


    // 피드 목록 (FeedService.FEED_PAGE_SIZE개씩, 아래로 스크롤하면 다음 page)
    @GetMapping
    public ResponseEntity<PageResponse<FeedResponse>> getFeeds(
            @RequestParam(defaultValue = "1") int page,
            @LoginUserId int userId) {

        return ResponseEntity.ok(feedService.getFeeds(userId, page));
    }


    // 피드 작성
    @PostMapping
    public ResponseEntity<FeedResponse> createFeed(
            @RequestBody FeedCreateRequest request,
            @LoginUserId int userId) {

        return ResponseEntity.ok(feedService.createFeed(userId, request));
    }


    // 피드 수정 (본인 글만, 사진 교체 / 제거 포함)
    @PutMapping("/{feedId}")
    public ResponseEntity<FeedResponse> updateFeed(
            @PathVariable int feedId,
            @RequestBody FeedUpdateRequest request,
            @LoginUserId int userId) {

        return ResponseEntity.ok(feedService.updateFeed(userId, feedId, request));
    }


    // 피드 삭제 (본인 글만, 관리자는 누구 글이든)
    @DeleteMapping("/{feedId}")
    public ResponseEntity<Void> deleteFeed(
            @PathVariable int feedId,
            @LoginUserId int userId,
            HttpServletRequest request) {

        feedService.deleteFeed(userId, feedId, isAdmin(request));

        return ResponseEntity.noContent().build();
    }


    // 좋아요 켜고 끄기 → 바뀐 피드
    @PostMapping("/{feedId}/like")
    public ResponseEntity<FeedResponse> toggleLike(
            @PathVariable int feedId,
            @LoginUserId int userId) {

        return ResponseEntity.ok(feedService.toggleLike(userId, feedId));
    }


    /* ================= 댓글 ================= */

    @GetMapping("/{feedId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable int feedId) {

        return ResponseEntity.ok(feedService.getComments(feedId));
    }


    @PostMapping("/{feedId}/comments")
    public ResponseEntity<List<CommentResponse>> addComment(
            @PathVariable int feedId,
            @RequestBody CommentRequest request,
            @LoginUserId int userId) {

        feedService.addComment(userId, feedId, request);

        // 방금 쓴 댓글까지 포함한 목록을 바로 돌려준다
        return ResponseEntity.ok(feedService.getComments(feedId));
    }


    // 댓글 수정 (본인 댓글만) → 갱신된 목록
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<List<CommentResponse>> updateComment(
            @PathVariable int commentId,
            @RequestBody CommentRequest request,
            @LoginUserId int userId) {

        return ResponseEntity.ok(feedService.updateComment(userId, commentId, request));
    }


    // 댓글 삭제 (본인 댓글만, 관리자는 누구 댓글이든)
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable int commentId,
            @LoginUserId int userId,
            HttpServletRequest request) {

        feedService.deleteComment(userId, commentId, isAdmin(request));

        return ResponseEntity.noContent().build();
    }


    /**
     * 관리자인지 (mingle_auth의 ADMIN → Security에서 ROLE_ADMIN)
     * isUserInRole은 ROLE_ 접두사를 알아서 붙여 비교한다.
     */
    private boolean isAdmin(HttpServletRequest request) {
        return request.isUserInRole("ADMIN");
    }


    /** 잘못된 입력 / 없는 글 / 남의 글·댓글 */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    }
}
