package org.joonzis.service;

import java.util.List;

import org.joonzis.domain.BoardVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.log4j.Log4j;



@Log4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
		"file:src/main/webapp/WEB-INF/spring/root-context.xml")
public class BoardServiceTests {
	
	@Autowired
	private BoardService service;
	
//	@Test
//	public void testGetList() {
//		List<BoardVO> list = service.getList();
//		
//		for (BoardVO vo : list) {
//            log.info(vo);
//        }	
//	}
	
	// 데이터 삽입
//	@Test
//	public void testregister() {
//		BoardVO vo = new BoardVO();
//		vo.setTitle("레지스터 삽입");
//		vo.setContent("레지스터내용");
//		vo.setWriter("레지스터 작성자");
//		
//		service.register(vo);
//	}
	
	// 데이터 삭제
//	@Test
//	public void testremove() {		
//		service.remove(11);
//	}
	
	// 데이터 수정
//	@Test
//	public void testmodify() {
//		BoardVO vo = new BoardVO();
//		vo.setBno(9);
//		vo.setTitle("레지스터 수정");
//		vo.setContent("수정내용");
//		vo.setWriter("작성자 수정");
//		
//		service.modify(vo);
//	}
	// 단일데이터 가져오기
	@Test
	public void testget() {
		BoardVO vo = service.get(10);
		log.info(vo);
	}
	
	
}
