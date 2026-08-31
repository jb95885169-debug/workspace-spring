<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>    
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="shortcut icon" href="#">
<link rel="stylesheet" href="/resources/css/header.css">
<title>MemberBoard</title>
</head>
<body>
	<div class="wrap">
		<div class="header">
			<div class="header-title">
				<a href="mainPage">MemberBoard</a>
			</div>
			<div class="header-member">
				<button type="button" class="header-btn" onclick="loginPage()">로그인</button>
				<button type="button" class="header-btn" onclick="joinPage()">회원가입</button>
			</div>
			<div class="menu">
				<a href="boardList">게시판</a>
			</div>
		</div>
		<div class="main">