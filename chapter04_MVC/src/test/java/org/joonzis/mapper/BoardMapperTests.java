package org.joonzis.mapper;

import java.util.List;

import org.joonzis.domain.BoardVO;
import org.joonzis.persistence.DataSourceTests;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.log4j.Log4j;

@Log4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
		"file:src/main/webapp/WEB-INF/spring/root-context.xml"
)
public class BoardMapperTests {
	
	@Autowired
	private BoardMapper mapper;
	
//	@Test
//	public void testGetList() {
//		List<BoardVO> list = mapper.getList();
//		for(BoardVO vo : list) {
//			log.info(vo);
//		}
//	}
	
//	@Test
//	public void testInsert() {
//		BoardVO vo = new BoardVO();
//		vo.setTitle("삽입 테스트  제목");
//		vo.setContent("삽입 테스트 내용");
//		vo.setWriter("삽입 테스터");
//		int result = mapper.insert(vo);
//		
//		if(result > 0) { log.info("성공");}
//		else { log.info("실패");} 
//	}
	
//	@Test
//	public void testRead() {
//		BoardVO vo = mapper.read(6);
//		log.info(vo);
//	}
	
//	@Test
//	public void testUpdate() {
//		BoardVO vo = new BoardVO();
//		vo.setBno(6);
//		vo.setTitle("수정 테스트  제목");
//		vo.setContent("수정 테스트 내용");
//		vo.setWriter("수정 테스터");
//		
//		int count = mapper.update(vo);
//		log.info("update count : " + count);
//	}
	
//	@Test
//	public void testDelete() {
//		int result = mapper.delete(6);
//		log.info("delete count : " + result);
//	}
	
}







