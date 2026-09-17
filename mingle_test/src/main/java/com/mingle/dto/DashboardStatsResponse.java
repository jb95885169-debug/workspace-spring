package com.mingle.dto;

import lombok.Data;

@Data
public class DashboardStatsResponse {

    /** 오늘 가입한 신규 회원 수 */
    private long newUsersToday;

    /** 미처리된 1:1 문의 건수 */
    private long pendingTickets;

    /** 대기 중인 신고 건수 */
    private long pendingReports;

    /** 오늘 총 매출액 */
    private long todaySalesAmount;

    /** 오늘 결제 건수 */
    private long todayPaymentCount;

    /** 이번 달 총 매출액 */
    private long monthSalesAmount;

    /** 이번 달 결제 건수 */
    private long monthPaymentCount;
}
