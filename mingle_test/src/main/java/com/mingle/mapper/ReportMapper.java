package com.mingle.mapper;

import org.apache.ibatis.annotations.Param;

import com.mingle.vo.ReportVO;

public interface ReportMapper {

    /** 관리자 신고 처리 상태 변경 (PENDING / RESOLVED / REJECTED만 허용). */
    int updateReportStatus(
            @Param("reportId") long reportId,
            @Param("status") String status);

    /** 신고 처리 결과 확인용 조회. */
    ReportVO selectReportById(@Param("reportId") long reportId);
}
