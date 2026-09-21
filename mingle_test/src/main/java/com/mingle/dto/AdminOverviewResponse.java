package com.mingle.dto;

import java.util.List;

import com.mingle.vo.ProductVO;
import com.mingle.vo.ReportVO;
import com.mingle.vo.SupportTicketVO;
import com.mingle.vo.UserVO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminOverviewResponse {

    private List<UserVO> users;
    private List<SupportTicketVO> tickets;
    private List<ReportVO> reports;
    private List<ProductVO> products;
}