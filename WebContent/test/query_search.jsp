<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<script>
	function checkValue() {
		var x = document.getElementsByName("PAGE")[0].value;
		
		if ((x == null || x == "") || (x > 5 || x < 1)) {
			alert("invalid input - page, value range : [1 < page <= 5]");
			return false;
		}
	}
</script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Query Search Test</title>
</head>
<body>
	<h1>Query Search Test - 등록한 북마크를 검색할 수 있습니다.</h1>
	<form action="http://114.70.192.40:8080/SearchEngine/searchAction.sf"
		method="get" name="searchForm" >
		Query : <input type="text" name="QUERY"> Page : <input
			type="text" name="PAGE"> <input type="submit" value="전송" onclick="return checkValue()">
	</form>
</body>
</html>