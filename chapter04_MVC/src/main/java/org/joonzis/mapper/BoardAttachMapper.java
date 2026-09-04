package org.joonzis.mapper;

import java.util.List;

import org.joonzis.domain.BoardAttachVO;

public interface BoardAttachMapper {
	// 첨부 파일은 수정이라는 개념이 존재하지 않기 때문에
	// 수정하려면 삭제 후 다시 삽입하는 과정을 거친다
	public void insert(BoardAttachVO vo);
	public void delete(String uuid);
	public List<BoardAttachVO> findByBno(int bno);

}
