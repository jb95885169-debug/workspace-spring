package com.mingle.service;

import com.mingle.dto.PageResponse;
import com.mingle.dto.UserResponse;
import com.mingle.mapper.UserMapper;
import com.mingle.type.SubscriptionTier;
import com.mingle.vo.UserVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
    private UserMapper userMapper;

	/** 받은 좋아요를 가릴지 등급으로 정한다 */
	@Autowired
	private SubscriptionService subscriptionService;

	/** 가려진 카드에 보여 줄 흐린 사진 */
	@Autowired
	private ImageBlurService imageBlurService;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getRecommendedUsers(int userId) {
        return userMapper.selectRecommendedUsers(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserVO> getUsers() {
        return userMapper.selectUsers();
    }

    @Override
    @Transactional
    public void updateUserStatus(int userId, String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("회원 상태는 필수입니다.");
        }

        String normalizedStatus = status.trim().toUpperCase();
        if (userMapper.updateUserStatus(userId, normalizedStatus) == 0) {
            throw new IllegalArgumentException("존재하지 않는 회원이거나 허용되지 않은 상태입니다.");
        }
    }

	@Override
	@Transactional(readOnly = true)
	public UserResponse getUser(int userId) {
		return userMapper.selectUser(userId);
	}

	@Override
	@Transactional(readOnly = true)
	public PageResponse<UserResponse> getReceivedLikes(int userId, int page) {

		int size = LIKE_PAGE_SIZE;
		int offset = PageResponse.toOffset(page, size);

		List<UserResponse> likes = userMapper.selectReceivedLikes(userId, offset, size);

		// 무료 회원에게는 일반 좋아요만 가린다 (원본 사진 주소도 내려보내지 않는다)
		// 슈퍼 좋아요는 보낸 사람이 드러나야 의미가 있어서 그대로 보여 준다 (추천 카드와 같은 규칙)
		if (isBasic(userId)) {
			likes.stream()
			     .filter(like -> !like.isSuperLike())
			     .forEach(this::mask);
		}

		return PageResponse.of(
				likes,
				page,
				size,
				userMapper.countReceivedLikes(userId));
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse getReceivedLikeCard(int viewerId, int senderId, boolean superLike) {

		UserResponse user = userMapper.selectUser(senderId);

		if (user == null) {
			return null;
		}

		// 받은 좋아요 목록에서 맨 위에 두기 위한 표시
		user.setSuperLike(superLike);

		// 목록과 같은 규칙: 일반 좋아요만 가리고 슈퍼 좋아요는 그대로 보여 준다
		if (!superLike && isBasic(viewerId)) {
			mask(user);
		}

		return user;
	}

	private boolean isBasic(int userId) {
		return SubscriptionTier.BASIC == SubscriptionTier.of(subscriptionService.getCurrentTier(userId));
	}

	/** 누구인지 알 수 있는 값은 지우고, 사진은 흐린 복사본으로 바꾼다 */
	private void mask(UserResponse user) {

		user.setNickname("???");
		user.setJob(null);
		user.setRegion(null);
		user.setIntroduction(null);
		user.setInterests(null);
		user.setAge(null);

		// 원본 주소를 그대로 주면 개발자 도구로 볼 수 있으므로 흐린 복사본만 내려보낸다
		user.setPhotoUrl(imageBlurService.getBlurredUrl(user.getPhotoUrl()));

		user.setLocked(true);
	}
}
