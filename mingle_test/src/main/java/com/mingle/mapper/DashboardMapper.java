package com.mingle.mapper;

import com.mingle.dto.DashboardStatsResponse;

public interface DashboardMapper {

    /** 관리자 대시보드 KPI 요약 */
    DashboardStatsResponse selectDashboardStats();
}
