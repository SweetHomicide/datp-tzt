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
<link rel="stylesheet" href="/static/front/css/user/login.css" type="text/css"></link>
<style type="text/css">
.login-body-full {
	background: url("${requestScope.constant['logoImages'] }") no-repeat;
	left:0px;
	height: 650px;
	padding-top: 50px;
	
	
}
</style>
</head>
<body >
<%@include file="../comm/header.jsp" %>
	<div class="container-full" style="background:#fff">
		<div class="container ">
			<div class="col-xs-12 padding-top-30">
				<div class="col-xs-7 login-body-full" ></div>
				<div class="col-xs-5 login1 land_right register col-xs-offset-7 padding-left-clear padding-right-clear" style="margin-left: 70.333%;">
					<div class="login-header">
						<div class="register-tab clearfix">
							<span data-show="register-phone" data-hide="register-email" data-type="0" class="register-item active">
								<i class="register-icon icon-phone"></i>
								<span>手机注册</span>
								<i class="icon-arrow"></i>
							</span>
							<span data-show="register-email" data-hide="register-phone" data-type="1" class="register-item">
								<i class="register-icon icon-email"></i>
								<span>邮箱注册</span>
								<i class="icon-arrow"></i>
							</span>
						</div>
					</div>
					<div class="login-body ">
						<div class="login-body-content">
						<!-- <div class="form-group register-phone" style="display:none">
							<select class="form-control login-ipt" id="register-areaCode">
								<option value="86">中国大陆(China)</option>
							</select>
						</div> -->
						<div class="form-group register-phone">
							<span id="register-phone-areacode" class="btn btn-areacode register-areacode" style="display:none">+86</span>
							<input id="register-phone" class="form-control login-ipt" type="text" placeholder="手机号码" required="required">
						</div>
						<div class="form-group register-email">
							<input id="register-email" class="form-control login-ipt" type="text" placeholder="邮箱地址" required="required">
						</div>
						<div class="form-group ">
							<input id="register-imgcode" class="form-control login-ipt" type="text" placeholder="验证码" required="required">
							<img class="btn btn-imgcode register-imgmsg" src="/servlet/ValidateImageServlet?r=<%=new java.util.Date().getTime() %>"></img>
						</div>
						<div class="form-group register-phone">
							<input id="register-msgcode" class="form-control login-ipt" type="text" placeholder="短信验证码" required="required">
							<button id="register-sendmessage" data-msgtype="12" data-tipsid="register-errortips" class="btn btn-sendmsg register-msg">发送验证码</button>
						</div>						
						<div class="form-group register-email">
							<input id="register-email-code" class="form-control login-ipt" type="text" placeholder="邮箱验证码" required="required">
							<button id="register-sendemail" data-msgtype="3" data-tipsid="register-errortips" class="btn btn-sendemailcode register-msg">发送验证码</button>
						</div>
						<div class="form-group ">
							<input id="register-password" class="form-control login-ipt" type="password" placeholder="密码" required="required">
						</div>
						<div class="form-group ">
							<input id="register-confirmpassword" class="form-control login-ipt" type="password" placeholder="确认密码" required="required">
						</div>
						<div class="form-group ">
							<input id="register-intro" class="form-control login-ipt" type="text" value="${intro }" placeholder="邀请码(${requestScope.constant['isMustIntrol']==1?'必填':'选填'})" value="${intro }">
						</div>
						<div class="checkbox">
							<label>
								<input id="agree" type="checkbox">
								阅读并同意
								<a target="_blank" href="/about/index.html?id=4" class="">《${requestScope.constant['webName']}用户协议》</a>
							</label>
						</div>
						<div class="form-group ">
							<span id="register-errortips" class="text-danger"></span>
						</div>
						<div class="form-group ">
							<button id="register-submit" class="btn btn-primary btn-block btn-login">注册</button>
						</div>
						<div class="form-group">
							<span class="text-white">已有帐号？</span>
							<a href="/">直接登录>></a>
						</div>
					</div>
					</div>
				</div>
			</div>
		</div>
	</div>

<%@include file="../comm/footer.jsp" %>	

	<input id="regType" type="hidden" value="0">
	<input id="intro_user" type="hidden" value="${intro }">
	<script type="text/javascript" src="/static/front/js/comm/msg.js"></script>
	<script type="text/javascript" src="/static/front/js/user/register.js"></script>
</body>
</html>

