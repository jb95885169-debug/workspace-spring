package org.joonzis.mapper;

import java.util.List;

import org.joonzis.domain.BoardVO;
import org.joonzis.domain.Criteria;

public interface BoardMapper {
	// 전체 리스트
	public List<BoardVO> getList(Criteria cri);
	// 데이터 삽입 - insert
	public int insert(BoardVO vo);
	// 단일 데이터 가져오기 - read
	public BoardVO read(int bno);
	// 데이터 삭제 - delete
	public int delete(int bno);
	// 데이터 수정 - update (제목, 내용, 작성자, 수정날짜 변경)
	public int update(BoardVO vo);
	
	public int getTotal();
	

	
}
