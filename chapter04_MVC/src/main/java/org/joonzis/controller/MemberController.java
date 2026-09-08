package org.joonzis.controller;

import org.joonzis.domain.MemberVO;
import org.joonzis.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.extern.log4j.Log4j;

@Log4j
@Controller
public class MemberController {
	
	@Autowired
	private MemberService service;
	
	@GetMapping("/joinMember")
	public String joinPage() {
		log.info("회원가입 ");
		return "/joinMember";
	}
	
	@PostMapping("/joinMember")
	public String joinMember(MemberVO vo) {
		log.info("회원 가입 정보 : " + vo);
		
        // 회원가입 처리
        service.register(vo);

        return "/customLogin";
    }
	
	
}
