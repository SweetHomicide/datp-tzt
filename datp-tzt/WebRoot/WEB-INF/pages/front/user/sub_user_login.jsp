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
<link rel="stylesheet" href="static/front/css/user/login.css" type="text/css"></link>
<style type="text/css">
.login-body-full {
	background: url("${requestScope.constant['logoImages'] }") no-repeat 50% 0;
	height: 696px;
	padding-top: 50px;
}
</style>
</head>
<body >


<%@include file="../comm/header.jsp" %>


	<div class="container-full login-body-full">
		<div class="container ">
			<div class="col-xs-12 padding-top-30">
				<div class="col-xs-4 login col-xs-offset-7 padding-left-clear padding-right-clear" style="margin-left: 70.333%;">
					<div class="login-header"></div>
					<div class="login-bg"></div>
					<!-- <div class="login-other-bg"></div> -->
					<div class="login-body">
						<span class="login-title text-center">登录${requestScope.constant['webName']}</span>
						<div class="form-group ">
							<input id="login-account" class="form-control login-ipt" type="text" placeholder='输入邮件或手机' />
						</div>
						<div class="form-group ">
							<input id="login-password" class="form-control login-ipt" type="password" placeholder='输入密码' />
						</div>
						<div class="form-group ">
							<span id="login-errortips" class="text-danger"></span>
						</div>
						<div class="form-group ">
							<button id="login-submit" class="btn btn-primary btn-block btn-login">登录</button>
						</div>
						<div class="form-group">
							<a href="validate/reset.html">忘记密码？</a>
							<a href="user/register.html" class="pull-right">注册</a>
						</div>
					</div>
					<!-- <div class="login-other">
						<a href="/link/qq/call.html">QQ登录</a>
						<a href="javascript:weixin();" class="pull-right">微信登录</a>
					</div> -->
				</div>
			</div>
		</div>
	</div>
	<input id="forwardUrl" type="hidden" value="${forwardUrl }">


<%@include file="../comm/footer.jsp" %>	

	<script type="text/javascript" src="static/front/js/user/login.js"></script>
</body>
</html>

