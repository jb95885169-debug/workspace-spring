package com.mingle.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mingle.dto.PaymentHistoryResponse;
import com.mingle.dto.PaymentRequest;
import com.mingle.dto.ProductResponse;
import com.mingle.dto.SubscriptionResponse;
import com.mingle.mapper.SubscriptionMapper;
import com.mingle.type.SubscriptionTier;
import com.mingle.vo.PaymentVO;
import com.mingle.vo.ProductVO;
import com.mingle.vo.SubscriptionVO;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private static final long DAY_MILLIS = 24L * 60 * 60 * 1000;

    @Autowired
    private SubscriptionMapper subscriptionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProducts(int userId) {

        SubscriptionResponse current = getMySubscription(userId);
        List<ProductResponse> products = subscriptionMapper.selectActiveProducts();

        // 상품마다 이 회원이 살 수 있는지와 낼 금액을 채운다 (화면 버튼 문구에 쓴다)
        for (ProductResponse product : products) {

            Purchase purchase = plan(current, product.getTier(), product.getPrice(), product.getDurationDays());

            product.setBuyable(purchase.buyable);
            product.setPayAmount(purchase.amount);
            product.setNote(purchase.note);
        }

        return products;
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionResponse getMySubscription(int userId) {

        SubscriptionResponse subscription = subscriptionMapper.selectCurrentSubscription(userId);

        // 유효한 구독이 없으면 무료 등급
        return (subscription == null) ? SubscriptionResponse.basic() : subscription;
    }

    @Override
    @Transactional(readOnly = true)
    public String getCurrentTier(int userId) {
        return getMySubscription(userId).getTier();
    }

    @Override
    @Transactional
    public SubscriptionResponse pay(int userId, PaymentRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("상품을 선택해 주세요.");
        }

        // 금액과 기간은 화면에서 받지 않고 DB의 상품에서 읽는다 (금액 조작 방지)
        ProductVO product = subscriptionMapper.selectActiveProduct(request.getProductId());

        if (product == null) {
            throw new IllegalArgumentException("판매 중인 상품이 아닙니다.");
        }

        SubscriptionResponse current = getMySubscription(userId);

        Purchase purchase = plan(current, product.getTier(), product.getPrice(), product.getDurationDays());

        if (!purchase.buyable) {
            throw new IllegalArgumentException(purchase.note);
        }

        // 업그레이드는 남은 기간을 새 등급으로 옮기므로 옛 구독을 여기서 끝낸다
        if (purchase.endCurrent) {
            subscriptionMapper.updateSubscriptionEnded(userId);
        }

        PaymentVO payment = new PaymentVO();
        payment.setUserId(userId);
        payment.setProductId(product.getId());
        payment.setProductName(product.getName());
        payment.setAmount(purchase.amount);

        subscriptionMapper.insertPayment(payment);   // selectKey로 payment.id가 채워진다

        // 결제와 같은 트랜잭션이라 결제만 남고 구독이 없는 상태는 생기지 않는다
        SubscriptionVO subscription = new SubscriptionVO();
        subscription.setUserId(userId);
        subscription.setProductId(product.getId());
        subscription.setPaymentId(payment.getId());
        subscription.setStartDate(purchase.startDate);
        subscription.setEndDate(purchase.endDate);

        subscriptionMapper.insertSubscription(subscription);

        log.info("구독 결제 - userId: " + userId
                + ", product: " + product.getName()
                + ", amount: " + purchase.amount
                + ", 기간: " + purchase.startDate + " ~ " + purchase.endDate);

        return getMySubscription(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentHistoryResponse> getPaymentHistory(int userId) {
        return subscriptionMapper.selectPaymentHistory(userId);
    }


    /* ================================================================
       구매 계산 (상품 목록 표시와 실제 결제가 같은 규칙을 쓴다)
       ================================================================ */

    private Purchase plan(SubscriptionResponse current, String productTier, int price, int durationDays) {

        SubscriptionTier currentTier = SubscriptionTier.of(current.getTier());
        SubscriptionTier newTier = SubscriptionTier.of(productTier);

        Date now = new Date();

        // 1. 구독이 없으면 오늘부터 상품 기간만큼, 정가
        if (current.getEndDate() == null) {
            return Purchase.ok(price, now, plusDays(now, durationDays), false, null);
        }

        // 2. 하위 등급은 이용이 끝난 뒤에
        if (newTier.isLowerThan(currentTier)) {
            return Purchase.no(currentTier.getDisplayName() + " 이용이 끝난 뒤에 구매할 수 있습니다.");
        }

        // 3. 같은 등급이면 남은 기간에 이어 붙이고 정가
        if (newTier == currentTier) {
            return Purchase.ok(
                    price,
                    current.getEndDate(),
                    plusDays(current.getEndDate(), durationDays),
                    false,
                    "지금 구독이 끝난 뒤부터 " + durationDays + "일 더 이용합니다.");
        }

        // 4. 업그레이드: 같은 기간 상품만 (골드 1개월 → 플래티넘 1개월)
        if (durationDays != current.getDurationDays()) {
            return Purchase.no("이용 중인 상품과 같은 기간의 상품으로만 올릴 수 있습니다.");
        }

        // 남은 기간만큼의 하루 값 차이를 받는다 (남은 일수는 올림)
        int remainingDays = remainingDays(current.getEndDate(), now);

        double currentDaily = (double) current.getPrice() / current.getDurationDays();
        double newDaily = (double) price / durationDays;

        int amount = (int) Math.max(0, Math.round((newDaily - currentDaily) * remainingDays));

        return Purchase.ok(
                amount,
                now,
                current.getEndDate(),   // 종료일은 그대로, 등급만 올린다
                true,
                "남은 " + remainingDays + "일을 " + newTier.getDisplayName() + "으로 바꿉니다.");
    }

    /** 남은 일수 (하루가 안 남아도 1일로 쳐 준다) */
    private int remainingDays(Date endDate, Date now) {

        long millis = endDate.getTime() - now.getTime();

        if (millis <= 0) {
            return 0;
        }

        return (int) Math.ceil((double) millis / DAY_MILLIS);
    }

    private Date plusDays(Date from, int days) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(from);
        calendar.add(Calendar.DAY_OF_MONTH, days);

        return calendar.getTime();
    }


    /** 이 회원이 이 상품을 어떻게 사게 되는지 */
    private static class Purchase {

        private boolean buyable;
        private int amount;
        private Date startDate;
        private Date endDate;
        private boolean endCurrent;   // 기존 구독을 끝내야 하는지 (업그레이드)
        private String note;

        static Purchase ok(int amount, Date startDate, Date endDate, boolean endCurrent, String note) {

            Purchase purchase = new Purchase();
            purchase.buyable = true;
            purchase.amount = amount;
            purchase.startDate = startDate;
            purchase.endDate = endDate;
            purchase.endCurrent = endCurrent;
            purchase.note = note;

            return purchase;
        }

        static Purchase no(String note) {

            Purchase purchase = new Purchase();
            purchase.buyable = false;
            purchase.note = note;

            return purchase;
        }
    }
}
