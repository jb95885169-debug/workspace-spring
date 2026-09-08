<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
<%@ taglib prefix = "sec" uri = "http://www.springframework.org/security/tags" %>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

	<jsp:include page="../layout/header.jsp"/>	
	
	<sec:authentication property="principal" var="pinfo"/>	<%-- 현재 로그인한 사용자의 정보를 jsp에서 사용할수 있도록 변수로 꺼내는 코드 --%>
	<%-- 게시글 수정, 댓글 수정&삭제버튼 여러버튼을 막는데 사용하기위해 전역변수로 뺌			var="pinfo"로 선언하지 않고 principal.username 등으로 사용가능 --%>
		
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
			
			<%-- ${pinfo.username}${vo.writer } --%>
			<%-- 이코드는 로그인 여부만 확인 --%>
			<sec:authorize access="isAuthenticated()">	
				<c:if test="${pinfo.username eq vo.writer}">	<%-- 이코드로 작성자와 로그인한 유저가 같은지 확인 --%>
					<button type="button" class="btn btn-sec" id="modifyBtn">수정</button>
				</c:if>
				
			

       		 
			</sec:authorize>
		
	<%-- 		<button type="button" class="btn btn-sec" id="modifyBtn">수정</button> --%>
			<button type="button" class="btn btn-fir" id="indexBtn">목록으로 이동</button>
		</div>
	</div>
	
	<div class="panel-footer">
		<div class="panel-footer-header">
			<div class="panel-footer-title">
				<a href="mainPage">댓글</a>
			</div>
			<div class="panel-footer-register">
				<sec:authorize access="isAuthenticated()">	
					<button type="button" class="btn btn-sec" id="replyBtn">댓글 달기</button>
				</sec:authorize>
			</div>
		</div>	
		<div class="panel-footer-body">
			<ul class="chat">
				<li data-rno="10">
					<div>
						<div class="chat-header">
							<strong>작성자</strong>
							<small class="pull-right">0000-00-00</small>
						</div>
						<p>내용</p>
					</div>
				</li>
			</ul>
		</div>
	</div>
	<!-- /panel footer -->
	
	<div class="file-container">
		<div class="file-header">
			<div class="file-title">
				<a>첨부 파일</a>
			</div>
		</div>
		<div class="file-body">
			<div class="uploadResult">
				<ul></ul>
			</div>
		</div>
	</div>

	<!-- 모달 -->
	<div id="modal">
		<div class="modal-content">
			<div class="modal-title">
				<a>게시글 댓글</a>
			</div>
			<hr>
			<div class="modal-body">
				<ul class="chat">
					<li>
						<div>
							<div>
								<span class="modal-font">댓글</span>
							</div>
							<p><input type="text" name="reply" ></p>
						</div>
					</li>
					<li>
						<div>
							<div>
								<span class="modal-font">작성자</span>
							</div>
							<p><input type="text" name="replyer"></p>
						</div>
					</li>
					<li>
						<div>
							<div>
								<span class="modal-font">등록 날짜</span>
							</div>
							<p><input type="text" name="replydate"></p>
						</div>
					</li>
				</ul>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-sec" id="addReplyBtn">등록</button>
				
				
			
					<sec:authorize access="isAuthenticated()">
						
							<button type="button" class="btn btn-thi" id="modifyReplyBtn">수정</button>
							<button type="button" class="btn btn-fou" id="removeReplyBtn">삭제</button>
						
					</sec:authorize>
				
				
				<button type="button" class="btn btn-fir" id="closeModalBtn">취소</button>
			</div>
		</div>
	</div>
	

	<jsp:include page="../layout/footer.jsp"/>
	<script type="text/javascript" src="/resources/js/reply.js"></script>	
	<script type="text/javascript" src="/resources/js/get.js"></script>	
	
	<sec:authorize access="isAuthenticated()">																				          
		<script>
			    const loginUser = '${pinfo.username}';
			    console.log("로그인 사용자:", loginUser);
		</script>
	</sec:authorize>()
	<%-- <sec:authorize access="isAuthenticated()">로 감싸서 로그인하지않은 상태로도  게시글을 볼수 있게 만듦 --%>


</body>
</html>