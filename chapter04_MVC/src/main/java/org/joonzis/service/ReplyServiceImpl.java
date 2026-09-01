package org.joonzis.service;

import java.util.List;

import org.joonzis.domain.ReplyVO;
import org.joonzis.mapper.ReplyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j;

@Log4j
@Service
public class ReplyServiceImpl implements ReplyService{

	@Autowired
	private ReplyMapper mapper;
	
	@Override
	public ReplyVO get(int rno) {
		log.info("service get..." + rno);
		return mapper.read(rno);	
	}
	@Override
	public List<ReplyVO> getList(int bno) {
		log.info("service getList..." + bno);
		return mapper.getList(bno);
	}
	@Override
	public int modify(ReplyVO vo) {
		log.info("service modify..." + vo);
		return mapper.update(vo);
	}
	@Override
	public int register(ReplyVO vo) {
		log.info("service register..." + vo);
		return mapper.insert(vo);
	}
	@Override
	public int remove(int rno) {
		log.info("service remove..." + rno);
		return mapper.delete(rno);
	}
	
	
}
