package org.joonzis.mapper;

import java.util.List;

import org.joonzis.domain.ReplyVO;

//1 . ReplyMapper 메소드 작성 
//2 . ReplyMapper.xml 쿼리 작성
//3 . ReplyMapper 테스트

public interface ReplyMapper {
	// 댓글 삽입 - insert 
	public int insert(ReplyVO vo);
	// 댓글 목록 - getList 
	public List<ReplyVO> getList(int bno);
	// 댓글 읽기 - read
	public ReplyVO read(int rno);
	// 댓글 삭제 - delete 
	public int delete(int rno);
	// 댓글 수정 - update - 내용, 수정 날짜 만 변경 
	public int update(ReplyVO vo);
	
	
	
}
