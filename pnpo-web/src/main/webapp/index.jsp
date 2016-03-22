<%@page import="org.pnpo.PNPO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html ng-app="index">
<head>
<title>来吧！匿名吐槽</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<link rel="stylesheet" href="css/bootstrap.min.css">
<link rel="stylesheet" href="css/bootstrap-theme.min.css">

<link rel="stylesheet" href="app/index/index.css">
</head>
<body>
	<nav class="navbar navbar-inverse navbar-fixed-top">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed"
					data-toggle="collapse" data-target="#navbar" aria-expanded="false"
					aria-controls="navbar">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="">PNPO</a>
			</div>
			<div id="navbar" class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<li class="active"><a href="">首页</a></li>
				</ul>
			</div>
			<!--/.nav-collapse -->
		</div>
	</nav>
	<div class="container">
		<div ng-view></div>
		<footer class="footer">
			<div style="display: block; float: left;">
				<a href="https://github.com/eagel/pnpo/wiki">PNPO Open Source
					Project</a>
			</div>
			<div style="display: block; float: right;">
				Version:&nbsp;
				<%=PNPO.VERSION%></div>
			<div style="clear: both;"></div>
		</footer>
	</div>

	<script src="js/jquery-2.1.1.min.js"></script>
	<script src="js/bootstrap.min.js"></script>

	<script src="js/angular.min.js"></script>
	<script src="js/angular-route.min.js"></script>

	<script src="app/index/index.js"></script>
</body>
</html>