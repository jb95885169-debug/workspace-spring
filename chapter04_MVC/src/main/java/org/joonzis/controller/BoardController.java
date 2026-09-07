package org.joonzis.controller;

import java.util.List;

import org.joonzis.domain.BoardAttachVO;
import org.joonzis.domain.BoardVO;
import org.joonzis.domain.Criteria;
import org.joonzis.domain.PageDTO;
import org.joonzis.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequestMapping("/board/*")
public class BoardController {

	@Autowired
	private BoardService service;
	
	// 1. 전체 게시글
	@GetMapping("/list")
	public String list(Model model, Criteria cri) {
		log.info("list...");
		
		if(cri.getPageNum() == 0 && cri.getAmount() == 0) {
			// 무조건 첫 번째 페이지로 와야하는 경우 cri객체를 던져주지 않아도 된다.
			cri.setPageNum(1);
			cri.setAmount(10);
		}

		// 해당 페이지에 맞는 데이터 가져오기
		model.addAttribute("list", service.getList(cri));
		
		// 페이징을 위한 과정
		// 1. 게시글 전체 개수 가져오기
		int total = service.getTotal();
		model.addAttribute("pageMaker", new PageDTO(cri, total));
		
		return "board/list";
	}
	
	@GetMapping("/register")
	public String register2() {
		log.info("register ... ");
		return "/board/register";
	}
	// 2. 게시글 등록
	@PostMapping("/register")
	public String register(BoardVO vo) {
		log.info("register ...  : " + vo);
		service.register(vo);
		
		if(vo.getAttachList() != null) {
			vo.getAttachList().forEach(attach -> log.info(attach));
		}
		
		return "redirect:/board/list";
	}
	
	// 3. 게시글 조회
	@GetMapping({"/get", "/modify"})
	public void get(@RequestParam("bno") int bno, Model model) {
		log.info("get.. : " + bno);
		model.addAttribute("vo", service.get(bno));
	}
	
	
	// 4. 게시글 수정 - modify
	@PostMapping("/modify")
	public String modify(BoardVO vo) {
		log.info("modify.. : " + vo);
		
		if(service.modify(vo)) {
			log.info("게시글 수정 성공");
		}
		return "redirect:/board/list";
	}
	
	// 5. 게시글 삭제 - remove
	@PostMapping("/remove")
	public String remove(@RequestParam("bno") int bno) {
		log.info("remove.. : " + bno);
		if(service.remove(bno)) {
			log.info("게시글 삭제 성공");
		}
		return "redirect:/board/list";
	}


	@ResponseBody
	@GetMapping(value = "/getAttachList/{bno}",
				produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<BoardAttachVO>> getAttachList(
			@PathVariable("bno") int bno){
		log.info("getAttachList....: " + bno);
		return new ResponseEntity<List<BoardAttachVO>>(
				service.getAttachList(bno), HttpStatus.OK);
	}
	
	
	
	
}












