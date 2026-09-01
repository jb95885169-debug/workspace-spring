<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Home</title>
</head>
<body>
	<button onclick="send()">전송</button>
</body>
<script type="text/javascript">
	function send() {
		
		const obj = {
				tno : 1,
				owner : 'kim',
				grade : 'gold'
		};
		
		fetch('/test/ticket.json',{
			method : 'post',
			body : JSON.stringify(obj),	//제이슨의 데이터 타입은 문자열
			headers : {'Content-type' : 'application/json; charset=utf-8'}
		})
			.then(response => response.json())
			.then(result =>{
				console.log(result);
			})
			.catch(err => console.log(err));
	}

</script>
</html>
