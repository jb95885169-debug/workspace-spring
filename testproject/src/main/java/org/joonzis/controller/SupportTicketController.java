package org.joonzis.controller;

import java.util.List;

import org.joonzis.domain.SupportTicketVO;
import org.joonzis.service.SupportTicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/support")
@RequiredArgsConstructor
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    // 1. 문의글 작성 화면 보여주기
    @GetMapping("/write")
    public String writeForm() {
        return "support/writeForm"; // 작성 폼 화면 파일(JSP 등) 경로
    }

    // 2. 작성된 문의글 데이터 DB에 저장하기
    @PostMapping("/write")
    public ResponseEntity<String> writeTicket(@RequestBody SupportTicketVO vo) {
        try {
            vo.setUserId(1L); 
            supportTicketService.registerTicket(vo);
            // 성공 시 200 OK 상태 코드와 함께 응답
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            e.printStackTrace();
            // 실패 시 500 에러 대신 실패 메시지 전달
            return ResponseEntity.status(500).body("fail");
        }
    }
    
    @GetMapping(value = "/list", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public List<SupportTicketVO> getTicketList() {
        // 1번 유저의 문의 목록을 가져온다고 가정 (또는 전체 목록 서비스 호출)
        return supportTicketService.getTicketList(); 
    }
    
    @PostMapping(value = "/updateStatus", produces = "application/json; charset=UTF-8")
    @ResponseBody
    public ResponseEntity<String> updateStatus(@RequestBody SupportTicketVO vo) {
        try {
            supportTicketService.updateTicketStatus(vo.getId());
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("fail");
        }
    }
  
}