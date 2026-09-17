package com.mingle.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mingle.dto.PasswordFindRequest;
import com.mingle.dto.SignupRequest;
import com.mingle.service.AuthService;
import com.mingle.service.InterestService;

/**
 * 로그인 화면 / 회원가입 / 비밀번호 찾기
 *
 * 로그인(POST /login)과 로그아웃(/logout) 처리는 Spring Security가 맡는다.
 * 여기에는 로그인 화면을 보여 주는 GET만 있다. (security-context.xml)
 */
@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private InterestService interestService;


    /* ================= 로그인 화면 ================= */

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String signup,
            Principal principal,
            Model model) {

        // 이미 로그인한 상태면 로그인 화면을 보여 주지 않는다 (익명 사용자는 null)
        if (principal != null) {
            return "redirect:/users/list";
        }

        // 로그인 실패 시 Security가 /login?error로 되돌려 보낸다
        if (error != null) {
            model.addAttribute("error", "이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        if (signup != null) {
            model.addAttribute("message", "회원가입이 완료되었습니다. 로그인해 주세요.");
        }
        return "login";
    }


    /* ================= 회원가입 ================= */

    @GetMapping("/signup")
    public String signupPage(Model model) {

        model.addAttribute("signupRequest", new SignupRequest());
        model.addAttribute("interests", interestService.getInterests());

        return "signup";
    }

    /**
     * 사진은 이미 /api/uploads/temp에 올라가 있고, 여기에는 임시 파일명만 들어온다.
     */
    @PostMapping("/signup")
    public String signup(
            @ModelAttribute SignupRequest signupRequest,
            Model model) {

        try {
            authService.signup(signupRequest);

        } catch (IllegalArgumentException e) {
            // 입력한 값은 그대로 두고 메시지만 보여 준다 (비밀번호는 화면에서 비운다)
            model.addAttribute("error", e.getMessage());
            model.addAttribute("interests", interestService.getInterests());
            return "signup";
        }

        return "redirect:/login?signup";
    }


    /* ================= 비밀번호 찾기 ================= */

    @GetMapping("/password/find")
    public String passwordFindPage(Model model) {

        model.addAttribute("passwordFindRequest", new PasswordFindRequest());

        return "passwordFind";
    }

    /**
     * 이메일 + 닉네임이 맞으면 임시 비밀번호를 발급해 화면에 한 번 보여 준다.
     * 저장된 비밀번호는 BCrypt 해시라 원래 값을 알려 줄 수 없다.
     */
    @PostMapping("/password/find")
    public String findPassword(
            @ModelAttribute PasswordFindRequest passwordFindRequest,
            Model model) {

        try {
            String tempPassword = authService.findPassword(passwordFindRequest);
            model.addAttribute("tempPassword", tempPassword);

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }

        return "passwordFind";
    }
}
