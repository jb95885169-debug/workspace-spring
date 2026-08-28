package org.joonzis.service;

import java.util.List;

import org.joonzis.domain.BoardVO;

public interface BoardService {
	// 전체리스트
	public List<BoardVO> getList() ;
	// 데이터 삽입
	public void register(BoardVO vo);
	// 단일 데이터
	public BoardVO get(int bno);
	// 데이터 삭제
	public boolean remove(int bno);
	// 데이터 수정 
	public boolean modify(BoardVO vo);
}
