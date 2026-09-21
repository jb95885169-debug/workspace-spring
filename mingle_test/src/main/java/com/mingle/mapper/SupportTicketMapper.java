package com.mingle.mapper;

import org.apache.ibatis.annotations.Param;
import java.util.List;

import com.mingle.vo.SupportTicketVO;

public interface SupportTicketMapper {

    List<SupportTicketVO> selectTicketsForAdmin();

    /** 관리자 답변 등록: PENDING 문의만 답변 완료(RESOLVED)로 전환한다. */
    int answerTicket(
            @Param("ticketId") long ticketId,
            @Param("answer") String answer);

    /** 답변 처리 결과 확인용 조회. */
    SupportTicketVO selectTicketById(@Param("ticketId") long ticketId);
}
