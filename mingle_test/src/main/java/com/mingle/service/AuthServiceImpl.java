package com.mingle.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mingle.dto.PasswordFindRequest;
import com.mingle.dto.SignupRequest;
import com.mingle.mapper.UserMapper;
import com.mingle.vo.UserVO;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class AuthServiceImpl implements AuthService {

    /** 비밀번호 최소 길이 */
    private static final int MIN_PASSWORD_LENGTH = 8;

    /** 임시 비밀번호 길이 */
    private static final int TEMP_PASSWORD_LENGTH = 8;

    /** 가입 시 부여하는 권한 (mingle_auth.role, 화면에서는 ROLE_USER) */
    private static final String DEFAULT_ROLE = "USER";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProfilePhotoService profilePhotoService;

    /** security-context.xml의 bcryptPasswordEncoder */
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public int signup(SignupRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("가입 정보를 입력해 주세요.");
        }

        String email = normalizeEmail(request.getEmail());
        String nickname = trim(request.getNickname());
        String phone = trim(request.getPhone());
        String region = trim(request.getRegion());

        String job = trim(request.getJob());

        validatePassword(request.getPassword(), request.getPasswordConfirm());
        validateProfile(nickname, request.getBirthDate(), request.getGender(), region, job, request.getHeight());
        validateInterests(request.getInterestIds());

        if (phone.isEmpty()) {
            throw new IllegalArgumentException("휴대폰 번호를 입력해 주세요.");
        }
        if (phone.length() > ProfileService.MAX_PHONE_LENGTH) {
            throw new IllegalArgumentException("휴대폰 번호가 너무 깁니다.");
        }

        // 미리 확인하지만 동시 요청은 통과할 수 있다.
        // 최종 방어는 uk_users_email / uk_profiles_nickname 제약이다.
        if (userMapper.countByEmail(email) > 0) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        if (userMapper.countByNickname(nickname) > 0) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        UserVO user = new UserVO();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhone(phone);

        userMapper.insertUser(user);   // selectKey로 user.id가 채워진다

        // 아래는 모두 같은 트랜잭션이라 회원만 생기고 프로필이 없는 상태는 생기지 않는다
        userMapper.insertAuth(user.getId(), DEFAULT_ROLE);

        userMapper.insertProfile(
                user.getId(),
                nickname,
                request.getBirthDate(),
                request.getGender(),
                region,
                emptyToNull(job),
                request.getHeight());

        userMapper.insertUserInterests(user.getId(), request.getInterestIds());

        // temp에 올려 둔 사진을 profile 폴더로 옮기고 등록 (없으면 아무것도 하지 않음)
        profilePhotoService.addPhotos(user.getId(), request.getTempFileNames());

        log.info("회원가입 - userId: " + user.getId());

        return user.getId();
    }

    @Override
    @Transactional
    public String findPassword(PasswordFindRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("정보를 입력해 주세요.");
        }

        String email = normalizeEmail(request.getEmail());
        String nickname = trim(request.getNickname());

        if (nickname.isEmpty()) {
            throw new IllegalArgumentException("닉네임을 입력해 주세요.");
        }

        Integer userId = userMapper.selectUserIdByEmailAndNickname(email, nickname);

        if (userId == null) {
            throw new IllegalArgumentException("일치하는 회원 정보를 찾을 수 없습니다.");
        }

        // 저장된 비밀번호는 해시라 되돌릴 수 없으므로 임시 비밀번호를 새로 발급한다
        String tempPassword = UUID.randomUUID().toString().substring(0, TEMP_PASSWORD_LENGTH);

        userMapper.updatePassword(userId, passwordEncoder.encode(tempPassword));

        log.info("임시 비밀번호 발급 - userId: " + userId);

        return tempPassword;
    }

    /** 대소문자와 공백 차이로 중복 가입되지 않도록 정리 */
    private String normalizeEmail(String email) {

        String value = trim(email);

        if (value.isEmpty()) {
            throw new IllegalArgumentException("이메일을 입력해 주세요.");
        }
        if (!value.contains("@")) {
            throw new IllegalArgumentException("이메일 형식이 올바르지 않습니다.");
        }
        return value.toLowerCase();
    }

    private void validatePassword(String password, String passwordConfirm) {

        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("비밀번호는 " + MIN_PASSWORD_LENGTH + "자 이상이어야 합니다.");
        }
        if (!password.equals(passwordConfirm)) {
            throw new IllegalArgumentException("비밀번호가 서로 다릅니다.");
        }
    }

    /** mingle_user_profiles의 NOT NULL 항목과 CHECK 제약, 컬럼 길이에 맞는지 확인 */
    private void validateProfile(String nickname, Date birthDate, String gender,
                                 String region, String job, Integer height) {

        if (nickname.isEmpty()) {
            throw new IllegalArgumentException("닉네임을 입력해 주세요.");
        }
        if (nickname.length() > ProfileService.MAX_NICKNAME_LENGTH) {
            throw new IllegalArgumentException(
                    "닉네임은 " + ProfileService.MAX_NICKNAME_LENGTH + "자까지 쓸 수 있습니다.");
        }
        if (birthDate == null) {
            throw new IllegalArgumentException("생년월일을 입력해 주세요.");
        }
        if (birthDate.after(new Date())) {
            throw new IllegalArgumentException("생년월일이 올바르지 않습니다.");
        }
        if (!"MALE".equals(gender) && !"FEMALE".equals(gender)) {
            throw new IllegalArgumentException("성별을 선택해 주세요.");
        }
        if (region.isEmpty()) {
            throw new IllegalArgumentException("거주 지역을 입력해 주세요.");
        }
        if (region.length() > ProfileService.MAX_REGION_LENGTH) {
            throw new IllegalArgumentException(
                    "거주 지역은 " + ProfileService.MAX_REGION_LENGTH + "자까지 쓸 수 있습니다.");
        }
        if (job.length() > ProfileService.MAX_JOB_LENGTH) {
            throw new IllegalArgumentException(
                    "직업은 " + ProfileService.MAX_JOB_LENGTH + "자까지 쓸 수 있습니다.");
        }
        // ck_profiles_height (100 ~ 250), 입력하지 않으면 저장하지 않는다
        if (height != null && (height < 100 || height > 250)) {
            throw new IllegalArgumentException("키는 100cm에서 250cm 사이로 입력해 주세요.");
        }
    }

    /** 추천 카드에 보여 줄 관심사 (선택지에 없는 ID는 FK 제약에 걸린다) */
    private void validateInterests(List<Integer> interestIds) {

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
