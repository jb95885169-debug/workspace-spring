package org.joonzis.security;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import org.joonzis.service.BoardServiceTests;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.log4j.Log4j;

@Log4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
	"file:src/main/webapp/WEB-INF/spring/root-context.xml",
	"file:src/main/webapp/WEB-INF/spring/security-context.xml"
})
public class MemberTests {
	@Autowired
	private DataSource ds;
	
	@Autowired
	private PasswordEncoder pwencoder;
	
	/*
	@Test
	public void testInsertMember() {
		String sql = "insert into " +
					"tbl_member(userid, userpw, username) " +
					"values(?, ?, ?)";
		
		for(int i=0; i<100; i++) {
			
			Connection conn = null;
			PreparedStatement ps = null;
			
			try {
				conn = ds.getConnection();
				ps = conn.prepareStatement(sql);
				
				// 비밀번호 인코딩
				ps.setString(2, pwencoder.encode("pw" + i));
				
				// id/이름
				if(i<80) {
					ps.setString(1, "user"+i);
					ps.setString(3, "일반사용자"+i);
				}else if(i<90) {
					ps.setString(1, "manager"+i);
					ps.setString(3, "운영자"+i);
				}else {
					ps.setString(1, "admin"+i);
					ps.setString(3, "관리자"+i);
				}
				ps.executeUpdate();
				conn.commit();
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(ps!=null) ps.close();
					if(conn!=null) conn.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		} // end for loop
	} // end testInsertMember
	*/
	
	@Test
	public void testInsertMemberAuth() {
		String sql = "insert into " +
					"tbl_member_auth(userid, auth) " +
					"values(?, ?)";
		
		for(int i=0; i<100; i++) {
			
			Connection conn = null;
			PreparedStatement ps = null;
			
			try {
				conn = ds.getConnection();
				ps = conn.prepareStatement(sql);
				
				// id/이름
				if(i<80) {
					ps.setString(1, "user"+i);
					ps.setString(2, "ROLE_USER");
				}else if(i<90) {
					ps.setString(1, "manager"+i);
					ps.setString(2, "ROLE_MEMBER");
				}else {
					ps.setString(1, "admin"+i);
					ps.setString(2, "ROLE_ADMIN");
				}
				ps.executeUpdate();
				conn.commit();
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(ps!=null) ps.close();
					if(conn!=null) conn.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		} // end for loop
	} // end testInsertMember
	
}













