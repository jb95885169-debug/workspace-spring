package org.joonzis.mapper;

import java.util.List;

import org.joonzis.domain.BoardVO;
import org.joonzis.domain.ReplyVO;
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
public class ReplyMapperTest {

//	@Autowired
//	private ReplyMapper mapper;
	// 댓글 삽입 - insert 
		
//		@Test
//		public void testInsert() {
//		
//			ReplyVO vo = new ReplyVO();
//			
//			// vo 만들고 데이터 담고 전달
//			vo.setBno(10);
//			vo.setReply("테스트댓글");
//			vo.setReplyer("테스트작성자");
//			// result의 값에 따라서 성공 실패 확인
//			int result = mapper.insert(vo);
//			if(result>0) {
//				log.info("성공");			
//			}else {
//				log.info("실패");
//			}
//	}
//			seq_board.nextval,
//			#{bno},
//			#{reply},
//			#{replyer},
//			sysdate,
//			sysdate
		// 위에는 insert의 쿼리문인데 #{bno},#{reply},#{replyer}3가지 값을 받아줘야한다
		
		
		
		
	// 댓글 목록 - getList 
//		@Test
//	public void testGetList(){
//		
//		List<ReplyVO> list = mapper.getList(10);
//		for(ReplyVO vo : list) {
//			log.info(vo);
//		}
//		
//	}
	
	// 댓글 읽기 - read
//	@Test
//	public void testread() {
//		ReplyVO vo = mapper.read(10);
//		log.info(vo);
//	}

	
	
	
	
//	// 댓글 삭제 - delete 
//	public int delete(int bno);
//	@Test
//	public void testdelete() {
//		
//		int result = mapper.delete(10);			
//		log.info("delete 결과 : " + result);
//	}
	
	
	
//	// 댓글 수정 - update - 내용, 수정 날짜 만 변경 
//	public int update(ReplyVO rvo);
//		
	
//	@Test
//	public void testupdate() {
//		ReplyVO vo = new ReplyVO();
//		vo.setRno(10);
//		vo.setReply("수정댓글");
//
//		int count = mapper.update(vo);
//		log.info("update count : " + count);
//	
	};
	
	
	
	
	
	
	
	
	
	
	
	
	
