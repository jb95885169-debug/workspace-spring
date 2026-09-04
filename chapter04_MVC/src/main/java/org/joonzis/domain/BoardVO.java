package org.joonzis.domain;

import java.sql.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class BoardVO {
	private int bno;
	private String title, content,writer;
	private Date regdate,updatedate;
	private int replycnt;
	
	private List<BoardAttachVO> attachList;
}
