package com.mingle.service;

public interface NotificationService {

	void notifyMatch(int matchId, int userId, int targetId);

	/**
	 * 좋아요 / 슈퍼 좋아요를 받은 사람에게 실시간 알림
	 * superLike가 true면 받은 좋아요 목록 맨 위에 표시된다.
	 */
	void notifyLike(int userId, int targetId, boolean superLike);
}
