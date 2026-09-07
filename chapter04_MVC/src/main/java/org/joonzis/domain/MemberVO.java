package org.joonzis.domain;

import java.sql.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberVO {
	private String userId;
	private String userPw;
	private String userName;
	private Date regDate;
	private Date updateDate;
	private boolean enabled;
	
	private List<AuthVO> authList;
	
}
