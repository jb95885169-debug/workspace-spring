package com.mingle.service;

import java.util.List;

import com.mingle.dto.PageResponse;
import com.mingle.dto.UserResponse;
import com.mingle.vo.UserVO;

/**
 * 회원 정보 조회
 * 로그인 / 회원가입 / 비밀번호 찾기는 AuthService와 Spring Security가 담당한다.
 */
public interface UserService {

	/** 받은 좋아요를 한 번에 가져오는 개수 (옆으로 스크롤하면 다음 페이지) */
	int LIKE_PAGE_SIZE = 20;

	// 추천 회원 목록 (이성, 아직 스와이프하지 않은 회원, 나에게 슈퍼 좋아요 보낸 회원 먼저)
	List<UserResponse> getRecommendedUsers(int userId);

	// 관리자용 회원 목록
	List<UserVO> getUsers();

	// 회원 상태 변경 (ACTIVE / BANNED / WITHDRAWN)
	void updateUserStatus(int userId, String status);

	// 회원 한 명 (없거나 ACTIVE가 아니면 null)
	UserResponse getUser(int userId);

	// 받은 좋아요 목록 (슈퍼 좋아요 먼저, page는 1부터)
	PageResponse<UserResponse> getReceivedLikes(int userId, int page);

	/**
	 * 받은 좋아요 카드 한 장 (실시간 알림용)
	 * 보는 사람(viewerId)이 무료 회원이면 일반 좋아요는 가려서 돌려준다.
	 * 슈퍼 좋아요는 보낸 사람이 드러나야 하므로 가리지 않는다.
	 */
	UserResponse getReceivedLikeCard(int viewerId, int senderId, boolean superLike);
}
