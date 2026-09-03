package org.joonzis.aop;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j;

@Log4j
@Component
@Aspect
public class LogAdvice {
	// @Aspect : 해당 클래스의 객체에서 Aspect를 구현한 것을 나타냄
	// @Component : AOP자체와는 관계없지만 스프링에서 빈으로 인식하기 위해서 사용
	// => xml 대신 java방식
	
	@Before("execution(* org.joonzis.service.SampleService*.*(..))")// <이곳에 있는 모든 메소드가 실행 되기전에 실행되는 메소드
	public void logBefore() {
		log.info("================");
	}
	
	@Before("execution(* org.joonzis.service." + 
			"SampleService*.doAdd(String, String)) && " + 
			"args(str1, str2)"
	)
	public void logBeforeWithParam(String str1, String str2) {
		log.info("str1 : " + str1);
		log.info("str2 : " + str2);
	}
	
	
	@AfterThrowing(
			pointcut = "execution(* org.joonzis.service.SampleService*.*(..))",
			throwing = "exception"
			)
	public void logException(Exception exception) {
		log.info("Exception!!");
		log.info("exception : " + exception);
	}	
	
	
	
}
