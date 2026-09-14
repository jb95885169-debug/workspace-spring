package org.joonzis.service;

import java.util.List;

import org.joonzis.domain.SupportTicketVO;

public interface SupportTicketService {
//	// 문의글 목록 전체 조회
//    List<SupportTicketVO> getTicketList();
//    
	
	void registerTicket(SupportTicketVO vo);

	List<SupportTicketVO> getTicketList();
    
	void updateTicketStatus(Long id);
}
