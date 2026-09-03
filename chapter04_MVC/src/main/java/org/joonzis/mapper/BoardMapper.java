package org.joonzis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
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
	// 게시글 전체 개수
	public int getTotal();
	// 댓글에 의한 댓글 개수 데이터 변경
	public void updateReplyCnt(
			@Param("bno")int bno, @Param("amount") int amount);
	
	/*
	 * 댓글이 등록되면 1이증가, 댓글이 삭제되면 1이 감소
	 * @Param 어노테이션을 이용하여 다중 파라미터 전달 가능
	 * */
	
}
