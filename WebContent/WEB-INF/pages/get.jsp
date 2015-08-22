<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>来吧！匿名吐槽</title>
</head>
<body background="images/littleSheep.jpg">
	<div
		style="width: 1024px; margin: 25px auto 25px auto; padding: 2px; border: solid 1px #7777AA;">
		<form action="post" method="post"
			style="border: solid 1px black; padding: 5px; text-align: center; margin: 5px; ">
			<label for="data">消息:</label> <input id="data" name="data"
				type="text" style="width: 900px; background: blue;"> <input type="submit"
				value="发送" />
		</form>
		<script type="text/javascript">
			document.getElementById('data').focus();
		</script>
		<%
			@SuppressWarnings("unchecked")
			List<Map<String, String>> data = (List<Map<String, String>>) request.getAttribute("data");
			for (Map<String, String> entry : data) {
		%>
		<div
			style="border: solid 1px black; padding: 5px; text-align: center; margin: 5px; word-wrap: break-word;"><%=entry.get("data")%></div>
		<%
			}
		%>
	</div>
</body>
</html>
