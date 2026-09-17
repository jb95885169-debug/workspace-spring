package com.mingle.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/users")
public class UserController {

    // 추천 회원(스와이프) JSP
    @GetMapping("/list")
    public String userListPage() {
        return "user/userList";
    }
}