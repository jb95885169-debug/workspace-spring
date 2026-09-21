package com.mingle.restcontroller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mingle.dto.ReportCreateRequest;
import com.mingle.security.LoginUserId;
import com.mingle.service.ReportService;
import com.mingle.vo.ReportVO;

@RestController
@RequestMapping("/api/reports")
public class ReportRestController {

    @Autowired
    private ReportService reportService;

    @PostMapping
    public ResponseEntity<Void> createReport(
            @RequestBody ReportCreateRequest request,
            @LoginUserId int userId) {

        reportService.createReport(userId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/mine")
    public ResponseEntity<List<ReportVO>> getMyReports(@LoginUserId int userId) {
        return ResponseEntity.ok(reportService.getMyReports(userId));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
    }
}