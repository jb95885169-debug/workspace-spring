package com.mingle.restcontroller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mingle.dto.PageResponse;
import com.mingle.dto.UserResponse;
import com.mingle.security.LoginUserId;
import com.mingle.service.UserService;
import com.mingle.vo.UserVO;

import lombok.extern.log4j.Log4j;

@RestController
@RequestMapping("/api/users")
@Log4j
public class UserRestController {

    @Autowired
    private UserService userService;

    // 추천 회원 목록 (스와이프 카드, 나에게 슈퍼 좋아요 보낸 회원 먼저)
    @GetMapping
    public ResponseEntity<List<UserResponse>> getRecommendedUsers(
            @LoginUserId int userId) {

        List<UserResponse> users =
                userService.getRecommendedUsers(userId);
        log.info(users);
        return ResponseEntity.ok(users);
    }

    // 관리자용 회원 목록
    @GetMapping("/admin")
    public ResponseEntity<List<UserVO>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    // 회원 상태 변경 (ACTIVE / BANNED / WITHDRAWN)
    @PatchMapping("/{userId:\\d+}/status")
    public ResponseEntity<Map<String, Object>> updateUserStatus(
            @PathVariable int userId,
            @RequestBody Map<String, String> request) {

        String status = request.get("status");
        userService.updateUserStatus(userId, status);

        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "status", status.toUpperCase()));
    }

    // 내 정보
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(
            @LoginUserId int userId) {

        UserResponse user =
                userService.getUser(userId);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user);
    }

    // 나를 좋아요한 회원 목록 (채팅 목록 화면의 좋아요 영역)
    // UserService.LIKE_PAGE_SIZE개씩, 옆으로 스크롤하면 다음 page
    @GetMapping("/likes")
    public ResponseEntity<PageResponse<UserResponse>> getReceivedLikes(
            @RequestParam(defaultValue = "1") int page,
            @LoginUserId int userId) {

        return ResponseEntity.ok(userService.getReceivedLikes(userId, page));
    }

    // 회원 상세
    @GetMapping("/{userId:\\d+}")
    public ResponseEntity<UserResponse> getUser(
            @PathVariable int userId) {

        UserResponse user =
                userService.getUser(userId);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user);
    }
}