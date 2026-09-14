package org.joonzis.mapper;

import java.util.List;

import org.joonzis.domain.SupportTicketVO;

public interface SupportTicketMapper {

	// 문의글 목록 전체 조회
    List<SupportTicketVO> getTicketList();
    
	// 회원이 문의글 남기기
    int insertTicket(SupportTicketVO vo);
    
    // 문의에 답변달린글 처리중 > 답변완료 처리
    void updateTicketStatus(Long id);
}