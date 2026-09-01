package org.joonzis.cotroller;

import org.joonzis.domain.TestVO;
import org.joonzis.domain.Ticket;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j;

@Log4j
@RequestMapping("/test")
@RestController	// 클래스 단위, 메소드단위는 @responsebody 
public class TestController {
					
	@GetMapping(value = "/getText", produces = "text/plain; charset=utf-8") // produces= 리턴할데이터의 타입
	public String getText() {
		log.info("Mime type" + MediaType.TEXT_PLAIN_VALUE);
		
		// 기존 jsp 파일의 이름이 아닌 순수 데이터를 전달
		return "안녕하세요";

	}
	
//	@GetMapping(value = "/getObject", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, 
//													MediaType.APPLICATION_XML_VALUE})
//	public TestVO getObject() {
//		return new TestVO(100,"kim");
//	}
	
	/* 
	 * 메소드를 만들고 URL에 맞게 요청해서 결과를 확인(json, xml)
	 * 1. 요청 URL : /test/check
	 * 2. 파라미터  : 실수형 age
	 * 3. 리턴 타입 : TestVO
	 * 	- vo 객체를 생성
	 *  - no 필드는 0으로 고정
	 *  - 전달 받은 age를 문자열 name 필드에 저장
	 * */
	@GetMapping(value = "/check", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, 
												MediaType.APPLICATION_XML_VALUE})//produces 생략가능
	
	public ResponseEntity<TestVO> check(double age) {	//ResponseEntity<> 데이터와 통신에 대한 상태값을 실어서 보냄 (404 405 500등등의 오류들)
														//ResponseEntity<> << 제네릭
		TestVO vo = new TestVO(0,"" + age);
		
		ResponseEntity<TestVO> result = null;
		if(age >150) {
			result = ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(vo);
		}else {
			result = ResponseEntity.status(HttpStatus.OK).body(vo);
		}
		
		return result;
	}
	
	@GetMapping("/product/{cat}/{pid}")	//경로 처럼 실어서 보냄 @PathVariable("cat")으로 값을 담아서 보내야함
	public String[] getPath( 
			@PathVariable("cat") String cat,
			@PathVariable("pid") int pid) {
		
		return new String[] {"category : " + cat + ", " + "productId : " + pid};
		// 파라미터를 던지고 받는 방식이 아님, 리턴에서 파라미터 값을 정함
	}
		
	// @RequestBody : 요청 데이터를 자바에서 사용가능한 객체로 변환 << 제이슨방식을 자바오브젝트로 변환
	// 주로 json to java object    
	@PostMapping("/ticket")
	public Ticket convert(@RequestBody Ticket t) {
		log.info("convert... ticket : " + t);
		return t;
	}
		

	
	
	
	
	
}












