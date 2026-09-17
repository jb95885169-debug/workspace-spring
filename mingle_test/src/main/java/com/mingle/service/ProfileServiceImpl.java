package com.mingle.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mingle.dto.ProfileResponse;
import com.mingle.dto.ProfileUpdateRequest;
import com.mingle.mapper.UserMapper;
import com.mingle.vo.UserProfileVO;
import com.mingle.vo.UserVO;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProfilePhotoService profilePhotoService;

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile(int userId) {

        UserProfileVO profile = userMapper.selectProfile(userId);
        UserVO user = userMapper.selectUserById(userId);

        if (profile == null || user == null) {
            // 가입할 때 프로필까지 같은 트랜잭션으로 넣으므로 정상적으로는 생기지 않는다
            throw new IllegalStateException("프로필 정보를 찾을 수 없습니다.");
        }

        ProfileResponse response = new ProfileResponse();

        response.setUserId(userId);
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());

        response.setNickname(profile.getNickname());
        response.setBirthDate(profile.getBirthDate());
        response.setGender(profile.getGender());
        response.setRegion(profile.getRegion());
        response.setJob(profile.getJob());
        response.setHeight(profile.getHeight());

        response.setInterestIds(userMapper.selectUserInterestIds(userId));
        response.setPhotos(userMapper.selectPhotos(userId));

        return response;
    }

    @Override
    @Transactional
    public void updateProfile(int userId, ProfileUpdateRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("수정할 내용을 입력해 주세요.");
        }

        String nickname = trim(request.getNickname());
        String phone = trim(request.getPhone());
        String region = trim(request.getRegion());

        String job = trim(request.getJob());

        validate(nickname, phone, region, job, request.getHeight(), request.getInterestIds());

        // 본인 닉네임은 그대로 둘 수 있어야 하므로 자기 자신은 빼고 확인한다
        if (userMapper.countByNicknameExcept(nickname, userId) > 0) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        if (userMapper.updateProfile(
                userId,
                nickname,
                region,
                emptyToNull(job),
                request.getHeight()) == 0) {

            throw new IllegalStateException("프로필 정보를 찾을 수 없습니다.");
        }

        userMapper.updatePhone(userId, phone);

        // 관심사는 지우고 다시 넣는다 (고른 것만 남기기)
        userMapper.deleteUserInterests(userId);
        userMapper.insertUserInterests(userId, request.getInterestIds());

        // 새로 올린 사진은 temp에서 옮겨 온다 (없으면 아무것도 하지 않음)
        profilePhotoService.addPhotos(userId, request.getTempFileNames());

        log.info("프로필 수정 - userId: " + userId);
    }

    private void validate(String nickname, String phone, String region, String job,
                          Integer height, List<Integer> interestIds) {

        if (nickname.isEmpty()) {
            throw new IllegalArgumentException("닉네임을 입력해 주세요.");
        }
        if (nickname.length() > MAX_NICKNAME_LENGTH) {
            throw new IllegalArgumentException("닉네임은 " + MAX_NICKNAME_LENGTH + "자까지 쓸 수 있습니다.");
        }
        if (phone.isEmpty()) {
            // 비밀번호 찾기에서 쓰므로 비워 둘 수 없다
            throw new IllegalArgumentException("휴대폰 번호를 입력해 주세요.");
        }
        if (phone.length() > MAX_PHONE_LENGTH) {
            throw new IllegalArgumentException("휴대폰 번호가 너무 깁니다.");
        }
        if (region.isEmpty()) {
            throw new IllegalArgumentException("거주 지역을 입력해 주세요.");
        }
        if (region.length() > MAX_REGION_LENGTH) {
            throw new IllegalArgumentException("거주 지역은 " + MAX_REGION_LENGTH + "자까지 쓸 수 있습니다.");
        }
        if (job.length() > MAX_JOB_LENGTH) {
            throw new IllegalArgumentException("직업은 " + MAX_JOB_LENGTH + "자까지 쓸 수 있습니다.");
        }
        // ck_profiles_height (100 ~ 250), 입력하지 않으면 저장하지 않는다
        if (height != null && (height < 100 || height > 250)) {
            throw new IllegalArgumentException("키는 100cm에서 250cm 사이로 입력해 주세요.");
        }
        if (interestIds == null || interestIds.size() < InterestService.MIN_INTERESTS) {
            throw new IllegalArgumentException("관심사를 " + InterestService.MIN_INTERESTS + "개 이상 골라 주세요.");
        }
    }

    private String trim(String value) {
        return (value == null) ? "" : value.trim();
    }

    private String emptyToNull(String value) {
        return value.isEmpty() ? null : value;
    }
}
