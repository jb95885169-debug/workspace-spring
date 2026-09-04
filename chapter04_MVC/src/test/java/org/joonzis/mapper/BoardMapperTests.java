package org.joonzis.mapper;

import java.util.List;

import org.joonzis.domain.BoardAttachVO;
import org.joonzis.domain.BoardVO;
import org.joonzis.persistence.DataSourceTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



import lombok.extern.log4j.Log4j;

@Log4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
		"file:src/main/webapp/WEB-INF/spring/root-context.xml")// < 컨테이너

public class BoardMapperTests {
	
	@Autowired
	private BoardMapper mapper;
	
//	@Test
//	public void testGetList() {
//		List<BoardVO> list = mapper.getList();
//		for(BoardVO vo : list) {
//			log.info(vo);
//		}
//		
//	}
	
	// 데이터 삽입
//	@Test
//	public void testInsert() {
//		BoardVO vo = new BoardVO();
////		BoardAttachVO avo = new BoardAttachVO();
//		// vo 만들고 데이터 담고 전달
//	
//		
//		vo.setTitle("테스트제목");
//		vo.setContent("테스트내용");
//		vo.setWriter("테스트작성자");
//		// result의 값에 따라서 성공 실패 확인
//		int result = mapper.insert(vo);
//		if(result>0) {
//			log.info("성공");			
//		}else {
//			log.info("실패");
//		}
//	}
//	
	// 단일 데이터 가져오기
	@Test
	 public void testread() {
		BoardVO vo = mapper.read(6);
		log.info(vo);
	 }
	
	// 데이터 삭제
	@Test
	public void testdelete() {
		
		int result = mapper.delete(6);			
		log.info("delete 결과 : " + result);
	}
	
	// 데이터 수정
	@Test
	public void testupdate() {
		BoardVO vo = new BoardVO();
		vo.setBno(7);
		vo.setTitle("수정제목");
		vo.setContent("수정내용");
		vo.setWriter("수정작성자");

		int count = mapper.update(vo);
		log.info("update count : " + count);
		
//		int result = mapper.insert(vo);
//		if(result>0)
//			log.info("INSERT 결과 : " + result);
		
	}
	
	
	
	
	
}










