<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix = "sec" uri = "http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h1>all.jsp</h1>
	
	<!-- "/sample/all" 
		
	-->
	
	<!-- 익명 사용자의 경우 (로그인을 하지 않은 경우도 해당) -->
	<sec:authorize access="isAnonymous()" >
		<a href="/customLogin">Login</a>
	</sec:authorize>
	
	
	<!-- 인증된 사용자 -->
	<sec:authorize access="isAuthenticated()" >
		<a href="/customLogout">Logout</a>
	</sec:authorize>	
	
	
	<!-- 
		표현식
		- hasRole([role])				: 해당 권한이 있으면 true
		  hasAuthority([authority])
		- hasAnyRole([role])			: 여러 권한들 중에 하나라도 있으면 true
		  hasAnyAuthority([authority])
		- principal : 현재 사용자 정보를 의미
		- permitAll : 모든 사용자에게 허용
		- denyAll : 모든 사용자에게 거부
		- isAnonymous() : 익명 사용자의 경우
		- isAuthenticated() : 인증된 사용자라면 true
		- idFullyAuthenticated() : Rememer-me 로 인증된 것이 아닌  사용자의 경우 true
		


	 -->
	
	
</body>
</html>





