package com.mingle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mingle.dto.ProfileResponse;
import com.mingle.dto.ProfileUpdateRequest;
import com.mingle.security.LoginUserId;
import com.mingle.service.InterestService;
import com.mingle.service.ProfileService;

/**
 * 내 프로필 수정 화면
 *
 * 사진 추가 / 삭제 / 대표 지정은 화면에서 JSON으로 처리한다.
 * (restcontroller.ProfilePhotoRestController)
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private InterestService interestService;


    @GetMapping
    public String profilePage(@LoginUserId int userId, Model model) {

        model.addAttribute("profile", profileService.getMyProfile(userId));
        model.addAttribute("interests", interestService.getInterests());

        return "user/profile";
    }


    @PostMapping
    public String updateProfile(
            @ModelAttribute ProfileUpdateRequest profileUpdateRequest,
            @LoginUserId int userId,
            Model model) {

        try {
            profileService.updateProfile(userId, profileUpdateRequest);

        } catch (IllegalArgumentException e) {

            // 저장에 실패하면 입력한 값을 그대로 둔 채 메시지를 보여 준다
            ProfileResponse profile = profileService.getMyProfile(userId);

            profile.setNickname(profileUpdateRequest.getNickname());
            profile.setPhone(profileUpdateRequest.getPhone());
            profile.setRegion(profileUpdateRequest.getRegion());
            profile.setJob(profileUpdateRequest.getJob());
            profile.setHeight(profileUpdateRequest.getHeight());
            profile.setInterestIds(profileUpdateRequest.getInterestIds());

            model.addAttribute("error", e.getMessage());
            model.addAttribute("profile", profile);
            model.addAttribute("interests", interestService.getInterests());

            return "user/profile";
        }

        return "redirect:/profile?saved";
    }
}
