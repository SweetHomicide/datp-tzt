<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../comm/include.inc.jsp"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path;
%>

<!doctype html>
<html>
<head> 
<base href="<%=basePath%>"/>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<%@include file="../comm/link.inc.jsp" %>

<link rel="stylesheet" href="/static/front/css/user/reset.css" type="text/css"></link>
</head>
<body>

<%@include file="../comm/header.jsp" %>

	<div class="container-full padding-top-30">
		<div class="container text-center validate">
			<div class="col-xs-12 clearfix padding-clear padding-top-30 text-center">
				<div class="validate-online">
					
						<c:if test="${validate == false }">
							<span class="validate-success failure">
								<span class="validate-text failure">激活失败， 该链接已失效 </span>
							</span>
						</c:if>
						<c:if test="${validate == true }">
						<span class="validate-success">
								<span class="validate-text">帐号激活成功</span>
							</span>
						</c:if>	
					
					<div class="form-group">
						<a class="btn btn-danger btn-block" href="/index.html">返回首页</a>
					</div>
					<div class="form-group">
						<a class="btn btn-danger btn-block" href="/">去登陆</a>
					</div>
				</div>
			</div>
		</div>
	</div>
	

<%@include file="../comm/footer.jsp" %>	



</body>
</html>
