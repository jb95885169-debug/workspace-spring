package org.joonzis.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.log4j.Log4j;

@Log4j
@Controller
@RequestMapping("/sample/*")
public class SampleController {
	
	@GetMapping("/all")
	public String doAll() {
		log.info("do all can access everybody");
		return "sample/all";
	}
	@GetMapping("/member")
	public String doMember() {
		log.info("logined member");
		return "sample/member";
	}
	@GetMapping("/admin")
	public String doAdmin() {
		log.info("admin only");
		return "sample/admin";
	}
	
	// 어노테이션을이용한 서큐리티
	@PreAuthorize("hasAntRole('ROLE_ADMIN', 'ROLE_MEMBER')")	// 표현식이 들어갈수있음
	@GetMapping("/annoMember")
	public String doMember2() {
		log.info("로그인 멤버 어노테이션");
		return "sample/annoMember";
	}
	
	@Secured({"ROLE_ADMIN"})	//권한 체킹
	@GetMapping("/annoAdmin")
	public String doAdmin2() {
		log.info("로그인 어드민 어노테이션");
		return "sample/annoAdmin";
	}
	
	
	
	
	
	
	
	
}








