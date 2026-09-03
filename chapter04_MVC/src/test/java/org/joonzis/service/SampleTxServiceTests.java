package org.joonzis.service;

import java.util.List;

import org.joonzis.domain.BoardVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.log4j.Log4j;



@Log4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
		"file:src/main/webapp/WEB-INF/spring/root-context.xml")
public class SampleTxServiceTests {
	
	@Autowired
	private SampleTxService service;
	
	@Test
	public void testLong() {
		String str = "Starry\r\n" + "Starry night\r\n" + "Paint your palette blue and grey\r\n"
	             + "Look out on a summer's day";
		
		log.info(str.getBytes().length);
		
		service.addDate(str);
	}
	
}





