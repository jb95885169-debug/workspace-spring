package org.joonzis.mapper;

import java.util.List;

import org.joonzis.domain.BoardVO;
import org.joonzis.domain.ReplyVO;
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
public class ReplyMapperTests {
	
	@Autowired
	private ReplyMapper mapper;
	
//	@Test
//	public void testGetList() {
//		List<ReplyVO> list = mapper.getList(1);
//		for(ReplyVO vo : list) {
//			log.info(vo);
//		}
//	}
	
//	@Test
//	public void testInsert() {
//		ReplyVO vo = new ReplyVO();
//		vo.setBno(1);
//		vo.setReply("테스트 내용");
//		vo.setReplyer("테스터");
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
//		ReplyVO vo = new ReplyVO();
//		vo.setRno(1);
//		vo.setReply("변경 테스트");
//		
//		int count = mapper.update(vo);
//		log.info("update count : " + count);
//	}
	
//	@Test
//	public void testDelete() {
//		int result = mapper.delete(1);
//		log.info("delete count : " + result);
//	}
	
}







