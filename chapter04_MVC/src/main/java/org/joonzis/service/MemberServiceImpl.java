package org.joonzis.service;

import org.joonzis.domain.MemberVO;
import org.joonzis.mapper.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j;

@Log4j
@Service
public class MemberServiceImpl implements MemberService{
	
	@Autowired
	private MemberMapper mapper;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public int register(MemberVO vo) {

		log.info("회원가입 : " + vo);
		vo.setUserPw(passwordEncoder.encode(vo.getUserPw()));
		
	    int result = mapper.register(vo);

	    mapper.registerAuth(vo);

	    return result;
		
		
		/*
		 * vo.setEnabled(true);
		 * 
		 * return mapper.register(vo);
		 */
	}
}
