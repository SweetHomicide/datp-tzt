<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="comm/include.inc.jsp"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path;
%>

<!doctype html>
<html>
<head>
<base href="<%=basePath%>" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<%@include file="comm/link.inc.jsp"%>

<link rel="stylesheet" href="/static/front/css/zhongchou/zc.css" type="text/css"></link>
</head>
<body>



	<%@include file="comm/header.jsp"%>




	<div class="container-full padding-top-30">
		<div class="container">
		<div style="display:none;">
				
				
		<%@include file="comm/left_menu.jsp" %>
				</div>
			<div class="col-xs-10 padding-right-clear">
				<div class="col-xs-12 padding-right-clear padding-left-clear rightarea withdraw">
					<div class="col-xs-12 rightarea-con">
						<div class="crowd_bi clear ">
								<span class="crowd_bi_hlb">
								</span>
								<h2>
									提示
								</h2>
								<div class="crowd_hui crowd_hui_red">
									<em></em>${msg }
								</div>
						</div>
					</div>
				</div>
			</div>				
		</div>
	</div>



	<%@include file="comm/footer.jsp"%>


</body>
</html>