package com.mingle.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mingle.dto.ReportCreateRequest;
import com.mingle.mapper.ReportMapper;
import com.mingle.vo.ReportVO;

@Service
public class ReportService {

    private static final int TITLE_MAX_LENGTH = 100;
    private static final int CONTENT_MAX_LENGTH = 1000;

    @Autowired
    private ReportMapper reportMapper;

    @Transactional
    public void createReport(long reporterId, ReportCreateRequest request) {
        if (request == null || request.getTargetUserId() <= 0) {
            throw new IllegalArgumentException("신고 대상이 올바르지 않습니다.");
        }
        if (isBlank(request.getReason()) || isBlank(request.getTitle()) || isBlank(request.getContent())) {
            throw new IllegalArgumentException("신고 사유, 제목, 내용을 모두 입력해 주세요.");
        }
        if (request.getTitle().trim().length() > TITLE_MAX_LENGTH
                || request.getContent().trim().length() > CONTENT_MAX_LENGTH) {
            throw new IllegalArgumentException("신고 제목은 100자, 내용은 1000자 이내로 입력해 주세요.");
        }

        ReportVO report = new ReportVO();
        report.setReporterId(reporterId);
        report.setTargetUserId(request.getTargetUserId());
        report.setReason(request.getReason().trim());
        report.setTitle(request.getTitle().trim());
        report.setContent(request.getContent().trim());

        reportMapper.insertReport(report);
    }

    @Transactional(readOnly = true)
    public List<ReportVO> getMyReports(long reporterId) {
        return reportMapper.selectReportsByReporter(reporterId);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}