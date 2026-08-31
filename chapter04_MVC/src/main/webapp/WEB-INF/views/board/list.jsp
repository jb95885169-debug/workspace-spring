<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<jsp:include page="../layout/header.jsp"/>	
	<div>
		<h1 class="page-header">게시판</h1>
	</div>
	<div class="panel-heading">
		<button type="button" class="btn btn-fir" id="registerBtn">새 게시글 등록</button>
	</div>
	<div class="panel-body">
		<table>
			<thead>
				<tr>
					<th>글번호</th>
					<th>제목</th>
					<th>작성자</th>
					<th>작성일</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="vo" items="${list }">
					<tr>
						<td>${vo.bno }</td>
						<td><a href = "${vo.bno }" >${vo.title }</a></td>
						<td>${vo.writer }</td>
						<td>${vo.regdate }</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<!-- page -->
      <div class="page-wrap">
         <ul class="page-nation">
            <c:if test="${pageMaker.prev }">
               <li class="previous">
                  <a href="${pageMaker.startPage-1 }"> &lt; </a>
               </li>
            </c:if>
            <c:forEach var="num" begin="${pageMaker.startPage }" end="${pageMaker.endPage }" step="1">
               <li>
                  <a href="${num }" class="${pageMaker.cri.pageNum == num ? 'active' : '' }"> ${num } </a>
               </li>
            </c:forEach>
            <c:if test="${pageMaker.next }">
               <li><a href="${pageMaker.endPage+1 }"> &gt; </a></li>
            </c:if>
         </ul>
       </div>
		
		
		
	</div>
	
	<jsp:include page="../layout/footer.jsp"/>	
	<script type="text/javascript" src="/resources/js/boardList.js"></script>
</body>
</html>