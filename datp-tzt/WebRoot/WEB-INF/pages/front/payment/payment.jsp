<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../comm/include.inc.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<%@include file="../comm/link.inc.jsp"%>
<title>Insert title here</title>
</head>
<body>
	<form id = "pay_form" action="${requestFrontUrl}" method="post">
		<c:forEach items="${reqData}" var="li">
			<input name="${li.key}" value="${li.value}" hidden="hidden"/>
		</c:forEach>
	</form>
		
	<script type="text/javascript">
		document.all.pay_form.submit();
	</script>	
</body>
</html>