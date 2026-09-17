package com.mingle.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 첫 화면
 *
 * "/"는 Spring Security가 로그인을 요구하므로 여기까지 들어왔다면 이미 로그인한 상태다.
 * 그래서 추천 회원 화면으로 보낸다. (로그인하지 않았으면 Security가 /login으로 보낸다)
 */
@Controller
public class HomeController {

	@GetMapping("/")
	public String home() {

		return "redirect:/users/list";
	}
}
