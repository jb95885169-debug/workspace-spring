<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<jsp:include page="../layout/header.jsp"/>	
	
	<div class="page-header">
		<h1>게시글 화면</h1>
	</div>
	<div class="panel-body">
		<form method="POST">
			<table>
				<tbody>
					<tr>
						<th>글 번호</th>
						<td><input type="text" name="bno" value="${vo.bno}" readonly></td>
					</tr>
					<tr>
						<th>제목</th>
						<td><input type="text" name="title" value="${vo.title }" readonly></td>
					</tr>
					<tr>
						<th>작성자</th>
						<td><input type="text" name="writer" value="${vo.writer }" readonly></td>
					</tr>
					<tr>
						<th>내용</th>
						<td>
							<textarea rows="10" cols="76" name="content" readonly>${vo.content }</textarea>
						</td>
					</tr>
				</tbody>
			</table>
		</form>
		<div class="panel-body-btns">
			<button type="button" class="btn btn-sec" id="modifyBtn">수정</button>
			<button type="button" class="btn btn-fir" id="indexBtn">목록으로 이동</button>
		</div>
	</div>

	<jsp:include page="../layout/footer.jsp"/>
	<script type="text/javascript" src="/resources/js/get.js"></script>	
</body>
</html>