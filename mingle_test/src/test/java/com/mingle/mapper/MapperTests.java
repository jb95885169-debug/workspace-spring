package com.mingle.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.mingle.dto.DashboardStatsResponse;
import com.mingle.vo.FeedVO;
import com.mingle.vo.ProductVO;
import com.mingle.vo.ReportVO;
import com.mingle.vo.SupportTicketVO;
import com.mingle.vo.UserVO;

import lombok.extern.log4j.Log4j;

@Log4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/test-context.xml")
public class MapperTests {
	@Autowired
	FeedMapper feedMapper;

	@Autowired
	UserMapper userMapper;

	@Autowired
	SupportTicketMapper supportTicketMapper;

	@Autowired
	ReportMapper reportMapper;

	@Autowired
	SubscriptionMapper subscriptionMapper;

	@Autowired
	DashboardMapper dashboardMapper;
	
//	@Test
//	public void InsertTest() {
//		FeedVO vo = new FeedVO();
//		vo.setTitle("테스트제목");
//		vo.setCategory("NORMAL");
//		vo.setUserId(1);
//		vo.setContent("11");
//		feedMapper.insertFeed(vo);
//	}

	/**
	 * 관리자 계정 상태 변경 테스트.
	 * 테스트 종료 시 트랜잭션이 롤백되므로 실제 회원 상태는 바뀌지 않는다.
	 */
//	@Test
//	@Transactional
//	public void selectUsersAndUpdateStatusTest() {
//		List<UserVO> users = userMapper.selectUsers();
//
//		assertNotNull(users);
//		assertFalse(users.isEmpty());
//
//		int userId = users.get(0).getId();
//		UserVO before = userMapper.selectUserById(userId);
//		String originalStatus = before != null && before.getStatus() != null ? before.getStatus() : "ACTIVE";
//		String targetStatus = "BANNED";
//
//		int updatedCount = userMapper.updateUserStatus(userId, targetStatus);
//		UserVO updatedUser = userMapper.selectUserById(userId);
//
//		assertEquals(1, updatedCount);
//		assertNotNull(updatedUser);
//		assertEquals(targetStatus, updatedUser.getStatus());
//
//		userMapper.updateUserStatus(userId, originalStatus);
//	}
	/*	관리자 페이지 첫 화면에서 서비스 현황을 직관적으로 파악할 수 있는 지표입니다.
	 * 	오늘 가입한 신규 회원 수, 미처리된 1:1 문의 건수 (PENDING), 대기 중인 신고 건수,
	 * 	일별/월별 총 매출액 및 결제 건수
	 * */
	@Test
	@Transactional(readOnly = true)
	public void dashboardStatsTest() {
		DashboardStatsResponse stats = dashboardMapper.selectDashboardStats();

		assertNotNull(stats);
		log.info("dashboard stats => " + stats);
	}

	/**
	 * 관리자 문의 답변 테스트.
	 * ticketId는 STATUS가 PENDING인 문의 ID로 바꿔서 실행한다.
	 * @Transactional 때문에 테스트 종료 시 답변, 상태, 답변일은 모두 롤백된다.
	 */
//	@Test
//	@Transactional
//	public void answerSupportTicketTest() {
//		long ticketId = 9;// < DB에 들어가있는 문의에 대해 테스트 
//		//Transactional주석처리하고 실제 db에 넣으면다음번에 테스트할때 이미 처리된 문의라 오류남 
//		String answer = "문의 내용을 확인했습니다. 처리 완료했습니다.";
//
//		int updatedCount = supportTicketMapper.answerTicket(ticketId, answer);
//		SupportTicketVO ticket = supportTicketMapper.selectTicketById(ticketId);
//
//		org.junit.Assert.assertEquals(1, updatedCount);
//		org.junit.Assert.assertNotNull(ticket);
//		org.junit.Assert.assertEquals("RESOLVED", ticket.getStatus());
//		org.junit.Assert.assertEquals(answer, ticket.getAnswer());
//		org.junit.Assert.assertNotNull(ticket.getAnsweredAt());
//	}

	/**
	 * 관리자 신고 처리 테스트.
	 * reportId는 실제 신고 ID로 바꿔서 실행한다.
	 * 테스트 종료 후 트랜잭션이 롤백되므로 변경 내용은 DB에 남지 않는다.
	 */
//	@Test
//	@Transactional
//	public void updateReportStatusTest() {
//		long reportId = 10;
//		String status = "RESOLVED";
//		
//		int updatedCount = reportMapper.updateReportStatus(reportId, status);
//		ReportVO report = reportMapper.selectReportById(reportId);
//
//		org.junit.Assert.assertEquals(1, updatedCount);
//		org.junit.Assert.assertNotNull(report);
//		org.junit.Assert.assertEquals(status, report.getStatus());
//		org.junit.Assert.assertNotNull(report.getProcessedAt());
//	}

	/**
	 * 관리자 상품 가격 / 설명 수정 테스트.
	 * productId는 실제 mingle_products의 상품 ID로 바꿔서 실행한다.
	 * @Transactional 때문에 테스트가 끝나면 상품 정보는 원래 값으로 롤백된다.
	 */
	// @Test
	// @Transactional
	// public void updateProductPriceAndDescriptionTest() {
	// 	int productId = 1;
	// 	int price = 19900;
	// 	String description = "관리자가 수정한 상품 설명입니다.";

	// 	int updatedCount = subscriptionMapper.updateProductPriceAndDescription(
	// 			productId, price, description);
	// 	ProductVO product = subscriptionMapper.selectProductById(productId);

	// 	org.junit.Assert.assertEquals(1, updatedCount);
	// 	org.junit.Assert.assertNotNull(product);
	// 	org.junit.Assert.assertEquals(price, product.getPrice());
	// 	org.junit.Assert.assertEquals(description, product.getDescription());
	// }
	
}
