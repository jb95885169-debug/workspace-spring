package com.mingle.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/subscriptions")
public class AdminSubscriptionController {

    @GetMapping
    public String page(HttpServletRequest request) {
        if (!request.isUserInRole("ADMIN")) {
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }
        return "admin/subscriptions";
    }
}