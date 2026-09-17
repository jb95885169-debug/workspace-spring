package com.mingle.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mingle.security.LoginUserId;
import com.mingle.service.MatchService;

@RestController
@RequestMapping("/api/matches")
public class MatchRestController {

    @Autowired
    private MatchService matchService;


    // 매칭 나가기 (ACTIVE면 CANCELLED, 상대가 먼저 나갔으면 DESTROYED)
    // 204 성공 / 401 로그인 필요 / 404 없는 매칭·참여자 아님 / 409 이미 나간 매칭
    // (404 / 409는 controller.advice.MatchExceptionHandler가 응답)
    @DeleteMapping("/{matchId}")
    public ResponseEntity<Void> leaveMatch(
            @PathVariable int matchId,
            @LoginUserId int userId) {

        // 로그인 확인은 Spring Security가 한다 (여기까지 오면 로그인된 상태)
        matchService.leaveMatch(matchId, userId);

        return ResponseEntity.noContent().build();
    }
}
