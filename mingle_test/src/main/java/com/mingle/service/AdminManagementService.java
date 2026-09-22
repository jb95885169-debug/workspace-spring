package com.mingle.service;

import java.util.List;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mingle.dto.AdminOverviewResponse;
import com.mingle.mapper.ReportMapper;
import com.mingle.mapper.SupportTicketMapper;
import com.mingle.mapper.UserMapper;
import com.mingle.vo.ReportVO;
import com.mingle.vo.SupportTicketVO;

@Service
public class AdminManagementService {

    @Autowired private UserMapper userMapper;
    @Autowired private SupportTicketMapper supportTicketMapper;
    @Autowired private ReportMapper reportMapper;
    @Autowired private AdminSubscriptionService adminSubscriptionService;

    @Transactional(readOnly = true)
    public AdminOverviewResponse getOverview() {
        return new AdminOverviewResponse(
                userMapper.selectUsers(),
                supportTicketMapper.selectTicketsForAdmin(),
                reportMapper.selectReportsForAdmin(),
                adminSubscriptionService.getProducts());
    }

    @Transactional
    public void answerTicket(long ticketId, String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            throw new IllegalArgumentException("문의 답변을 입력해 주세요.");
        }
        if (supportTicketMapper.answerTicket(ticketId, answer.trim()) == 0) {
            throw new IllegalArgumentException("문의가 없거나 이미 처리되었습니다.");
        }
    }

    @Transactional
    public void updateReport(long reportId, String status, Integer suspensionDays) {
        if (status == null || !("RESOLVED".equals(status) || "REJECTED".equals(status))) {
            throw new IllegalArgumentException("신고 처리 상태가 올바르지 않습니다.");
        }

        Date processedAt = new Date();
        if ("RESOLVED".equals(status)) {
            if (suspensionDays == null || suspensionDays < 1 || suspensionDays > 3650) {
                throw new IllegalArgumentException("정지 기간은 1일부터 3650일까지 입력해 주세요.");
            }

            ReportVO report = reportMapper.selectReportById(reportId);
            if (report == null) {
                throw new IllegalArgumentException("신고를 찾을 수 없습니다.");
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(processedAt);
            calendar.add(Calendar.DATE, suspensionDays);
            if (userMapper.suspendUser(report.getTargetUserId(), calendar.getTime()) == 0) {
                throw new IllegalArgumentException("정지할 회원을 찾을 수 없습니다.");
            }
        }

        if (reportMapper.updateReportStatus(reportId, status, processedAt) == 0) {
            throw new IllegalArgumentException("신고를 찾을 수 없습니다.");
        }
    }
}