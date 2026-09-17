package com.mingle.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mingle.dto.UserResponse;
import com.mingle.vo.InterestVO;
import com.mingle.vo.UserPhotoVO;
import com.mingle.vo.UserProfileVO;
import com.mingle.vo.UserVO;

public interface UserMapper {

	// 이메일로 회원 조회 (로그인, 없으면 null / 상태 확인은 security.CustomUserDetailService)
	UserVO selectUserByEmail(String email);

	// 회원 계정 조회 (프로필 수정 화면의 이메일 / 휴대폰)
	UserVO selectUserById(int userId);

	// 회원 목록 (관리자용)
	List<UserVO> selectUsers();

	// 회원 권한 목록 (mingle_auth, 값은 USER / ADMIN)
	List<String> selectRoles(int userId);

	// 마지막 로그인 일시 갱신 (로그인 성공 시)
	int updateLastLoginAt(int userId);

	// 관리자 계정 상태 변경 (ACTIVE / BANNED / WITHDRAWN만 허용)
	int updateUserStatus(
			@Param("userId") int userId,
			@Param("status") String status);

	// 추천 회원 목록 (나에게 슈퍼 좋아요 보낸 회원 먼저)
	List<UserResponse> selectRecommendedUsers(int userId);

	// 회원 한 명 (ACTIVE 회원만, 없으면 null)
	UserResponse selectUser(int userId);

	// 받은 좋아요 목록 (슈퍼 좋아요 먼저, offset부터 size개)
	List<UserResponse> selectReceivedLikes(
			@Param("userId") int userId,
			@Param("offset") int offset,
			@Param("size") int size);

	// 받은 좋아요 수 (더 불러올 게 있는지 확인용)
	int countReceivedLikes(int userId);


	/* ================= 회원가입 ================= */

	// 이메일 중복 확인
	int countByEmail(String email);

	// 닉네임 중복 확인 (가입)
	int countByNickname(String nickname);

	// 닉네임 중복 확인 (수정, 본인 것은 제외)
	int countByNicknameExcept(
			@Param("nickname") String nickname,
			@Param("userId") int userId);

	// 회원 INSERT (selectKey로 user.id가 채워짐)
	int insertUser(UserVO user);

	// 권한 INSERT (가입 시 USER)
	int insertAuth(@Param("userId") int userId, @Param("role") String role);

	// 프로필 INSERT (닉네임 / 생년월일 / 성별 / 지역은 필수, job과 height는 선택)
	int insertProfile(
			@Param("userId") int userId,
			@Param("nickname") String nickname,
			@Param("birthDate") Date birthDate,
			@Param("gender") String gender,
			@Param("region") String region,
			@Param("job") String job,
			@Param("height") Integer height);


	/* ================= 내 프로필 ================= */

	// 내 프로필 (없으면 null)
	UserProfileVO selectProfile(int userId);

	// 프로필 수정 (생년월일 / 성별은 바꾸지 않는다)
	int updateProfile(
			@Param("userId") int userId,
			@Param("nickname") String nickname,
			@Param("region") String region,
			@Param("job") String job,
			@Param("height") Integer height);

	// 휴대폰 번호 수정 (비밀번호 찾기에 쓰이므로 계정 쪽에 있다)
	int updatePhone(@Param("userId") int userId, @Param("phone") String phone);


	/* ================= 관심사 ================= */

	// 관심사 선택지 (가입 / 수정 화면 체크박스)
	List<InterestVO> selectInterests();

	// 내가 고른 관심사 ID
	List<Integer> selectUserInterestIds(int userId);

	// 회원이 고른 관심사 INSERT (여러 건)
	int insertUserInterests(
			@Param("userId") int userId,
			@Param("interestIds") List<Integer> interestIds);

	// 관심사 전체 삭제 (수정 시 지우고 다시 넣는다)
	int deleteUserInterests(int userId);


	/* ================= 프로필 사진 ================= */

	// 사진 목록 (대표 사진이 먼저, 그다음 등록 순서)
	List<UserPhotoVO> selectPhotos(int userId);

	// 사진 한 장 (본인 것만, 없으면 null)
	UserPhotoVO selectPhoto(@Param("photoId") int photoId, @Param("userId") int userId);

	// 사진 개수 (등록 제한 확인)
	int countPhotos(int userId);

	// 마지막 노출 순서 (사진을 뒤에 붙일 때, 없으면 0)
	int selectMaxDisplayOrder(int userId);

	// 사진 INSERT (첫 장이 대표 사진)
	int insertProfilePhoto(
			@Param("userId") int userId,
			@Param("photoUrl") String photoUrl,
			@Param("isPrimary") int isPrimary,
			@Param("displayOrder") int displayOrder);

	// 사진 삭제 (본인 것만)
	int deletePhoto(@Param("photoId") int photoId, @Param("userId") int userId);

	// 대표 사진 해제 (새 대표를 지정하기 전에 호출)
	int clearPrimaryPhoto(int userId);

	// 대표 사진 지정 (본인 것만)
	int updatePhotoPrimary(@Param("photoId") int photoId, @Param("userId") int userId);


	/* ================= 비밀번호 찾기 ================= */

	// 본인 확인 (이메일 + 닉네임, 없으면 null)
	Integer selectUserIdByEmailAndNickname(
			@Param("email") String email,
			@Param("nickname") String nickname);

	// 비밀번호 변경 (임시 비밀번호 발급)
	int updatePassword(
			@Param("userId") int userId,
			@Param("passwordHash") String passwordHash);
}
