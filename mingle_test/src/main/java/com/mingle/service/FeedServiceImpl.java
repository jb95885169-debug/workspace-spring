package com.mingle.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mingle.dto.CommentRequest;
import com.mingle.dto.CommentResponse;
import com.mingle.dto.FeedCreateRequest;
import com.mingle.dto.FeedResponse;
import com.mingle.dto.FeedUpdateRequest;
import com.mingle.dto.PageResponse;
import com.mingle.mapper.FeedMapper;
import com.mingle.type.UploadDir;
import com.mingle.vo.FeedVO;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class FeedServiceImpl implements FeedService {

    @Autowired
    private FeedMapper feedMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FeedResponse> getFeeds(int userId, int page) {

        int size = FEED_PAGE_SIZE;
        int offset = PageResponse.toOffset(page, size);

        return PageResponse.of(
                feedMapper.selectFeeds(userId, offset, size),
                page,
                size,
                feedMapper.countFeeds());
    }

    @Override
    @Transactional(readOnly = true)
    public FeedResponse getFeed(int userId, int feedId) {

        FeedResponse feed = feedMapper.selectFeed(userId, feedId);

        if (feed == null) {
            throw new IllegalArgumentException("없는 글입니다.");
        }
        return feed;
    }

    @Override
    @Transactional
    public FeedResponse createFeed(int userId, FeedCreateRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("내용을 입력해 주세요.");
        }

        String title = trim(request.getTitle());
        String content = trim(request.getContent());
        String category = trim(request.getCategory());

        validate(title, content, category);

        FeedVO feed = new FeedVO();
        feed.setUserId(userId);
        feed.setTitle(title);
        feed.setCategory(category);
        feed.setContent(content);

        // 사진을 올렸으면 temp에서 feed 폴더로 옮긴다 (이름 검사도 저장소가 한다)
        String tempFileName = trim(request.getTempFileName());

        if (!tempFileName.isEmpty()) {
            feed.setImageUrl(fileStorageService.moveFromTemp(tempFileName, UploadDir.FEED));
        }

        feedMapper.insertFeed(feed);   // selectKey로 feed.id가 채워진다

        log.info("피드 작성 - userId: " + userId + ", feedId: " + feed.getId());

        return getFeed(userId, feed.getId());
    }

    @Override
    @Transactional
    public FeedResponse updateFeed(int userId, int feedId, FeedUpdateRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("수정할 내용을 입력해 주세요.");
        }

        FeedResponse feed = getFeed(userId, feedId);

        if (feed.getUserId() != userId) {
            throw new IllegalArgumentException("내가 쓴 글만 수정할 수 있습니다.");
        }

        String title = trim(request.getTitle());
        String content = trim(request.getContent());
        String category = trim(request.getCategory());

        validate(title, content, category);

        String imageUrl = feed.getImageUrl();
        String oldImageUrl = null;

        String tempFileName = trim(request.getTempFileName());

        if (!tempFileName.isEmpty()) {

            // 새 사진으로 교체 (옛 파일은 수정이 끝난 뒤에 지운다)
            imageUrl = fileStorageService.moveFromTemp(tempFileName, UploadDir.FEED);
            oldImageUrl = feed.getImageUrl();

        } else if (request.isRemoveImage()) {

            imageUrl = null;
            oldImageUrl = feed.getImageUrl();
        }

        if (feedMapper.updateFeed(feedId, userId, title, category, content, imageUrl) == 0) {
            throw new IllegalArgumentException("내가 쓴 글만 수정할 수 있습니다.");
        }

        // DB가 먼저 바뀐 뒤에 파일을 지운다 (지우고 실패하면 사진 없는 글이 된다)
        fileStorageService.delete(oldImageUrl);

        log.info("피드 수정 - userId: " + userId + ", feedId: " + feedId);

        return getFeed(userId, feedId);
    }

    @Override
    @Transactional
    public void deleteFeed(int userId, int feedId, boolean admin) {

        // 사진 경로를 먼저 읽어 둔다 (지우고 나면 알 수 없음)
        FeedResponse feed = feedMapper.selectFeed(userId, feedId);

        if (feed == null) {
            throw new IllegalArgumentException("없는 글입니다.");
        }

        // 일반 회원이 남의 글을 지우려 하면 0건 (댓글과 좋아요는 FK의 ON DELETE CASCADE로 함께 지워진다)
        if (feedMapper.deleteFeed(feedId, userId, admin) == 0) {
            throw new IllegalArgumentException("내가 쓴 글만 지울 수 있습니다.");
        }

        fileStorageService.delete(feed.getImageUrl());

        // 관리자가 남의 글을 지운 경우는 나중에 확인할 수 있게 따로 남긴다
        if (admin && feed.getUserId() != userId) {
            log.warn("관리자 피드 삭제 - adminId: " + userId
                    + ", feedId: " + feedId
                    + ", 작성자: " + feed.getUserId()
                    + ", 제목: " + feed.getTitle());
        } else {
            log.info("피드 삭제 - userId: " + userId + ", feedId: " + feedId);
        }
    }

    @Override
    @Transactional
    public FeedResponse toggleLike(int userId, int feedId) {

        // 없는 글이면 여기서 걸린다
        getFeed(userId, feedId);

        if (feedMapper.existsFeedLike(feedId, userId)) {
            feedMapper.deleteFeedLike(feedId, userId);
        } else {
            feedMapper.insertFeedLike(feedId, userId);
        }

        return getFeed(userId, feedId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(int feedId) {
        return feedMapper.selectComments(feedId);
    }

    @Override
    @Transactional
    public void addComment(int userId, int feedId, CommentRequest request) {

        String content = checkCommentContent(request);

        // 없는 글에는 댓글을 달 수 없다 (FK 위반 대신 안내 문구로)
        getFeed(userId, feedId);

        feedMapper.insertComment(feedId, userId, content);
    }

    @Override
    @Transactional
    public List<CommentResponse> updateComment(int userId, int commentId, CommentRequest request) {

        String content = checkCommentContent(request);

        Integer feedId = feedMapper.selectFeedIdByComment(commentId);

        if (feedId == null) {
            throw new IllegalArgumentException("없는 댓글입니다.");
        }

        if (feedMapper.updateComment(commentId, userId, content) == 0) {
            throw new IllegalArgumentException("내가 쓴 댓글만 고칠 수 있습니다.");
        }

        return feedMapper.selectComments(feedId);
    }

    @Override
    @Transactional
    public void deleteComment(int userId, int commentId, boolean admin) {

        if (feedMapper.deleteComment(commentId, userId, admin) == 0) {
            throw new IllegalArgumentException(admin
                    ? "없는 댓글입니다."
                    : "내가 쓴 댓글만 지울 수 있습니다.");
        }

        if (admin) {
            log.warn("관리자 댓글 삭제 - adminId: " + userId + ", commentId: " + commentId);
        }
    }

    private String checkCommentContent(CommentRequest request) {

        String content = (request == null) ? "" : trim(request.getContent());

        if (content.isEmpty()) {
            throw new IllegalArgumentException("댓글 내용을 입력해 주세요.");
        }
        if (content.length() > MAX_COMMENT_LENGTH) {
            throw new IllegalArgumentException("댓글은 " + MAX_COMMENT_LENGTH + "자까지 쓸 수 있습니다.");
        }
        return content;
    }

    private void validate(String title, String content, String category) {

        if (title.isEmpty()) {
            throw new IllegalArgumentException("제목을 입력해 주세요.");
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException("제목은 " + MAX_TITLE_LENGTH + "자까지 쓸 수 있습니다.");
        }
        if (content.isEmpty()) {
            throw new IllegalArgumentException("내용을 입력해 주세요.");
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException("내용은 " + MAX_CONTENT_LENGTH + "자까지 쓸 수 있습니다.");
        }
        if (!CATEGORIES.contains(category)) {
            throw new IllegalArgumentException("카테고리를 선택해 주세요.");
        }
    }

    private String trim(String value) {
        return (value == null) ? "" : value.trim();
    }
}
