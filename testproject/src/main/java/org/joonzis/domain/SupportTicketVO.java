package org.joonzis.domain;






import java.util.Date;

import lombok.Data;

@Data
public class SupportTicketVO {
    private Long id;
    private Long userId;
    private String category;
    private String title;
    private String content;
    private String status;
    private Date createdAt;
    private Date updatedAt;
}
