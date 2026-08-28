package org.joonzis.controller;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.joonzis.dto.StudentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	// @RequestMapping : url-mapping
	// 메소드를 대상으로 어노테이션을 붙인다.
	// value="/" : 컨텍스트 패스를 의미, 서버:포트/디폴트패키지
	// method = RequestMethod.GET : get/post 방식
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		String formattedDate = dateFormat.format(date);
		model.addAttribute("serverTime", formattedDate );
		
		return "member/input"; // 반드시 string 리턴
//		"home" 이라고만 써있지만 home.jsp를 리턴하고있음
	}
	
	
	@RequestMapping(value = "member/result", method = RequestMethod.POST)
	public String login(@RequestParam("id") String id,
						@RequestParam("pw") String pw,
						Model model) {
		
		model.addAttribute("id", id);
		model.addAttribute("pw", pw);
		
		return "member/output";
		// 기본 방식이 forward 방식
	}
	// 주소에 http://localhost:8080/a/b/c/d/e 을 입력시 view01.jsp로 이동
	// @RequestMapping("a/b/c/d/e")value 생략 가능
	@RequestMapping(value = "a/b/c/d/e")
	public String goView01() {
		// 1. 리턴 타입 : 뷰(view)를 리턴하기 때문 에 언제나 String을 리턴
		// 2. 메소드 명 : goView01은 아무런 의미가 없다
		// 3. 리턴 :  "/view01", "view01"의 차이점은 없다
		return "/view01";
	}
		
	/*
	 * Model 클래스
	 * 1. request 의 attribute 역할을 수행
	 * 2. addAttribute("속성명","값") 방식으로 저장
	 * 3. controller가 jsp에게 파라미터를 전달하려면 무조건 model사용
	 * 	(스프링 2버전 전에는 ModelAndView 사용)
	 * 
	 * */		
	
	@RequestMapping("admin/view02")
	public String goView2(Model model) {
		model.addAttribute("id", "admin");
		model.addAttribute("pw", "1234");
		return "admin/view02";
	}
	
	// index 로 요청시 index.jsp로 가능 goIndex 메소드 생성
	
	@RequestMapping("index")
//	public String goIndex() {
//		return "index";
//	}
	public void goIndex() {}
	// 들어오는 경로와 나가는 경로가 똑같다면 String > Index로 처리할수 있음
	
	// goResult1 메소드 - v01의 요청 처리 - result.jsp로 이동
	@RequestMapping(value = "v01", method = RequestMethod.POST)
	public String goResult1(StudentDTO sDto, Model model) {
		model.addAttribute("sDto", sDto);
		return "result";
	}
	
	// goResult2 메소드 동기 방식
	@RequestMapping(value = "v02", method = RequestMethod.POST)
	public String goResult2(@ModelAttribute("s") StudentDTO sDto) {
		return "result";
	}
	
}
