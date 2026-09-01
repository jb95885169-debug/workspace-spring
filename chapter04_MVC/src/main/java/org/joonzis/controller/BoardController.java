package org.joonzis.controller;

import org.joonzis.domain.BoardVO;
import org.joonzis.domain.Criteria;
import org.joonzis.domain.PageDTO;
import org.joonzis.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.log4j.Log4j;

@Controller // <서블릿으로 만들어줌	
@Log4j
@RequestMapping("/board/*")
public class BoardController {

	@Autowired
	private BoardService service;
	
	
//	1. 전체 게시글
	@GetMapping("/list")	// get방식, post 방식이면 포스트맵핑
	public String list(Model model, Criteria cri) {
		log.info("list...");
		
		if(cri.getPageNum()==0 && cri.getAmount()==0) {
			// 무조건 첫 번째 페이지로 와야하는 경우 cri객체를 던져주지 않아도 된다.
			cri.setPageNum(1);
			cri.setAmount(10);
		}
		
		
		
		// 해당 페이지에 맞는 데이터 가져오기
		model.addAttribute("list", service.getList(cri));
		
		// 페이징을 위한 과정
		// 1. 게시글 전체 개수 가져오기
		int total = service.getTotal();
		model.addAttribute("pageMaker", new PageDTO(cri,total));
		
		
		return "board/list";
	}	// 데이터를 가져가야하기 때문에 포워드 방식
	
//	2. 게시글 등록 화면 이동
	@GetMapping("/register")
	public String register2() {
		log.info("register...  : "  );
		return "/board/register"; 
	}
//	2. 게시글 등록
	@PostMapping("/register")
	public String register(BoardVO vo) {
		log.info("register...  : " + vo );
		service.register(vo);
		return "redirect:/board/list"; 
	}
	
//	3. 게시글 조회
	@GetMapping({"/get", "/modify"})	// 같은 코드를 두번쓰는 걸 방지하기 위해 /get, /modify을 배열처리
	public void get(@RequestParam("bno") int bno, Model model) {
		log.info("get...  : " + bno );
		model.addAttribute("vo", service.get(bno));
		
	}
//	4-2. 게시글 수정 페이지로 이동?
//	@GetMapping("/modify")
//	public String getModify(@RequestParam("bno") int bno, Model model) {
//		log.info("get...  : " + bno );
//		model.addAttribute("vo", service.get(bno));
//		return "/board/modify";
//		
//	};
	
//	4-1. 게시글 수정 - modify
	@PostMapping("/modify")
	public String modify(BoardVO vo) {
		log.info("modify...  : " + vo );  //<파라미터가 잘들어왔는지 확인하기위한 로그
		if(service.modify(vo)) {
			log.info("게시글 수정 성공");
		}
//		service.modify(vo);	// 위에는  modify가 불리안타입으로 받았기때문에
		return "redirect:/board/list"; 
	}
	
	
	
	
	
//	5. 게시글 삭제 - remove
	@PostMapping("/remove")
	public String remove(@RequestParam("bno") int bno) {
		log.info("remove...  : " + bno );
		if(service.remove(bno)) {
			log.info("게시글 삭제 성공");
		}
//		service.remove(bno);
		return "redirect:/board/list"; 
	}
	
	
}









