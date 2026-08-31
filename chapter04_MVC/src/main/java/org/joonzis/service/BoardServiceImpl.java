package org.joonzis.service;

import java.util.List;

import org.joonzis.domain.BoardVO;
import org.joonzis.domain.Criteria;
import org.joonzis.mapper.BoardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j;

@Log4j
@Service
public class BoardServiceImpl implements BoardService{
	
	@Autowired
	private BoardMapper mapper;
	
	@Override
	public List<BoardVO> getList(Criteria cri){
		log.info("service getList...");
		return mapper.getList(cri);
	}
	@Override
	public BoardVO get(int bno) {
		log.info("service get...");
		return mapper.read(bno);
	}
	@Override
	public void register(BoardVO vo) {
		log.info("service register...");
		mapper.insert(vo);
	}

	@Override
	public boolean remove(int bno) {
		log.info("service remove...");
		int result = mapper.delete(bno);
		return result == 1;
	}
	@Override
	public boolean modify(BoardVO vo) {
		log.info("service modify...");
		int result = mapper.update(vo);
		if(result ==1) {
			return true;
		}else {
			return false;
		}
	}
	
	@Override
	public int getTotal() {
		return mapper.getTotal();		
	}
	
	
}
