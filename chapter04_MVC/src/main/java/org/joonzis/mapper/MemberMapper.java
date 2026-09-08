package org.joonzis.mapper;

import org.joonzis.domain.MemberVO;

public interface MemberMapper {
	public MemberVO read(String userId);

	public int register(MemberVO vo);
	
	public int registerAuth(MemberVO vo);
}
