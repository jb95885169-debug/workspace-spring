package com.mingle.restcontroller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mingle.dto.AdminOverviewResponse;
import com.mingle.service.AdminManagementService;
import com.mingle.service.UserService;

@RestController
@RequestMapping("/api/admin/management")
public class AdminManagementRestController {

    @Autowired private AdminManagementService adminManagementService;
    @Autowired private UserService userService;

    @GetMapping
    public ResponseEntity<AdminOverviewResponse> overview(HttpServletRequest request) {
        if (!isAdmin(request)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(adminManagementService.getOverview());
    }

    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<Void> updateUserStatus(@PathVariable int userId,
            @RequestBody StatusRequest body, HttpServletRequest request) {
        if (!isAdmin(request)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        userService.updateUserStatus(userId, body.status);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/tickets/{ticketId}")
    public ResponseEntity<Void> answerTicket(@PathVariable long ticketId,
            @RequestBody AnswerRequest body, HttpServletRequest request) {
        if (!isAdmin(request)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        adminManagementService.answerTicket(ticketId, body.answer);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/reports/{reportId}")
    public ResponseEntity<Void> updateReport(@PathVariable long reportId,
            @RequestBody StatusRequest body, HttpServletRequest request) {
        if (!isAdmin(request)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        adminManagementService.updateReport(reportId, body.status);
        return ResponseEntity.noContent().build();
    }

    private boolean isAdmin(HttpServletRequest request) {
        return request.isUserInRole("ADMIN");
    }

    public static class StatusRequest { public String status; }
    public static class AnswerRequest { public String answer; }
}