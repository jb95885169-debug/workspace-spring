package com.mingle.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mingle.dto.PaymentHistoryResponse;
import com.mingle.dto.ProductResponse;
import com.mingle.dto.SubscriptionResponse;
import com.mingle.vo.PaymentVO;
import com.mingle.vo.ProductVO;
import com.mingle.vo.SubscriptionVO;

/**
 * 구독 상품 / 결제 / 구독 이력
 * 세 테이블이 한 흐름(상품 고르기 → 결제 → 구독 생성)이라 한 매퍼에 둔다.
 */
@Mapper
public interface SubscriptionMapper {

	// 판매 중인 상품 목록
	List<ProductResponse> selectActiveProducts();

	// 판매 중인 상품 한 개 (없거나 판매 중지면 null)
	ProductVO selectActiveProduct(int productId);

	// 관리자 상품 관리용 조회 (판매 중지 상품도 포함)
	ProductVO selectProductById(int productId);

	// 관리자 상품 가격 / 설명 수정 (결제 내역의 상품명·금액은 수정하지 않는다)
	int updateProductPriceAndDescription(
			@Param("productId") int productId,
			@Param("price") int price,
			@Param("description") String description);

	// 결제 INSERT (selectKey로 payment.id가 채워짐)
	int insertPayment(PaymentVO payment);

	// 구독 INSERT (결제와 같은 트랜잭션)
	int insertSubscription(SubscriptionVO subscription);

	// 이용 중인 구독 즉시 종료 (업그레이드할 때 남은 기간을 새 등급으로 넘긴다)
	int updateSubscriptionEnded(int userId);

	// 지금 유효한 구독 (없으면 null = BASIC)
	SubscriptionResponse selectCurrentSubscription(int userId);

	// 내 결제 / 구독 내역 (최근 결제 순)
	List<PaymentHistoryResponse> selectPaymentHistory(int userId);
}
