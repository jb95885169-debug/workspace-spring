package org.joonzis.service;

import java.util.List;

import org.joonzis.domain.ReplyVO;
import org.joonzis.mapper.BoardMapper;
import org.joonzis.mapper.ReplyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j;

@Log4j
@Service
public class ReplyServiceImpl implements ReplyService{

	@Autowired
	private ReplyMapper mapper;
	@Autowired
	private BoardMapper boardMapper;
	
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
	
	
	
	// 댓글 추가 미 삭제 시 댓글 개수 변경 같이 진행 - 트랜젝션 처리
	@Transactional
	@Override
	public int register(ReplyVO vo) {
		log.info("service register..." + vo);
		
		// 1. 댓글 추가
		int result = mapper.insert(vo);
		
		// 2. 댓글 개수 변경
		boardMapper.updateReplyCnt(vo.getBno(), 1);
		
		return result;
	}
	
	
	
	@Transactional
	@Override
	public int remove(int rno) {
		log.info("service remove..." + rno);
		
		// rno를이용해서 vo 가져오고, vo를 이용해서 bno가져오기
		ReplyVO vo = mapper.read(rno);
		
		// 1. 댓글 개수 변 경(1감소)
		boardMapper.updateReplyCnt(vo.getBno(), -1);
		
		// 2. 댓글 삭제
		int result  = mapper.delete(rno);
		
		
		return result;
	}
	
	
}







