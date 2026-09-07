package org.joonzis.service;

import java.util.List;

import org.joonzis.domain.BoardAttachVO;
import org.joonzis.domain.BoardVO;
import org.joonzis.domain.Criteria;
import org.joonzis.mapper.BoardAttachMapper;
import org.joonzis.mapper.BoardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j;

@Log4j
@Service
public class BoardServiceImpl implements BoardService {

	@Autowired
	private BoardMapper mapper;
	
	@Autowired
	private BoardAttachMapper attachMapper;
	
	@Override
	public List<BoardVO> getList(Criteria cri) {
		log.info("service getList...");
		return mapper.getList(cri);
	}
	
	@Override
	public BoardVO get(int bno) {
		log.info("service get...");
		return mapper.read(bno);
	}
	
	// 게시글 등록 + 첨부 파일 등록
	@Transactional
	@Override
	public void register(BoardVO vo) {
		log.info("service register..." + vo);
		
		// 1. tbl_board 테이블에 게시글 등록
		mapper.insert(vo);
		
		// 2. 등록된 게시글 번호 가져오기
		int currentBno = mapper.getBno();
		
		// 3. tbl_attach 테이블에 파일들 등록
		// 첨부파일이 존재하면, 등록
		if(vo.getAttachList() != null && vo.getAttachList().size() > 0) {
			vo.getAttachList().forEach(attach -> {
				attach.setBno(currentBno);
				attachMapper.insert(attach);
			});
		}
		
	}
	
	@Override
	public boolean remove(int bno) {
		log.info("service remove..." + bno);
		int result = mapper.delete(bno);
		return result == 1;
	}
	
	@Override
	public boolean modify(BoardVO vo) {
		log.info("service modify..." + vo);
		int result = mapper.update(vo);
		if(result == 1) {
			return true;
		}else {
			return false;
		}
	}
	
	@Override
	public int getTotal() {
		return mapper.getTotal();
	}
	
	@Override
	public List<BoardAttachVO> getAttachList(int bno) {
		log.info("get attach list by bno ... : "  + bno);
		return attachMapper.findByBno(bno);
	}
	
	
}













