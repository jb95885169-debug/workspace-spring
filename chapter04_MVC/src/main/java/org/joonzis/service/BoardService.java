package org.joonzis.service;

import java.util.List;

import org.joonzis.domain.BoardAttachVO;
import org.joonzis.domain.BoardVO;
import org.joonzis.domain.Criteria;

public interface BoardService {
	// 전체리스트
//	public List<BoardVO> getList() ;
	
	public List<BoardVO> getList(Criteria cri) ;
	// 데이터 삽입
	public void register(BoardVO vo);
	// 단일 데이터
	public BoardVO get(int bno);
	// 데이터 삭제
	public boolean remove(int bno);
	// 데이터 수정 
	public boolean modify(BoardVO vo);
	// 게시글 전체 개수
	public int getTotal();
	// 첨부 파일 리스트
	public List<BoardAttachVO> getAttachList(int bno);
	
	
}
