<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h1>회원가입 페이지</h1>
	
	
	<form action="/joinMember" method="post">
	
		<div>
			이름 : <input type="text" name="userName">
		</div>
		<div>
			아이디 : <input type="text" name="userId">
		</div>
		<div>
			비밀번호 : <input type="password" name="userPw">			
		</div>
		
		<input type="submit" value="회원가입" >
	<%-- 	<input 
			type="hidden" 
			name="${_csrf.parameterName}"
			value="${_csrf.token }"> --%>
	</form>
</body>
</html>