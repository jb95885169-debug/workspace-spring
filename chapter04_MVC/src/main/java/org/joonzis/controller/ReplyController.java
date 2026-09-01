package org.joonzis.controller;

import java.util.List;

import org.joonzis.domain.ReplyVO;
import org.joonzis.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j;

@Log4j
@RequestMapping("/reply")
@RestController		// 비동기방식
public class ReplyController {
	
	/*
	 * 1. 등록 - /reply/new - POST
	 * 2. 조회 - /reply/:rno -GET
	 * 3. 삭제 - /reply/:rno - DELETE
	 * 4. 수정 - /reply/:rno - PUT or PATCH
	 * 5. 전체 댓글 - /reply/pages/:bno - GET
	 * */	
	
	@Autowired
	private ReplyService service;
	
	// 1. 등록
	// consumes = 수신할 데이터 포맷
	// produces = 송신할 데이터 포맷
	@PostMapping(value = "/new",
				consumes = "application/json",
				produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> create(@RequestBody ReplyVO vo){ 	//@RequestBody
		log.info("ReplyVO : " + vo );
		
		int insertCount = service.register(vo);
		
		return insertCount == 1?
				new ResponseEntity<String>("success", HttpStatus.OK) : 
				new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		
		
		
		
	}
	
	// 2. 목록
	@GetMapping(value = "/pages/{bno}",
				produces = {MediaType.APPLICATION_XML_VALUE,
							MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<List<ReplyVO>> getList(
							@PathVariable("bno") int bno){
		log.info("getList... " + bno);
		return new ResponseEntity<>(
				service.getList(bno),HttpStatus.OK);
	}

	// 3. 댓글 조회
	@GetMapping(value = "/{rno}",
				produces = {MediaType.APPLICATION_XML_VALUE,
							MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<ReplyVO> get(
			@PathVariable("rno")int rno) {
		log.info("get..." + rno);
		return new ResponseEntity<>(
				service.get(rno), HttpStatus.OK);
	}
	
	// 4. 삭제
	@DeleteMapping(value = "/{rno}",
					produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> remove(
				@PathVariable("rno") int rno) {
		log.info("remove..." +  rno);
		
		return service.remove(rno)==1 ? 
				new ResponseEntity<>("success", HttpStatus.OK) : 
				new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	// 5. 수정
	@RequestMapping(method = {RequestMethod.PUT, RequestMethod.PATCH},
							value = "/{rno}",
							consumes = "application/json",
							produces = MediaType.TEXT_PLAIN_VALUE)
	
	public ResponseEntity<String> modify(@RequestBody ReplyVO vo,
											@PathVariable("rno") int rno) {
		log.info("modify... vo : " + vo);
		log.info("modify... rno : " + rno);
		
		vo.setRno(rno);
		
		int modifyCount = service.modify(vo);
		log.info("Reply Modify Count : " + modifyCount);
		
		return modifyCount==1 ? 
				new ResponseEntity<>("success", HttpStatus.OK) : 
				new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
	
	
	
	
}











