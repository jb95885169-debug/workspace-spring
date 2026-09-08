package org.joonzis.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.log4j.Log4j;

@Log4j
@Controller
public class CommonController {
	@GetMapping("/accessError")
	public String accessDenied(Authentication auth, Model model) {
		log.info("Access Denied : " + auth);
		model.addAttribute("msg", "접근 권한 에러");
		return "/accessError";
	}
	
	@GetMapping("/customLogin")
	public String loginInput(String error, String logout, Model model) {
		log.info("error : " + error);
		log.info("logout : " + logout);
		
		if(error != null) {
			model.addAttribute("error", "Login Error Check your Account");
		}
		if(logout != null) {
			model.addAttribute("logout", "Logout!!!!");
		}
		return "/customLogin";
		
	}
	
	@GetMapping("/customLogout")
	public String logoutGET() {
		log.info("custom logout");
		return "/customLogout";
	}
	
	@ResponseBody
	@GetMapping("/api/currentUser")
	public Authentication getCurrentUser() {
		return SecurityContextHolder.getContext().getAuthentication();
	}
	
	
	
	
	
	
}








