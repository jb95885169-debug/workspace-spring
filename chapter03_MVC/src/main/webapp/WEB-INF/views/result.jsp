<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<div>
		이름 : ${sDto.name } <br>
		학과 : ${sDto.dept } <br>
		학년 : ${sDto.gradeNo } <br>
		반 : ${sDto.classNo } <br>
	</div>
	<hr>
	<div>
		이름 : ${s.name } <br>
		학과 : ${s.dept } <br>
		학년 : ${s.gradeNo } <br>
		반 : ${s.classNo } <br>
	</div>
</body>
</html>