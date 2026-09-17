package com.mingle.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 피드 화면
 * 목록 / 작성 / 좋아요 / 댓글은 화면에서 /api/feeds로 처리한다.
 */
@Controller
@RequestMapping("/feeds")
public class FeedController {

    @GetMapping
    public String feedListPage() {

        return "feed/feedList";
    }
}
