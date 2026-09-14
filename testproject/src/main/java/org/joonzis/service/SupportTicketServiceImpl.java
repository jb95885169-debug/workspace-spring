package org.joonzis.service;

import java.util.List;

import org.joonzis.domain.SupportTicketVO;
import org.joonzis.mapper.SupportTicketMapper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupportTicketServiceImpl implements SupportTicketService {

    // Mapper를 주입받음 (Lombok의 @RequiredArgsConstructor 덕분에 자동으로 연결됨)
    private final SupportTicketMapper supportTicketMapper;
//
//    @Override
//    public List<SupportTicketVO> getTicketList() {
//        // Mapper의 메서드를 호출해서 DB에서 데이터를 가져옴
//        return supportTicketMapper.getTicketList();
//    }
    
    @Override
    public void registerTicket(SupportTicketVO vo) {
        supportTicketMapper.insertTicket(vo);
    }
    @Override
    public List<SupportTicketVO> getTicketList() {
    	return supportTicketMapper.getTicketList();
    }
    
    @Override
    public void updateTicketStatus(Long id) {
    	supportTicketMapper.updateTicketStatus(id);
    }
    
}